package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.helpers.Stats
import io.qmpu842.labs.logic.Board

abstract class OpponentProfile {
    val rand = MyRandom.random
    val id: Int = rand.nextInt()

    open val timeLimit:Int = 10

    var stats: Stats = Stats()

    abstract fun nextMove(
        board: Board,
        forSide: Int): Int
}
