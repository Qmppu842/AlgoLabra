package io.qmpu842.labs.logic

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

object SecondHeuristicThing {
    fun combinedWells(
        board: Board,
        forSide: Int,
    ): IntArray {
        return IntArray(board.getWells()) { 0 }
        val res = IntArray(board.getWells()) { 0 }

        // Tier 0: Insta wins
        val threeStraightPos = getMovesWith3Straight(board, forSide)
        val threeStraightNeg = getMovesWith3Straight(board, -forSide)
        val gappedPos = getMovesWith3TokensWithAirGap(board, forSide)
        val gappedNeg = getMovesWith3TokensWithAirGap(board, -forSide)

        // Tier 1: 2 tokens

        // Tier 99: Just empty boards
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

        for (i in 0..<startingPoints.size step 2) {
            var counter = 0
            for (way in Way.entries) {
                val hold =
                    board.checkLine(
                        x = startingPoints[i],
                        y = startingPoints[i + 1],
                        sign = forSide,
                        way = way,
                    )
                if (hold >= board.boardConfig.neededForWin - 1) {
                    counter++
                }
            }
            result[startingPoints[i]] = counter
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
    fun getMovesWith3TokensWithAirGap(
        board: Board,
        forSide: Int,
    ): IntArray {
        val result = IntArray(board.board.size) { 0 }

        val startingPoints = board.startingPoints()

        for (i in 0..<startingPoints.size step 2) {
            var counter = 0
            for (way in Way.entries) {
                val x = startingPoints[i]
                val y = startingPoints[i + 1]
                val result: Int =
                    board.checkLine(
                        x = x + way.x,
                        y = y + way.y,
                        sign = forSide,
                        way = way,
                    )
                val opposite = way.getOpposite()
                val result2: Int =
                    board.checkLine(
                        x = x + opposite.x,
                        y = y + opposite.y,
                        sign = forSide,
                        way = opposite,
                    )
                val doubleLine = result + result2

                if (doubleLine >= board.boardConfig.neededForWin - 1) {
                    counter++
                }
            }
            result[startingPoints[i]] = counter
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

        for (i in 0..<startingPoints.size step 2) {
            var counter = 0
            for (way in Way.entries) {
                val x = startingPoints[i]
                val y = startingPoints[i + 1]
                val result: Int =
                    board.checkLine(
                        x = x + way.x,
                        y = y + way.y,
                        sign = 0,
                        way = way,
                    )
                val opposite = way.getOpposite()
                val result2: Int =
                    board.checkLine(
                        x = x + opposite.x,
                        y = y + opposite.y,
                        sign = 0,
                        way = opposite,
                    )
                if (result == result2) counter += (result + result2)
                counter += (result + result2)
            }
            result[startingPoints[i]] = counter
        }
        return result
    }
}
