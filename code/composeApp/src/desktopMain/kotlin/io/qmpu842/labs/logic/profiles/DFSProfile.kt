package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board
import kotlin.math.sign

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
        println("Time on starting: $eka")
        val joku =
            resulting(
                board = board.deepCopy(),
                forSide = forSide,
                timeMax = System.currentTimeMillis() + timeLimit
        )
        println("joku: ${joku.toList()}")

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
        println("Done")
//        println("Time spend: ${round(diff/1000f)}s")
        println("Time spend2: ${diff} ms")

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
            println("times up")
            return winnersAndLoser
        }

        val voittaja = board.isLastPlayWinning()
        if (voittaja) {
            val laste = board.history.last()
            if (laste.sign == forSide) {
                winnersAndLoser[laste] -= 1
            } else {
                winnersAndLoser[laste] += 1
            }
            return winnersAndLoser
        }

        val moves = board.getLegalMoves()
//        if(moves.isEmpty()){
//            return winnersAndLoser
//        }

//        val results = IntArray(board.getWells()) { 0 }
//        println("winnersAndLoser: ${winnersAndLoser.toList()}")
//        println("moves: $moves")

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

//        while (moves.isNotEmpty() && !board.isLastPlayWinning() && timeNow < timeMax) {
//            val asd = moves.removeAt(rand.nextInt(moves.size))
//            val boarde = board.dropToken(asd, forSide)
//            val voitto = boarde.isLastPlayWinning()
//            if (voitto) {
//                winnersAndLoser[asd] += if (forSide == side) 1 else -1
//            } else {
//                val ccc =
//                    resulting(
//                        board = boarde.copy(),
//                        forSide = -forSide,
//                        timeMax = timeMax,
//                        winnersAndLoser
//                    )
//                ccc.forEachIndexed { index, t ->
//                    winnersAndLoser[index] += t
//                }
//            }
//        }
//        return winnersAndLoser
    }
}
