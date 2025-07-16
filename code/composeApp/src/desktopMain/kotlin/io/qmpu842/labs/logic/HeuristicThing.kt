package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import java.awt.Point
import kotlin.math.abs

object HeuristicThing {
    fun singleWellHeuristic(
        column: Int,
        board: Board,
        neededForWin: Int = 4,
    ): Int {
        println("asdas")

        return 0
    }

    fun checkAround(
        startingPoint: Point,
        board: Board,
        forSide: Int = -1
    ): Int {
        var result = 0
        val depth = 0
        val maxDepth = 0
        val toCheck = mutableListOf<Point>()
        toCheck.add(startingPoint)
//        val thing =
//            board.board.get(startingPoint) ?: error("Targeting null cell @ checkAround?")

//        val signed = thing / abs(thing)

        val ways = Way.entries
        while (toCheck.isNotEmpty()) {
            val currentPoint: Point = toCheck.removeFirst()
//            val currPointValue = board.board.get(currentPoint)
            for (way in ways) {
                val next = board.board.next(currentPoint, way)
                if (next == null) {
                    result -= 10
                    continue
                }
                val value = board.board.get(next)
                check(value != null){"It would be 'eadache if this ever shows up."}
                val valueSide = value / abs(value)
                var shouldThink = false
                if (value == 0) {
                    result + 5
                    shouldThink = true
                }else if  (forSide == valueSide){
                    result += 10
                    shouldThink = true
                }else{
                    result -= 10
                }

                if (way == Way.Up){
                    shouldThink = false
                }
                if (depth >= maxDepth){
                    shouldThink = false
                }

                if (shouldThink){
                    toCheck.add(next)
                }
            }
        }

        return result
    }

    private fun checker(
        currentPoint: Point,
        way: Way = Way.Up,
        counter: Int = 1,
        value: Int = 0,
        maxCounter: Int = 4,
        board: Board,
    ): Boolean {
        val currentPointValue = board.board.get(currentPoint) ?: return false

        val valueSum = value + currentPointValue
        if (abs(value) >= abs(valueSum)) return false

        val next = board.board.next(currentPoint, way)
        if (next == null && counter < maxCounter) return false

        if (counter < maxCounter) {
            check(next != null) { "Next should not be null at this point of checker" }
            return checker(
                currentPoint = next,
                way = way,
                counter = counter + 1,
                value = valueSum,
                maxCounter = maxCounter,
                board = board,
            )
        }
        return true
    }
}
