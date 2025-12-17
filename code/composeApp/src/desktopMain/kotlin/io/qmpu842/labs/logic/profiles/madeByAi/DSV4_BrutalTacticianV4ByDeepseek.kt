package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs
import kotlin.math.max
@Suppress("ktlint:standard:class-naming")
class DSV4_BrutalTacticianV4ByDeepseek : OpponentProfile() {
    override val name = "DSV4_BrutalTacticianV4ByDeepseek"
    override var depth: Int = 8 // Will be adjusted dynamically based on time
    override val timeLimit: Long = 100 // 100ms base, but we'll use the full second

    private data class MoveEval(
        val column: Int,
        val score: Int,
    )

    private val killerMoves = mutableMapOf<Int, MutableSet<Int>>()
    private var transpositionTable = mutableMapOf<String, Int>()
    private var nodesEvaluated = 0
    private var startTime = 0L

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        startTime = System.currentTimeMillis()
        nodesEvaluated = 0
        val moveTimeLimit = timeLimit * 10 // Use full second

        // Immediate win detection
        val winningMove = findImmediateWin(board, forSide)
        if (winningMove != -1) return winningMove

        // Block opponent's immediate win
        val blockingMove = findImmediateWin(board, -forSide)
        if (blockingMove != -1) return blockingMove

        // Iterative deepening with time management
        var bestMove = board.getLegalsMiddleOutSeq().firstOrNull { it != -1 } ?: 0
        var currentDepth = 4 // Start with shallow depth

        while (System.currentTimeMillis() - startTime < moveTimeLimit && currentDepth <= 12) {
            depth = currentDepth

            val result = iterativeDeepening(board, forSide, currentDepth, moveTimeLimit)
            if (result.first != -1 && System.currentTimeMillis() - startTime < moveTimeLimit * 0.9) {
                bestMove = result.first
            }

            currentDepth++
        }

