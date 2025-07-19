package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.next
import io.qmpu842.labs.helpers.summa
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

object SecondHeuristicThing {

    fun combinedWells(
        board: Board,
        forSide: Int,
    ): IntArray {
        val res = IntArray(board.getWells()) { 0 }

        //Tier 0: Insta wins
        val threeStraightPos = getMovesWith3Straight(board, forSide)
        val threeStraightNeg = getMovesWith3Straight(board, -forSide)
        val gappedPos = getMovesWith3TokensWithAirGap(board, forSide)
        val gappedNeg = getMovesWith3TokensWithAirGap(board, -forSide)

        //Tier 1: 2 tokens


        //Tier 99: Just empty boards
        val openness = getOpenness(board)

        res.forEachIndexed { index, t ->
            val positive = max(threeStraightPos[index], gappedPos[index])
            val negative = min(threeStraightNeg[index], gappedNeg[index])
            if (positive.sign == 1) {
                res[index] = Int.MAX_VALUE
            } else if (negative.sign == 1) {
                res[index] = Int.MIN_VALUE
            } else {
                res[index] = openness[index]
            }
        }
        return res
    }

    /**
     * This values places with 3 tokens
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3Straight(
        board: Board,
        forSide: Int,
    ): IntArray {
        val result = IntArray(board.board.size) { 0 }

        val startingPoints = board.startingPoints()

        for (point in startingPoints) {
            var counter = 0
            for (way in Way.entries) {
                val next = board.board.next(point, way)
                if (next == null) continue
                val hold =
                    board.checkLine(
                        current = next,
                        sign = forSide,
                        way = way,
                    )
                if (hold >= 3){
                    counter++
                }
            }
            result[point.x] = counter
        }
        return result
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
        val result = IntArray(board.board.size) { 0 }

        val startingPoints = board.startingPoints()

        for (point in startingPoints) {
            var counter = 0
            for (way in Way.entries) {
                val next = board.board.next(point, way)
                if (next == null) continue
                val doubleLine = board.doubleLineWithJumpStart(
                    current = next,
                    sign = forSide,
                    way = way
                )

                if (doubleLine.summa() >= 3) {
                    counter++
                }
            }
            result[point.x] = counter
        }

        return result
    }

    /**
     * This values the open spaces around itself.
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getOpenness(board: Board): IntArray {
        val result = IntArray(board.board.size) { 0 }

        val startingPoints = board.startingPoints()

        for (asd in startingPoints) {
            var counter = 0
            for (way in Way.entries) {
                val next = board.board.next(asd, way)
                if (next == null) continue
                val hold =
                    board.checkLine(
                        current = next,
                        sign = 0,
                        way = way,
                    )
                val antiNext = board.board.next(asd, way.getOpposite())

                var hold2 = 0
                if (antiNext != null) {
                    hold2 =
                        board.checkLine(
                            current = antiNext,
                            sign = 0,
                            way = way.getOpposite(),
                        )
                }

                // lol this is some fine-tuning, i am half-half with this
                if (hold == hold2) counter += (hold + hold2)
//                if (min(hold, hold2) * 1.2 < max(hold, hold2)) counter -= ((hold + hold2) / 1.25).toInt()
//                if (way in listOf(Way.Up, Way.UpRight, Way.LeftUp)) counter -= ((hold + hold2) / 3).toInt()

                counter += (hold + hold2)
            }
            result[asd.x] = counter
        }

        return result
    }

    fun getMovesWithTwoTokens(
        board: Board,
        forSide: Int,
    ): IntArray {
        val result = IntArray(board.board.size) { 0 }

        val startingPoints = board.startingPoints()

        for (asd in startingPoints) {
            var counter = 0
            for (way in Way.entries) {
                val next = board.board.next(asd, way)
                if (next == null) continue
                val hold =
                    board.checkLine(
                        current = next,
                        sign = forSide,
                        way = way,
                    )
                if (hold == 1){
                    counter++
                }
            }
            result[asd.x] = counter
        }
        return result
    }
}
