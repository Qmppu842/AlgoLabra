package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

interface OpponentProfile {
    fun nextMove(board: Board): Int
}
