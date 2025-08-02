package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.getIndexOfMax
import io.qmpu842.labs.logic.Board

class DFSProfile(
    val side: Int,
) : OpponentProfile() {
    /**
     * Allowed time limit for this to think
     */
    override var timeLimit = 1000L

    var currentMaxTime = System.currentTimeMillis() + timeLimit

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit
        val joku =
            resulting(
                board = board.deepCopy(),
                forSide = forSide,
        )


        return joku.getIndexOfMax()
    }

    fun resulting(
        board: Board,
        forSide: Int,
        winnersAndLoser: IntArray = IntArray(board.getWells()) { 0 },
    ): IntArray {
        val timeNow = System.currentTimeMillis()
        if (timeNow >= currentMaxTime) {
            return winnersAndLoser
        }

        val voittaja = board.isLastPlayWinning()
        if (voittaja) {
            val laste = board.history.last()
            // Nvm the performance is about the same, still weird, not as weird as the other. I was using the wrong profile
            // Maybe tiny bit better
            if (forSide == side) {
                winnersAndLoser[laste] -= 1
            } else {
                winnersAndLoser[laste] += 1
            }
            return winnersAndLoser
        }

        val moves = board.getLegalMoves()
        moves.shuffle(rand)

        val res = IntArray(board.getWells()) { 0 }
        for (move in moves) {
            val ddd = board.dropToken(move, forSide)
            val ccc =
                resulting(
                    board = ddd,
                    forSide = -forSide,
                    winnersAndLoser,
                )
            ccc.forEachIndexed { index, t ->
                res[index] += t
            }
        }
        return res
    }
}
