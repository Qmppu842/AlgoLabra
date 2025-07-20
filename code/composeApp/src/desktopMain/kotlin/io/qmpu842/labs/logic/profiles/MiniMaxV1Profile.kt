package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class MiniMaxV1Profile : OpponentProfile() {
    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int = board.getLegalMoves().random(rand)

    fun minimax(node: Any, depth :Int, maximizingPlayer: Boolean): Int {
        return 0
    }
}
