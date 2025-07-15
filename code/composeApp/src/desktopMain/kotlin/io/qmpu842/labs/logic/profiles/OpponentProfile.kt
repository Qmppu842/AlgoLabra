package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.MoveHistory

interface OpponentProfile {
    fun nextMove(state: MoveHistory): Int
}