        return bestMove
    }

    private fun iterativeDeepening(
        board: Board,
        forSide: Int,
        maxDepth: Int,
        timeLimit: Long,
    ): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        // Order moves: center first, then captures, then killers, then rest
        val orderedMoves = orderMoves(board, forSide)

        for (move in orderedMoves) {
            if (System.currentTimeMillis() - startTime > timeLimit * 0.9) break

            val newBoard = makeMove(board, move, forSide)
            val score = -negamax(newBoard, maxDepth - 1, -beta, -alpha, -forSide, move)

            if (score > bestScore) {
                bestScore = score
                bestMove = move
                alpha = max(alpha, bestScore)
            }
        }

        // Store killer move
        if (bestMove != -1) {
            val depthKey = maxDepth
            killerMoves.getOrPut(depthKey) { mutableSetOf() }.add(bestMove)
        }

        return Pair(bestMove, bestScore)
    }

    private fun negamax(
        board: Board,
        depth: Int,
        alpha: Int,
        beta: Int,
        forSide: Int,
        lastMove: Int,
    ): Int {
        nodesEvaluated++

        // Time check
        if (nodesEvaluated % 1000 == 0 && System.currentTimeMillis() - startTime > timeLimit * 10) {
            return evaluateBoard(board, forSide)
        }

        // Terminal node check
        if (board.isLastPlayWinning()) {
            return if (board.history.lastOrNull() == lastMove) {
                Int.MIN_VALUE / 2 + depth // Winning move (prefer faster wins)
            } else {
                Int.MAX_VALUE / 2 - depth // Opponent wins (delay loss)
            }
        }

        if (board.isAtMaxSize() || depth == 0) {
            return evaluateBoard(board, forSide)
        }

        // Transposition table lookup
        val boardKey = board.toString() + forSide + depth
        transpositionTable[boardKey]?.let { return it }

        var bestValue = Int.MIN_VALUE
        var currentAlpha = alpha

        val orderedMoves = orderMoves(board, forSide)

        for (move in orderedMoves) {
            val newBoard = makeMove(board, move, forSide)
            val value = -negamax(newBoard, depth - 1, -beta, -currentAlpha, -forSide, move)

            bestValue = max(bestValue, value)
            currentAlpha = max(currentAlpha, bestValue)

            if (currentAlpha >= beta) {
                // Beta cutoff
                if (move != -1) {
                    val depthKey = depth
                    killerMoves.getOrPut(depthKey) { mutableSetOf() }.add(move)
                }
                break
            }
        }

        // Store in transposition table
        if (bestValue > alpha && bestValue < beta) {
            transpositionTable[boardKey] = bestValue
            if (transpositionTable.size > 10000) {
                transpositionTable.clear() // Clear to prevent memory issues
            }
        }

        return bestValue
    }

    private fun orderMoves(
        board: Board,
        forSide: Int,
    ): List<Int> {
        val legalMoves = board.getLegalsMiddleOutSeq().takeWhile { it != -1 }.toList()

        // Score each move for ordering
        val scoredMoves =
            legalMoves.map { move ->
                var score = 0

                // Center preference (from middle-out sequence already helps)
                val center = board.getWells() / 2
                score += 100 - abs(move - center) * 10

                // Immediate win check
                val testBoard = makeMove(board, move, forSide)
                if (testBoard.isLastPlayWinning()) score += 10000

                // Block opponent win
                val oppTestBoard = makeMove(board, move, -forSide)
                if (oppTestBoard.isLastPlayWinning()) score += 5000

                // Create double threats
                score += countThreats(testBoard, forSide) * 100

                // Prefer moves that give opponent fewer options
                score += (board.getWells() - testBoard.getLegalsMiddleOutSeq().count { it != -1 }) * 10

                MoveEval(move, score)
            }

        // Killer moves boost
        val killers = killerMoves.values.flatten().toSet()
        val finalScored =
            scoredMoves.map { me ->
                val killerBoost = if (killers.contains(me.column)) 200 else 0
                me.copy(score = me.score + killerBoost)
            }

        return finalScored.sortedByDescending { it.score }.map { it.column }
    }

    private fun evaluateBoard(
        board: Board,
        forSide: Int,
    ): Int {
        val (p1Score, p2Score) = board.getFullBoardValues()
        var score = if (forSide == 1) p1Score - p2Score else p2Score - p1Score

        // Add positional bonuses
        score += evaluatePositional(board, forSide)

        // Pattern recognition bonuses
        score += evaluatePatterns(board, forSide) * 10

        return score
    }

    private fun evaluatePositional(
        board: Board,
        forSide: Int,
    ): Int {
        var score = 0
        val center = board.getWells() / 2

        for (col in 0 until board.getWells()) {
            if (board.getWellSpace(col) == 0) continue // Column full

            val height = board.getHighestSpaceIndex(col)
            if (height < 0) continue

            // Center control bonus
            val centerDist = abs(col - center)
            score += (board.getWells() / 2 - centerDist) * 3

            // Prefer moves that connect to existing pieces
            for (way in Way.half) {
                val nx = col + way.x
                val ny = height + way.y
                if (nx in 0 until board.getWells() && ny in 0 until board.boardConfig.height) {
                    if (board.board[nx][ny] == forSide) {
                        score += 5
                    } else if (board.board[nx][ny] == -forSide) {
                        score -= 2 // Slight penalty for helping opponent block
                    }
                }
            }
        }

        return score
    }

    private fun evaluatePatterns(
        board: Board,
        forSide: Int,
    ): Int {
        var patterns = 0

        // Check for potential 3-in-a-rows with open ends
        for (col in 0 until board.getWells()) {
            val height = board.getWellSpace(col)
            if (height >= board.boardConfig.height) continue

            // Check horizontal potential
            patterns += countPotentialLines(board, col, height, forSide, Way.Left)
        }

        return patterns
    }

    private fun countPotentialLines(
        board: Board,
        x: Int,
        y: Int,
        side: Int,
        way: Way,
    ): Int {
        var count = 0
        var consecutive = 0
        var openEnds = 0

        // Check in both directions
        for (dir in listOf(way, Way.opp[way.ordinal])) {
            var cx = x
            var cy = y

            for (i in 0 until 3) {
                cx += dir.x
                cy += dir.y

                if (cx !in 0 until board.getWells() || cy !in 0 until board.boardConfig.height) {
                    if (i == 0) openEnds++
                    break
                }

                val cell = board.board[cx][cy]
                if (cell == side) {
                    consecutive++
                } else if (cell == 0) {
                    openEnds++
                    break
                } else {
                    break
                }
            }
        }

        if (consecutive >= 2 && openEnds >= 1) count += 1
        if (consecutive >= 1 && openEnds >= 2) count += 2 // Double threat

        return count
    }

    private fun countThreats(
        board: Board,
        forSide: Int,
    ): Int {
        var threats = 0

        for (col in 0 until board.getWells()) {
            if (board.getWellSpace(col) == 0) continue

            val testBoard = makeMove(board, col, forSide)
            if (testBoard.isLastPlayWinning()) {
                threats++
            }
        }

        return threats
    }

    private fun findImmediateWin(
        board: Board,
        forSide: Int,
    ): Int {
        for (col in board.getLegalsMiddleOutSeq()) {
            if (col == -1) break

            val testBoard = makeMove(board, col, forSide)
            if (testBoard.isLastPlayWinning()) {
                return col
            }
        }
        return -1
    }

    private fun makeMove(
        board: Board,
        column: Int,
        side: Int,
    ): Board {
        val newBoard = board.board.map { it.copyOf() }.toTypedArray()
        val height = board.getHighestSpaceIndex(column)
        if (height >= 0) {
            newBoard[column][height] = side
        }
        return Board(newBoard, board.boardConfig, board.history + column)
    }
}
