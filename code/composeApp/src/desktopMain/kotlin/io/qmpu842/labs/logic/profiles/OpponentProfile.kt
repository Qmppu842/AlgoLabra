package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.Board

abstract class OpponentProfile {
    val rand = MyRandom.random
    val id: Int = rand.nextInt()

    var wins = 0
    var lose = 0
    var tie = 0

    abstract fun nextMove(
        board: Board,
        forSide: Int): Int
}
