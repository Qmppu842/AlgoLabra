package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.SecondHeuristicThing
import kotlin.math.max
import kotlin.math.min

class SimpleOpportunisticProfile(
    override val id: Int= MyRandom.random.nextInt(),
) : OpponentProfile {
    val rand = MyRandom.random

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        val moves = mutableListOf<Int>()

        val asd = board.getLegalMoves().toList()
        moves.addAll(asd)

        val heuristicWells =
            SecondHeuristicThing.combinedWells(
                board = board,
                forSide = forSide,
            )
//        moves.add(
//            heuristicWells.find { iti -> iti == heuristicWells.max() }!!
//        )
        var maxi = 0
        var mini = 0
        heuristicWells.forEach { it ->
            maxi = max(maxi, it)
            mini = min(mini, it)
        }

        heuristicWells.forEachIndexed { index, t ->
            if (t == maxi || t == mini) moves.add(index)
        }

        if (heuristicWells.contains(Int.MIN_VALUE)){
            moves.clear()
            heuristicWells.forEachIndexed { index, t ->
                if (t == Int.MIN_VALUE){
                    moves.add(index)
                }
            }
        }

        if (heuristicWells.contains(Int.MAX_VALUE)){
            moves.clear()
            heuristicWells.forEachIndexed { index, t ->
                if (t == Int.MAX_VALUE){
                    moves.add(index)
                }
            }
        }
        var movemove = moves.random(rand)
        if (movemove > board.getWells()) { // Sometimes this gives numbers that seem closely like the values instead of the  indexes
            println("Strange one, but not for now")
            movemove = board.getLegalMoves().random(rand)
        }
        return movemove
    }
}
