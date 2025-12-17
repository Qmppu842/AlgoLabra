package io.qmpu842.labs.logic.heuristics

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import kotlin.math.sign

/**
 * Made by chatgpt
 *
 */
fun fullPowerByChatGPT(heuristicArgs: HeuristicArgs): Int {
    val (board, _, _, forSide, neededForWin) = heuristicArgs
    var threatCount = 0

    // Helper: scan windows in a given direction for scores and threats
    fun scanWindows(
        startingPoints: List<Board.Point>,
        way: Way,
    ): Pair<Int, Int> {
        var totalP1 = 0
        var totalP2 = 0

        for (point in startingPoints) {
            var nextX = point.x
            var nextY = point.y
            val window = mutableListOf<Int>()

            while (nextX in 0 until board.boardConfig.width &&
                nextY in 0 until board.boardConfig.height
            ) {
                val current = board.board[nextX][nextY].sign
                window.add(current)
                if (window.size > neededForWin) window.removeAt(0)

                if (window.size == neededForWin) {
                    val p1Count = window.count { it == 1 }
                    val p2Count = window.count { it == -1 }
                    val emptyCount = window.count { it == 0 }

                    // Score accumulation (original squared logic)
                    if (p1Count > 0 && p2Count == 0) totalP1 += p1Count * p1Count
                    if (p2Count > 0 && p1Count == 0) totalP2 += p2Count * p2Count

                    // Threat detection: opponent must-blocks
                    if (emptyCount == 1) {
                        val emptyIndex = window.indexOf(0)
                        val xCheck = nextX - (neededForWin - 1 - emptyIndex) * way.x
                        val yCheck = nextY - (neededForWin - 1 - emptyIndex) * way.y

                        // Opponent threat
                        val opponentSign = -forSide
                        if ((opponentSign == 1 && p1Count == neededForWin - 1) ||
                            (opponentSign == -1 && p2Count == neededForWin - 1)
                        ) {
                            if (yCheck == board.getHighestSpaceIndex(xCheck)) threatCount++
                        }
                    }
                }

                nextX += way.x
                nextY += way.y
            }
        }

        return Pair(totalP1, totalP2)
    }

    // 1. Aggregate board scores
    val (vertP1, vertP2) = scanWindows(board.katto, Way.Down)
    val (horiP1, horiP2) = scanWindows(board.rightSeina, Way.Left)
    val (diagUpP1, diagUpP2) = scanWindows(board.rightSeinaJaPohja, Way.LeftUp)
    val (diagDownP1, diagDownP2) = scanWindows(board.leftSeinaJaPohja, Way.UpRight)

    val totalP1 = vertP1 + horiP1 + diagUpP1 + diagDownP1
    val totalP2 = vertP2 + horiP2 + diagUpP2 + diagDownP2

    // 2. Perspective
    val myScore = if (forSide == 1) totalP1 else totalP2
    val oppScore = if (forSide == 1) totalP2 else totalP1

    // 3. Threat penalty (dominates any heuristic gain)
    val threatValue = 1_000_0
    val threatPenalty = threatCount * threatValue

    return (myScore - oppScore) - threatPenalty
}
