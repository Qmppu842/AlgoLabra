package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board

class DFSProfile(
    val side: Int,
) : OpponentProfile() {
    /**
     * Allowed time limit for this to think
     */
    var timeLimit = 1000

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        val eka = System.currentTimeMillis()
//        println("Time on starting: $eka")
        val joku =
            resulting(
                board = board.deepCopy(),
                forSide = forSide,
                timeMax = System.currentTimeMillis() + timeLimit
        )
//        println("joku: ${joku.toList()}")

        var indexe = 0
        var max = joku[indexe]
        
        joku.forEachIndexed { index, t -> 
            if (t > max){
                indexe = index
                max = t
            }
        }
        val toka = System.currentTimeMillis()
        val diff = toka - eka
//        println("Done")
//        println("Time spend2: ${diff} ms")

        return indexe
    }

    fun resulting(
        board: Board,
        forSide: Int,
        timeMax: Long = System.currentTimeMillis() + timeLimit,
        winnersAndLoser: IntArray = IntArray(board.getWells()) { 0 },
    ): IntArray {
        val timeNow = System.currentTimeMillis()
        if (timeNow >= timeMax) {
            return winnersAndLoser
        }

        val voittaja = board.isLastPlayWinning()
        if (voittaja) {
            val laste = board.history.last()
            /* Weird...
             * This (forSide == side) should be the correct one
             * But initial feeling is that this (laste.sign == forSide) performed better.
             * It is nonsensical as the history should be always positive.
             * Yet I remember seeing positives and negatives in the end result, weird..
             */
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
                    timeMax = timeMax,
                    winnersAndLoser,
                )
            ccc.forEachIndexed { index, t ->
                res[index] += t
            }
        }
        return res
    }
}
