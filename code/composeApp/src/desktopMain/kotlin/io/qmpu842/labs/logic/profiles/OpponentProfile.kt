package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.helpers.Stats
import io.qmpu842.labs.logic.Board

abstract class OpponentProfile {
    val rand = MyRandom.random
    val id: Int = rand.nextInt()
    open var depth: Int = -1

    open val timeLimit: Long = 100

    var firstPlayStats: Stats = Stats()
    var secondPlayStats: Stats = Stats()

    val combinedStats: Stats
        get() = firstPlayStats + secondPlayStats

    /**
     * Name of the profile
     */
    open val name: String
        get() {
            return "${this::class.simpleName}, depth ${depth}, timelimit $timeLimit"
        }

    /**
     * Takes the
     *  @param board and
     *  @param forSide then
     *  @return the index of the well that the profile wants to play next
     */
    abstract fun nextMove(
        board: Board,
        forSide: Int,
    ): Int

    open fun resetSelf(): OpponentProfile {
        return this
    }
}
