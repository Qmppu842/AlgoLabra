package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs

class DSV3_BrutalMinimaxProfileV3ByDeepseek : OpponentProfile() {
    override var depth: Int = 7 // Adjustable depth - higher = stronger but slower
    override val timeLimit: Long = 1000 // Increase time for deeper search
    override val name: String = "BrutalMinimax (depth=$depth)"

    // Precomputed winning positions for faster evaluation
    private val winningPatterns by lazy { precomputeWinningPatterns() }

    // Transposition table for caching board evaluations
    private val transpositionTable = mutableMapOf<Long, TranspositionEntry>()

    // Killer moves heuristic - moves that caused beta cutoff
    private val killerMoves = mutableMapOf<Int, MutableList<Int>>()

    // History heuristic - successful moves at each depth
    private val historyHeuristic = mutableMapOf<Int, MutableMap<Int, Int>>()

    data class TranspositionEntry(
        val value: Int,
        val depth: Int,
        val flag: Int, // 0 = exact, 1 = lower bound, 2 = upper bound
        val move: Int,
    )

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        // Clear transposition table for new search
        transpositionTable.clear()

        val legalMoves = board.getLegalMoves()
        if (legalMoves.isEmpty()) return -1

        // Check for immediate win
        for (move in legalMoves) {
            val newBoard = simulateMove(board, move, forSide)
            if (newBoard.isLastPlayWinning(board.boardConfig.neededForWin)) {
                return move
            }
        }

        // Check for opponent's immediate win
        for (move in legalMoves) {
            val newBoard = simulateMove(board, move, -forSide)
            if (newBoard.isLastPlayWinning(board.boardConfig.neededForWin)) {
                return move // Block opponent's win
            }
        }

        // Order moves: center first, then captures, then others
        val orderedMoves = orderMoves(board, legalMoves, forSide)

        var bestMove = orderedMoves.first()
        var bestValue = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        // Iterative deepening with time management
        val startTime = System.currentTimeMillis()
        var currentDepth = 3 // Start with shallow search

        while (currentDepth <= depth && System.currentTimeMillis() - startTime < timeLimit * 0.8) {
            var currentBestMove = orderedMoves.first()
            var currentBestValue = Int.MIN_VALUE

            for (move in orderedMoves) {
                val newBoard = simulateMove(board, move, forSide)
                val value =
                    -alphaBeta(
                        newBoard,
                        currentDepth - 1,
                        -beta,
                        -alpha,
                        -forSide,
                        startTime,
                    )

                if (value > currentBestValue) {
                    currentBestValue = value
                    currentBestMove = move
                }

                alpha = maxOf(alpha, value)
                if (alpha >= beta) {
                    break // Beta cutoff
                }
            }

            if (System.currentTimeMillis() - startTime < timeLimit * 0.8) {
                bestMove = currentBestMove
                bestValue = currentBestValue
                currentDepth++
            }
        }

