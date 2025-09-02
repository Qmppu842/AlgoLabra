package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import kotlin.math.max
import kotlin.math.min

class MiniMaxV2Profile(
    override var depth: Int = 10,
    override var timeLimit: Long = TRILLION,
) : OpponentProfile() {
    constructor(depth: Int, timeLimit: Int) : this(depth = depth, timeLimit = timeLimit.toLong())

    var currentMaxTime = Long.MAX_VALUE

    /**
     * This is dumb
     * it only purpose is that min and max imports stay even if I comment a/b part in minimax.
     */
    fun dumm() {
        val asd = min(1, 3)
        val qwe = max(1, 3)
    }

    override fun nextMove(board: Board, forSide: Int): Int {
        return 0
    }


}