package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class HumanProfile(override val id: Int) : OpponentProfile {
    override fun nextMove(board: Board): Int {
        TODO("Not yet implemented")
    }
}
