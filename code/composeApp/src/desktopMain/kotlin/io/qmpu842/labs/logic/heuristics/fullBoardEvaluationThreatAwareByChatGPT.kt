package io.qmpu842.labs.logic.heuristics

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import kotlin.math.sign

/**
 * Made by chatgpt
 *
 */
fun fullBoardEvaluationThreatAwareByChatGPT(heuristicArgs: HeuristicArgs): Int {
    val (board, _, _, forSide, neededForWin) = heuristicArgs
    val winn = neededForWin

    // 1. Base scores using your existing getGeneralLineValues
    val (basePlayer1, basePlayer2) = board.getFullBoardValues()

    var threatCount = 0

    // Helper: detect opponent must-block threats
    fun detectThreats(
        startingPoints: List<Board.Point>,
        way: Way,
        opponentSign: Int,
    ) {
        for (point in startingPoints) {
            var nextX = point.x
            var nextY = point.y
            val window = mutableListOf<Int>()
            while (nextX in 0 until board.boardConfig.width && nextY in 0 until board.boardConfig.height) {
                val current = board.board[nextX][nextY].sign
                window.add(current)
                if (window.size > winn) window.removeAt(0)

                if (window.size == winn) {
                    val oppCount = window.count { it == opponentSign }
                    val emptyCount = window.count { it == 0 }

                    // Must-block threat if opponent has (winn-1) in a row and empty is playable
                    if (oppCount == winn - 1 && emptyCount == 1) {
                        val emptyIndex = window.indexOf(0)
                        val xCheck = nextX - (winn - 1 - emptyIndex) * way.x
                        val yCheck = nextY - (winn - 1 - emptyIndex) * way.y
                        // Check if empty square is playable (on top of column or above filled)
                        if (yCheck == board.getHighestSpaceIndex(xCheck)) {
                            threatCount++
                        }
                    }
                }

                nextX += way.x
                nextY += way.y
            }
        }
    }

    val opponentSign = -forSide
    detectThreats(board.katto, Way.Down, opponentSign)
    detectThreats(board.rightSeina, Way.Left, opponentSign)
    detectThreats(board.rightSeinaJaPohja, Way.LeftUp, opponentSign)
    detectThreats(board.leftSeinaJaPohja, Way.UpRight, opponentSign)

    // 2. Apply perspective to base scores
    val myScore = if (forSide == 1) basePlayer1 else basePlayer2
    val oppScore = if (forSide == 1) basePlayer2 else basePlayer1

    // 3. Apply threat penalty
    val threatValue = 1_000_000 // Must dominate any normal heuristic gain
    val threatPenalty = threatCount * threatValue

    return (myScore - oppScore) - threatPenalty
}
