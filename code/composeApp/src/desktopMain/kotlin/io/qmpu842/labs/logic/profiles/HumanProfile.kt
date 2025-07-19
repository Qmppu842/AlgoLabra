package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class HumanProfile : OpponentProfile() {
    /**
     * There is one waaaaaay more fun thing but nah, that is not for now.
     */
    override fun nextMove(board: Board, forSide: Int): Int {
        return board.getLegalMoves().random(rand)
    }
}
