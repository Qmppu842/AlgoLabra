package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.MoveHistory

class RandomProfile : OpponentProfile {
    val rand = MyRandom.random

    override fun nextMove(state: MoveHistory): Int {
        val moves = state.getLegalMoves()
        return moves.random(rand)
    }
}
