package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.getIndexOfMax
import io.qmpu842.labs.logic.Board
import kotlin.math.max
import kotlin.math.min

class MiniMaxV1Profile : OpponentProfile() {
    var depth = 10

    var timeLimit = 200
    var currentMaxTime = System.currentTimeMillis() + timeLimit


    var parasSyvyys = 0

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
//        println("Starting minimax")
        currentMaxTime = System.currentTimeMillis() + timeLimit
        val secondBoard = board.deepCopy()
        val winnersAndLoser = IntArray(board.getWells()) { 0 }
        val moves = secondBoard.getLegalMoves()
        for (move in moves) {
            val valuee =
                minimax(
                    board = board.dropLockedToken(move).deepCopy(),
                    depth = depth,
                    maximizingPlayer = true,
                )
            winnersAndLoser[move] = valuee
        }
//        val endtime = System.currentTimeMillis() -(currentMaxTime - timeLimit)
//        println("Stopping minimax, spend time $endtime ms")
//        println("saavutettu syvyys: $parasSyvyys")
//        println("Winners and losers: ${winnersAndLoser.toList()}")

        return winnersAndLoser.getIndexOfMax()
    }

    fun minimaxAsHearisticWells(
        board: Board,
        forSide: Int,
    ): IntArray {
        val secondBoard = board.deepCopy()
        val winnersAndLoser = IntArray(board.getWells()) { 0 }
        val moves = secondBoard.getLegalMoves()
        for (move in moves) {
            val valuee =
                minimax(
                    board = board.dropLockedToken(move).deepCopy(),
                    depth = depth,
                    maximizingPlayer = false,
                )
            winnersAndLoser[move] = valuee
        }

        return winnersAndLoser
    }

    fun minimax(
        board: Board,
        depth: Int,
        maximizingPlayer: Boolean,
    ): Int {
//        parasSyvyys = max(parasSyvyys, depth)
        val terminal = board.isLastPlayWinning()

        if (terminal && maximizingPlayer) return Int.MAX_VALUE

        if (terminal) return Int.MIN_VALUE

        val time = System.currentTimeMillis()

        if (depth == 0 || time >= currentMaxTime) return board.lastMovesValue()

        val moves = board.getLegalMoves()

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            for (move in moves) {
                val doTheMove = board.dropLockedToken(move)
                value =
                    max(
                        value,
                        minimax(
                            board = doTheMove.deepCopy(),
                            depth = depth - 1,
                            maximizingPlayer = false,
                        ),
                    )
            }
            return value
        } else {
            var value = Int.MAX_VALUE
            for (move in moves) {
                val doTheMove = board.dropLockedToken(move)
                value =
                    min(
                        value,
                        minimax(
                            board = doTheMove.deepCopy(),
                            depth = depth - 1,
                            maximizingPlayer = true,
                        ),
                    )
            }
            return value
        }
    }
}
