package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class RandomProfile : OpponentProfile() {
    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int = board.getLegalMoves().random(rand)
}
