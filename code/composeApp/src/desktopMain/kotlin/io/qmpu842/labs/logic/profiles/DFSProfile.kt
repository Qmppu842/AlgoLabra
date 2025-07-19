package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board
import kotlin.math.round

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
        println("Time on starting: ${eka}")
        val joku =
            resulting(
                board = board.copy(),
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
        println("Time spend: ${round(diff/1000f)}s")

        return indexe
    }

    fun resulting(
        board: Board,
        forSide: Int,
        timeMax: Long = System.currentTimeMillis() + timeLimit,
    ): IntArray {
        val results = IntArray(board.getWells()) { 0 }
        val moves = board.getLegalMoves()
        val timeNow = System.currentTimeMillis()
        println("results: ${results.toList()}")
        println("moves: $moves")
        while (moves.isNotEmpty() && !board.isLastPlayWinning() && timeNow < timeMax) {
            val asd = moves.removeAt(rand.nextInt(moves.size))
            val boarde = board.dropToken(asd, forSide)
            val voitto = boarde.isLastPlayWinning()
            if (voitto) {
                results[asd] += if (forSide == side) 1 else -1
            } else {
                val ccc =
                    resulting(
                        board = boarde.copy(),
                        forSide = -forSide,
                        timeMax = timeMax,
                    )
                ccc.forEachIndexed { index, t ->
                    results[index] += t
                }
            }
        }
        return results
    }
}
