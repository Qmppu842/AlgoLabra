package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

interface OpponentProfile {


    val id: Int

    fun nextMove(board: Board, forSide: Int): Int
}
