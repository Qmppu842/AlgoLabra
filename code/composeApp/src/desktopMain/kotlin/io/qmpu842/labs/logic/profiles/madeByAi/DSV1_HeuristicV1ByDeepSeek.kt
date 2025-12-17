package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
@Suppress("ktlint:standard:class-naming")
class DSV1_HeuristicV1ByDeepSeek : OpponentProfile() {
    private val maxDepth = 6  // Adjust depth for difficulty

    override fun nextMove(board: Board, forSide: Int): Int {
        val startTime = System.currentTimeMillis()
        val maxTime = 2000L  // 2 seconds max

        // Get all legal moves
        val legalMoves = (0 until board.getWells()).filter {
            board.getWellSpace(it) > 0
        }

        // Check for immediate win
        for (move in legalMoves) {
            val simulatedBoard = simulateMove(board, move, forSide)
            if (simulatedBoard.isLastPlayWinning()) {
                return move
            }
        }

        // Check for opponent's immediate win (block)
        val opponent = -forSide
        for (move in legalMoves) {
            val simulatedBoard = simulateMove(board, move, opponent)
            if (simulatedBoard.isLastPlayWinning()) {
                return move
            }
        }

        // Use iterative deepening with time limit
        var bestMove = legalMoves[legalMoves.size / 2]  // Default to middle
        var currentDepth = 2

        while (currentDepth <= maxDepth && System.currentTimeMillis() - startTime < maxTime) {
            val result = minimax(board, currentDepth, forSide, Int.MIN_VALUE, Int.MAX_VALUE, true)
            bestMove = result.second
            currentDepth++
        }

        return bestMove
    }

    private fun minimax(
        board: Board,
        depth: Int,
        player: Int,
        alpha: Int,
        beta: Int,
        maximizingPlayer: Boolean
    ): Pair<Int, Int> {
        val legalMoves = (0 until board.getWells()).filter {
            board.getWellSpace(it) > 0
        }

        // Terminal conditions
        if (depth == 0 || legalMoves.isEmpty() || board.isAtMaxSize()) {
            return Pair(evaluate(board, player), -1)
        }

        // Check for immediate win/lose
        if (board.isLastPlayWinning()) {
            return if (board.history.lastOrNull()?.let {
                    board.board[it][board.getWellSpace(it)] == player
                } == true) {
                Pair(1000000 - depth, -1)  // Win for us
            } else {
                Pair(-1000000 + depth, -1)  // Win for opponent
            }
        }

        var bestValue = if (maximizingPlayer) Int.MIN_VALUE else Int.MAX_VALUE
        var bestMove = legalMoves.firstOrNull() ?: -1
        var currentAlpha = alpha
        var currentBeta = beta

        // Move ordering: middle moves first (better for alpha-beta)
        val orderedMoves = legalMoves.sortedBy {
            abs(it - board.getWells() / 2)
        }

        for (move in orderedMoves) {
            val simulatedBoard = simulateMove(board, move,
                if (maximizingPlayer) player else -player)

            val result = minimax(
                simulatedBoard,
                depth - 1,
                player,
                currentAlpha,
                currentBeta,
                !maximizingPlayer
            )

            val value = result.first

            if (maximizingPlayer) {
                if (value > bestValue) {
                    bestValue = value
                    bestMove = move
                }
                currentAlpha = max(currentAlpha, bestValue)
            } else {
                if (value < bestValue) {
                    bestValue = value
                    bestMove = move
                }
                currentBeta = min(currentBeta, bestValue)
            }

            // Alpha-beta pruning
            if (currentBeta <= currentAlpha) {
                break
            }
        }

        return Pair(bestValue, bestMove)
    }

    private fun evaluate(board: Board, player: Int): Int {
        // Get scores for both players
        val (player1Score, player2Score) = board.getFullBoardValues()

        // Extract our score and opponent's score based on which player we are
        val ourScore = if (player == 1) player1Score else player2Score
        val opponentScore = if (player == 1) player2Score else player1Score

        // Base evaluation: our score minus opponent's score
        var evaluation = ourScore - opponentScore

        // Center control bonus (middle columns are more valuable)
        val middleCol = board.getWells() / 2
        for (col in 0 until board.getWells()) {
            val centerBonus = board.getWells() - abs(col - middleCol)
            for (row in 0 until board.boardConfig.height) {
                if (board.board[col][row] == player) {
                    evaluation += centerBonus
                } else if (board.board[col][row] == -player) {
                    evaluation -= centerBonus
                }
            }
        }

        // Threat detection: check for potential 3-in-a-rows
        evaluation += countThreats(board, player) * 50
        evaluation -= countThreats(board, -player) * 50

        return evaluation
    }

    private fun countThreats(board: Board, player: Int): Int {
        var threatCount = 0
        val neededForWin = board.boardConfig.neededForWin

        // Check horizontal threats
        for (row in 0 until board.boardConfig.height) {
            for (col in 0..(board.boardConfig.width - neededForWin)) {
                var playerCount = 0
                var emptyCount = 0
                for (i in 0 until neededForWin) {
                    val value = board.board[col + i][row]
                    when {
                        value == player -> playerCount++
                        value == 0 -> emptyCount++
                    }
                }
                if (playerCount == neededForWin - 1 && emptyCount == 1) {
                    threatCount++
                }
            }
        }

        // Check vertical threats
        for (col in 0 until board.boardConfig.width) {
            for (row in 0..(board.boardConfig.height - neededForWin)) {
                var playerCount = 0
                var emptyCount = 0
                for (i in 0 until neededForWin) {
                    val value = board.board[col][row + i]
                    when {
                        value == player -> playerCount++
                        value == 0 -> emptyCount++
                    }
                }
                if (playerCount == neededForWin - 1 && emptyCount == 1) {
                    threatCount++
                }
            }
        }

        // Check diagonal (up-right) threats
        for (col in 0..(board.boardConfig.width - neededForWin)) {
            for (row in 0..(board.boardConfig.height - neededForWin)) {
                var playerCount = 0
                var emptyCount = 0
                for (i in 0 until neededForWin) {
                    val value = board.board[col + i][row + i]
                    when {
                        value == player -> playerCount++
                        value == 0 -> emptyCount++
                    }
                }
                if (playerCount == neededForWin - 1 && emptyCount == 1) {
                    threatCount++
                }
            }
        }

        // Check diagonal (up-left) threats
        for (col in (neededForWin - 1) until board.boardConfig.width) {
            for (row in 0..(board.boardConfig.height - neededForWin)) {
                var playerCount = 0
                var emptyCount = 0
                for (i in 0 until neededForWin) {
                    val value = board.board[col - i][row + i]
                    when {
                        value == player -> playerCount++
                        value == 0 -> emptyCount++
                    }
                }
                if (playerCount == neededForWin - 1 && emptyCount == 1) {
                    threatCount++
                }
            }
        }

        return threatCount
    }

    private fun simulateMove(board: Board, column: Int, player: Int): Board {
        // Create a deep copy of the board
        val newBoardArray = Array(board.board.size) { i ->
            board.board[i].copyOf()
        }

        // Find the lowest empty row in the column
        val row = board.getHighestSpaceIndex(column)
        if (row >= 0) {
            newBoardArray[column][row] = player
        }

        // Create new history
        val newHistory = board.history + column

        return Board(newBoardArray, board.boardConfig, newHistory)
    }
}