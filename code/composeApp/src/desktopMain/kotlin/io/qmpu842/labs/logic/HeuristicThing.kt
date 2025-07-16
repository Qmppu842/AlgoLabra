package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import java.awt.Point
import kotlin.math.abs

object HeuristicThing {
    fun allTheWells(
        board: Board,
        neededForWin: Int = 4,
        forSide: Int = -1,
        maxDepth: Int = 2,
    ): MutableList<Int> {
        val results = mutableListOf<Int>()
        val targets = List(board.board.size) { it }
        for (target in targets) {
            results.add(
                singleWellHeuristic(
                    column = target,
                    board = board,
                    neededForWin = neededForWin,
                    forSide = forSide,
                    maxDepth = maxDepth,
                ),
            )
        }
        return results
    }

    fun singleWellHeuristic(
        column: Int,
        board: Board,
        neededForWin: Int = 4,
        forSide: Int = -1,
        maxDepth: Int = 2,
    ): Int {
        val target = board.getWellSpace(column) - 1

        return checkAround(
            startingPoint = Point(column, target),
            board = board,
            forSide = forSide,
            depth = 0,
            maxDepth = maxDepth,
        )
    }

    private fun checkAround(
        startingPoint: Point,
        board: Board,
        forSide: Int = -1,
        depth: Int = 0,
        maxDepth: Int = 0,
    ): Int {
        var result = 0
        val toCheck = mutableListOf<Point>()

        val ways = Way.entries
        for (way in ways) {
            val next = board.board.next(startingPoint, way)
            if (next == null) {
                result -= 150
                continue
            }
            result -= depth * 10

            val value = board.board.get(next)
            if (value == null && way == Way.Up) result -= 1000
            check(value != null) { "It would be 'eadache if this ever shows up." }

            var shouldThink: Boolean
            if (value == 0) {
                result + 50
                shouldThink = true
            } else {
                val valueSide = value / abs(value)
                if (forSide == valueSide) {
                    result += 101 * (depth+1)
                    shouldThink = true
                } else {
                    result -= 75
                    shouldThink = false
                }
            }

            if (way == Way.Up) {
                shouldThink = false
            }
            if (depth >= maxDepth) {
                shouldThink = false
            }

            if (shouldThink) {
                toCheck.add(next)
            }
        }

        var additionalPoints = 0
        for (checkIt in toCheck) {
            additionalPoints +=
                checkAround(
                    startingPoint = checkIt,
                    board = board,
                    forSide = forSide,
                    depth = depth + 1,
                    maxDepth = maxDepth,
                )
        }

        return result + additionalPoints
    }
}
