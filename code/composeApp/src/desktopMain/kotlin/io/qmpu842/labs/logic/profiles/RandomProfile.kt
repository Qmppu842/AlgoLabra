package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.Board

class RandomProfile(override val id: Int = MyRandom.random.nextInt()) : OpponentProfile {
    val rand = MyRandom.random

    override fun nextMove(board: Board): Int {
       return board.getLegalMoves().random(rand)
    }
}
