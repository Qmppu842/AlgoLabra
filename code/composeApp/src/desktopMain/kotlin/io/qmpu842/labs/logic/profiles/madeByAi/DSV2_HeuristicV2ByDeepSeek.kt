package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs
import kotlin.random.Random
@Suppress("ktlint:standard:class-naming")
class DSV2_HeuristicV2ByDeepSeek : OpponentProfile() {

    override fun nextMove(board: Board, forSide: Int): Int {
        val legalMoves = (0 until board.getWells()).filter {
            board.getWellSpace(it) > 0
        }

        // Idea 1: "Threat Propagation" - check for patterns of threats
        val threatScores = IntArray(board.getWells())

        // For each legal move, evaluate the threat it creates and blocks
        for (move in legalMoves) {
            var score = 0

            // Calculate center bonus (prefer center columns)
            val centerBonus = board.getWells() / 2 - abs(move - board.getWells() / 2)
            score += centerBonus * 10

            // Check if this move creates immediate win
            val simulated = simulateMove(board, move, forSide)
            if (simulated.isLastPlayWinning()) {
                return move // Immediate win
            }

            // Check if opponent has immediate win (must block)
            val opponent = -forSide
            val opponentWinningMove = findWinningMove(board, opponent)
            if (opponentWinningMove != -1) {
                // Consider blocking, but sometimes make them work for it?
                // 70% chance to block, 30% to try something else if we can win next turn
                if (Random.nextDouble() < 0.7) {
                    return opponentWinningMove
                }
            }

            // Evaluate "line potential" - how many lines this move participates in
            score += evaluateLineParticipation(board, move, forSide) * 15

            // Penalize moves that give opponent easy responses
            score -= evaluateOpponentOpportunity(board, move, forSide) * 8

            // Favor moves that create multiple threats
            score += countThreatsAfterMove(board, move, forSide) * 25

            // Consider board "tension" - areas with many pieces of both colors
            score += evaluateBoardTension(board, move) * 5

            // Sometimes (10%) make a "wild" move to break patterns
            if (Random.nextDouble() < 0.1) {
                score += Random.nextInt(-50, 50)
            }

            // Small random factor to avoid being perfectly predictable
            score += Random.nextInt(-3, 4)

            threatScores[move] = score
        }

        // Choose the move with highest threat score
        return legalMoves.maxByOrNull { threatScores[it] } ?: legalMoves.random()
    }

    private fun findWinningMove(board: Board, player: Int): Int {
        val legalMoves = (0 until board.getWells()).filter {
            board.getWellSpace(it) > 0
        }

        for (move in legalMoves) {
            val simulated = simulateMove(board, move, player)
            if (simulated.isLastPlayWinning()) {
                return move
            }
        }
        return -1
    }

    private fun evaluateLineParticipation(board: Board, column: Int, player: Int): Int {
        // Count how many winning lines (horizontal, vertical, diagonal) this move is part of
        var count = 0
        val height = board.boardConfig.height

        // Count lines where this column can participate
        for (i in 0 until height) {
            // Check if there's space for a line starting at this position
            if (column >= 0 && column < board.getWells() - 3) {
                count++ // Right horizontal line possible
            }
            if (column >= 3 && column < board.getWells()) {
                count++ // Left horizontal line possible
            }
            if (i <= height - 4) {
                count++ // Vertical line possible
            }
            // Diagonals
            if (column >= 3 && i <= height - 4) {
                count++ // Up-left diagonal
            }
            if (column < board.getWells() - 3 && i <= height - 4) {
                count++ // Up-right diagonal
            }
        }
        return count
    }

