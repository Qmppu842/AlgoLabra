package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class DFSProfile : OpponentProfile() {
    /**
     * Allowed time limit for this to think
     */
    var timeLimit = 5000

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        println("asd")
        return board.getLegalMoves().random(rand)
    }
}
