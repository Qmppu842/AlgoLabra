package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.SecondHeuristicThing

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
            SecondHeuristicThing.both3straights(
                board = board,
                forSide = forSide,
            )
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

        return moves.random(rand)
    }
}
