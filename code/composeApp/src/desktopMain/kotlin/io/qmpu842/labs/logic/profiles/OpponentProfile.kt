package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.helpers.Stats
import io.qmpu842.labs.logic.Board

abstract class OpponentProfile {
    val rand = MyRandom.random
    val id: Int = rand.nextInt()

    open val timeLimit:Long = 100

    var firstPlayStats: Stats = Stats()
    var secondPlayStats: Stats = Stats()

    /**
     * Name of the profile
     */
    val name: String
        get(){
            return "${this::class.simpleName}"
        }

    /**
     * Takes the
     *  @param board and
     *  @param forSide then
     *  @return the index of the well that the profile wants to play next
     */
    abstract fun nextMove(
        board: Board,
        forSide: Int): Int
}
