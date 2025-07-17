package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import java.awt.Point
import kotlin.math.abs
import kotlin.math.sign

object SecondHeuristicThing {
    /**
     * This values basically guaranteed win places open in the map
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3StraightAnd2AirSpace(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values places with 3 tokens
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3Straight(board: Board, forSide: Int): IntArray {
        val result = IntArray(board.board.size) { 0 }

        val legalSpaces = board.getLegalMoves()

        val startingPoints = mutableListOf<Point>()
        for (aa in legalSpaces) {
            val thing = board.getWellSpace(aa)
            startingPoints.add(Point(aa, thing - 1))
        }

        ylempi@ for (sp in startingPoints) {
            println("result: ${result.toList()}")
            ways@ for (way in Way.entries) {
                println("result1: ${result.toList()}")
                var nextValue: Int
                var counter = 0
                var lastCounter = 0
                do {
                    if (counter >= 3) {
                        result[sp.x] = Int.MAX_VALUE
                        println("result2: ${result.toList()}")
                        continue@ways
                    }
                    if (counter <= -3) {
                        result[sp.x] = Int.MIN_VALUE
                        println("result3: ${result.toList()}")
                        continue@ways
                    }
                    val next = board.board.next(sp, way) ?: continue@ways
                    nextValue = (board.board).get(next) ?: continue@ways

                    if (nextValue.sign == forSide.sign) {
                        counter++
                    } else if (nextValue.sign * -1 == forSide.sign) {
                        counter--
                    }
                    if (abs(counter) <= lastCounter) {
                        continue@ways
                    } else {
                        lastCounter = abs(counter)
                    }
                } while (nextValue.sign == forSide.sign || nextValue.sign * -1 == forSide.sign)
            }
        }

        return result
    }

    fun checkLine() {
    }

    /**
     * This values places with 3 tokens separated by 1 air
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3TokensWithAirGap(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }



    /**
     * This values places with already 2 tokens and that would have 2 airspaces around after this one
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith2TokensAndAirSpaceAround(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values places with 2 tokens and possibility for 4
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith2TokensAndAirSpace(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values the open spaces around itself.
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getOpenness(board: Board, forSide: Int,): IntArray {
        TODO("Yet to be implemented")
    }


    /**
     * This is the actual checker
     *
     * @param patternToLook
     *  x for this position
     *  o for air
     *  e for enemy
     *  a for allied
     *
     *  @param point where to look for it
     *
     * @return first is how many allied patterns
     */
    fun theChecker(
        board: Board,
        patternToLook: CharArray,
        point: Point,
        forSide: Int,
    ): Pair<Int, Int> {
        TODO("Yet to be implemented")
    }
}