    private fun evaluateOpponentOpportunity(board: Board, column: Int, player: Int): Int {
        // Simulate our move, then check how many good moves opponent gets
        val simulated = simulateMove(board, column, player)
        val opponent = -player
        var opportunityScore = 0

        val opponentMoves = (0 until simulated.getWells()).filter {
            simulated.getWellSpace(it) > 0
        }

        for (move in opponentMoves) {
            val oppSimulated = simulateMove(simulated, move, opponent)

            // If opponent could win immediately after our move, that's bad
            if (oppSimulated.isLastPlayWinning()) {
                opportunityScore += 10
            }

            // Check if opponent gets good center control
            val centerDist = abs(move - simulated.getWells() / 2)
            opportunityScore += (simulated.getWells() / 2 - centerDist)
        }

        return opportunityScore
    }

    private fun countThreatsAfterMove(board: Board, column: Int, player: Int): Int {
        // Count how many immediate threats (3-in-a-row with empty space) this move creates
        val simulated = simulateMove(board, column, player)
        var threatCount = 0

        // Check all directions from the placed piece
        val row = simulated.getWellSpace(column) // Get the row where piece was placed
        if (row < 0) return 0

        val neededForWin = board.boardConfig.neededForWin

        // Check in all 4 directions (simplified threat detection)
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue

                var consecutive = 1 // Count our piece
                var emptySpaces = 0

                // Check forward direction
                for (i in 1..3) {
                    val newX = column + dx * i
                    val newY = row + dy * i

                    if (newX in 0 until simulated.getWells() &&
                        newY in 0 until simulated.boardConfig.height) {
                        when (simulated.board[newX][newY]) {
                            player -> consecutive++
                            0 -> emptySpaces++
                        }
                    }
                }

                // Check backward direction
                for (i in 1..3) {
                    val newX = column - dx * i
                    val newY = row - dy * i

                    if (newX in 0 until simulated.getWells() &&
                        newY in 0 until simulated.boardConfig.height) {
                        when (simulated.board[newX][newY]) {
                            player -> consecutive++
                            0 -> emptySpaces++
                        }
                    }
                }

                if (consecutive >= neededForWin - 1 && emptySpaces > 0) {
                    threatCount++
                }
            }
        }

        return threatCount
    }

    private fun evaluateBoardTension(board: Board, column: Int): Int {
        // Areas with mixed pieces create tension and opportunities
        val tensionRadius = 2
        var tension = 0

        val startRow = board.getHighestSpaceIndex(column)
        if (startRow < 0) return 0

        for (dx in -tensionRadius..tensionRadius) {
            for (dy in -tensionRadius..tensionRadius) {
                val checkX = column + dx
                val checkY = startRow + dy

                if (checkX in 0 until board.getWells() &&
                    checkY in 0 until board.boardConfig.height) {
                    val value = board.board[checkX][checkY]
                    if (value != 0) {
                        tension++
                    }
                }
            }
        }

        return tension
    }

    private fun simulateMove(board: Board, column: Int, player: Int): Board {
        // Create a simple simulation (copy board and add piece)
        // Note: This is a simplified simulation - might not handle all edge cases
        val newBoardArray = Array(board.board.size) { i ->
            board.board[i].copyOf()
        }

        val row = board.getHighestSpaceIndex(column)
        if (row >= 0) {
            newBoardArray[column][row] = player
        }

        // Create new history
        val newHistory = board.history + column

        return Board(newBoardArray, board.boardConfig, newHistory)
    }

    // Bonus: Pattern recognition for common traps
    private fun checkForTraps(board: Board, player: Int): Int {
        // Check for "fork" patterns - moves that create multiple winning threats
        val legalMoves = (0 until board.getWells()).filter {
            board.getWellSpace(it) > 0
        }

        for (move in legalMoves) {
            val simulated = simulateMove(board, move, player)

            // Count how many winning threats this move creates
            var threatCount = 0
            for (secondMove in legalMoves) {
                if (secondMove == move) continue
                val doubleSimulated = simulateMove(simulated, secondMove, player)
                if (doubleSimulated.isLastPlayWinning()) {
                    threatCount++
                }
            }

            if (threatCount >= 2) {
                return move // Found a fork!
            }
        }

        return -1
    }
}