        // Fallback if something went wrong
        return if (bestMove in legalMoves) bestMove else legalMoves.random(rand)
    }

    private fun alphaBeta(
        board: Board,
        depth: Int,
        alpha: Int,
        beta: Int,
        player: Int,
        startTime: Long,
    ): Int {
        // Time management
        if (System.currentTimeMillis() - startTime > timeLimit) {
            return evaluateBoard(board, player)
        }

        val originalAlpha = alpha
        val hash = board.hashCode().toLong()

        // Check transposition table
        transpositionTable[hash]?.let { entry ->
            if (entry.depth >= depth) {
                when (entry.flag) {
                    0 -> return entry.value
                    1 -> if (entry.value >= beta) return entry.value
                    2 -> if (entry.value <= alpha) return entry.value
                }
            }
        }

        // Terminal node evaluation
        if (depth == 0 || board.isAtMaxSize()) {
            return evaluateBoard(board, player)
        }

        // Check for immediate win/loss
        if (board.isLastPlayWinning(board.boardConfig.neededForWin)) {
            return if (player == 1) Int.MIN_VALUE + 1 else Int.MAX_VALUE - 1
        }

        val legalMoves = board.getLegalMoves()
        if (legalMoves.isEmpty()) {
            return evaluateBoard(board, player)
        }

        // Order moves for better pruning
        val orderedMoves = orderMoves(board, legalMoves, player)

        var currentAlpha = alpha
        var bestValue = Int.MIN_VALUE
        var bestMove = orderedMoves.first()

        for (move in orderedMoves) {
            val newBoard = simulateMove(board, move, player)
            val value = -alphaBeta(newBoard, depth - 1, -beta, -currentAlpha, -player, startTime)

            if (value > bestValue) {
                bestValue = value
                bestMove = move
            }

            currentAlpha = maxOf(currentAlpha, value)
            if (currentAlpha >= beta) {
                // Beta cutoff - store killer move
                val killers = killerMoves.getOrPut(depth) { mutableListOf() }
                if (move !in killers) {
                    killers.add(move)
                    if (killers.size > 2) killers.removeAt(0)
                }
                // Update history heuristic
                val history = historyHeuristic.getOrPut(depth) { mutableMapOf() }
                history[move] = history.getOrDefault(move, 0) + 1

                break
            }
        }

        // Store in transposition table
        val flag =
            when {
                bestValue <= originalAlpha -> 2

                // Upper bound
                bestValue >= beta -> 1

                // Lower bound
                else -> 0 // Exact
            }

        transpositionTable[hash] = TranspositionEntry(bestValue, depth, flag, bestMove)

        return bestValue
    }

    private fun orderMoves(
        board: Board,
        moves: List<Int>,
        player: Int,
    ): List<Int> {
        val moveScores = mutableMapOf<Int, Int>()

        for (move in moves) {
            var score = 0

            // 1. Center preference (higher score for center columns)
            val center = board.getWells() / 2
            score += 10 - abs(move - center)

            // 2. Check for immediate win
            val tempBoard = simulateMove(board, move, player)
            if (tempBoard.isLastPlayWinning(board.boardConfig.neededForWin)) {
                score += 10000
            }

            // 3. Check for opponent's immediate win on next move
            val opponentTempBoard = simulateMove(board, move, -player)
            var opponentCanWin = false
            for (oppMove in opponentTempBoard.getLegalMoves()) {
                val oppBoard = simulateMove(opponentTempBoard, oppMove, -player)
                if (oppBoard.isLastPlayWinning(board.boardConfig.neededForWin)) {
                    opponentCanWin = true
                    break
                }
            }
            if (opponentCanWin) {
                score -= 5000
            }

            // 4. Killer moves
            for (depth in killerMoves.keys) {
                if (move in killerMoves[depth]!!) {
                    score += 50
                }
            }

            // 5. History heuristic
            for (depth in historyHeuristic.keys) {
                score += historyHeuristic[depth]?.get(move) ?: 0
            }

            // 6. Potential for creating multiple threats
            score += evaluateMovePotential(board, move, player)

            moveScores[move] = score
        }

        return moves.sortedByDescending { moveScores[it] }
    }

    private fun evaluateMovePotential(
        board: Board,
        move: Int,
        player: Int,
    ): Int {
        val row = board.getHighestSpaceIndex(move)
        if (row < 0) return 0

        var potential = 0
        val needed = board.boardConfig.neededForWin

        // Check all directions for potential threats
        for (way in Way.half) {
            var count = 1 // Current cell
            var emptySpaces = 0

            // Check positive direction
            for (i in 1 until needed) {
                val newX = move + way.x * i
                val newY = row + way.y * i
                if (newX in 0 until board.getWells() && newY in 0 until board.boardConfig.height) {
                    val cell = board.board[newX][newY]
                    if (cell == player) {
                        count++
                    } else if (cell == 0 && board.board[newX][0] == 0) {
                        emptySpaces++
                    } else {
                        break
                    }
                } else {
                    break
                }
            }

            // Check negative direction
            val opposite = Way.opp[way.ordinal]
            for (i in 1 until needed) {
                val newX = move + opposite.x * i
                val newY = row + opposite.y * i
                if (newX in 0 until board.getWells() && newY in 0 until board.boardConfig.height) {
                    val cell = board.board[newX][newY]
                    if (cell == player) {
                        count++
                    } else if (cell == 0 && board.board[newX][0] == 0) {
                        emptySpaces++
                    } else {
                        break
                    }
                } else {
                    break
                }
            }

            // Add potential based on how close we are to a win
            if (count >= needed - 1 && emptySpaces > 0) {
                potential += 100
            } else if (count >= needed - 2 && emptySpaces > 1) {
                potential += 50
            }
        }

        return potential
    }

    private fun evaluateBoard(
        board: Board,
        player: Int,
    ): Int {
        var score = 0

        // Immediate win/loss check
        if (board.isLastPlayWinning(board.boardConfig.neededForWin)) {
            return if (player == 1) Int.MIN_VALUE + 1 else Int.MAX_VALUE - 1
        }

        // Center control
        val center = board.getWells() / 2
        for (col in center - 1..center + 1) {
            if (col in 0 until board.getWells()) {
                for (row in 0 until board.boardConfig.height) {
                    val cell = board.board[col][row]
                    if (cell == player) {
                        score += 3
                    } else if (cell == -player) {
                        score -= 3
                    }
                }
            }
        }

        // Evaluate all possible winning lines
        val needed = board.boardConfig.neededForWin

        // Check horizontal
        for (row in 0 until board.boardConfig.height) {
            for (col in 0..board.getWells() - needed) {
                score += evaluateLine(board, col, row, 1, 0, player, needed)
            }
        }

        // Check vertical
        for (col in 0 until board.getWells()) {
            for (row in 0..board.boardConfig.height - needed) {
                score += evaluateLine(board, col, row, 0, 1, player, needed)
            }
        }

        // Check diagonal (down-right)
        for (col in 0..board.getWells() - needed) {
            for (row in 0..board.boardConfig.height - needed) {
                score += evaluateLine(board, col, row, 1, 1, player, needed)
            }
        }

        // Check diagonal (up-right)
        for (col in 0..board.getWells() - needed) {
            for (row in needed - 1 until board.boardConfig.height) {
                score += evaluateLine(board, col, row, 1, -1, player, needed)
            }
        }

        // Mobility - number of available moves
        val mobility = board.getLegalMoves().size
        score += mobility * 2

        return score
    }

    private fun evaluateLine(
        board: Board,
        startX: Int,
        startY: Int,
        dx: Int,
        dy: Int,
        player: Int,
        needed: Int,
    ): Int {
        var playerCount = 0
        var opponentCount = 0
        var emptyCount = 0

        for (i in 0 until needed) {
            val x = startX + dx * i
            val y = startY + dy * i
            val cell = board.board[x][y]

            when {
                cell == player -> playerCount++
                cell == -player -> opponentCount++
                else -> emptyCount++
            }
        }

        // Both players have pieces in this line - no potential
        if (playerCount > 0 && opponentCount > 0) return 0

        // Score based on potential
        return when {
            playerCount == needed -> 10000

            // Win
            opponentCount == needed -> -10000

            // Loss
            playerCount == needed - 1 && emptyCount == 1 -> 500

            // One move from win
            opponentCount == needed - 1 && emptyCount == 1 -> -800

            // Opponent one move from win
            playerCount == needed - 2 && emptyCount == 2 -> 100

            // Two moves from win
            opponentCount == needed - 2 && emptyCount == 2 -> -150

            // Opponent two moves from win
            playerCount > 0 -> playerCount * playerCount * 10

            // General advantage
            opponentCount > 0 -> -opponentCount * opponentCount * 10

            // General disadvantage
            else -> 0
        }
    }

    private fun simulateMove(
        board: Board,
        column: Int,
        player: Int,
    ): Board {
        val newBoard = board.board.map { it.clone() }.toTypedArray()
        val row = board.getHighestSpaceIndex(column)
        if (row >= 0) {
            newBoard[column][row] = player
        }
        val newHistory = board.history + column
        return Board(newBoard, board.boardConfig, newHistory)
    }

    private fun precomputeWinningPatterns(): List<List<Pair<Int, Int>>> {
        // This would precompute all winning patterns for faster evaluation
        // For now, we'll evaluate dynamically
        return emptyList()
    }

    override fun resetSelf(): OpponentProfile {
        transpositionTable.clear()
        killerMoves.clear()
        historyHeuristic.clear()
        return this
    }

    private fun Board.getLegalMoves(): List<Int> = (0 until getWells()).filter { board[it][0] == 0 }
}
