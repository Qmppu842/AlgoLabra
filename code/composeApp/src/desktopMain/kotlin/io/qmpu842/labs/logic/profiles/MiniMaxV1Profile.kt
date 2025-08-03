package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.*
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import java.awt.Point

class MiniMaxV1Profile(
    var depth: Int = 10,
    override var timeLimit: Long = 100L,
) : OpponentProfile() {

    constructor(depth: Int, timeLimit: Int) : this(depth = depth, timeLimit = timeLimit.toLong())

    var currentMaxTime = Long.MAX_VALUE

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit

        val thing = minimax2(
            board = board,
            depth = depth,
            maximizingPlayer = true,
            alpha = 0,
            beta = 0,
            forLastSide = -forSide
        )
//        println("The thing: $thing")
        return thing.second
    }

    /**
     * @param forLastSide you should put here the value of last turns side.
     *  Why this way?
     *  Because the first round of minimax does nothing, only after it can do the first moves
     */
    fun minimax2(
        board: Board,
        depth: Int,
        maximizingPlayer: Boolean,
        alpha: Int,
        beta: Int,
        forLastSide: Int,
    ): Pair<Int, Int> {
        val terminal = board.isLastPlayWinning()
        val hasStopped = board.isAtMaxSize()
        val lastMove = board.getLastMove() ?: 0


        if (terminal) {
            return if (!maximizingPlayer) {
                Pair(MINIMAX_WIN + depth, lastMove)
            } else {
                Pair(MINIMAX_LOSE - depth, lastMove)
            }
        } else if (hasStopped) {
            //On case of Draw
            return Pair(0, lastMove)
        }

        val time = System.currentTimeMillis()
        val y = board.getWellSpace(lastMove)

        if (depth == 0 || time >= currentMaxTime) return Pair(lastMovesValue5(
            board = board,
            x = lastMove,
            y = y,
            forSide = forLastSide * if (maximizingPlayer) -1 else 1
        ),lastMove)

//        if (depth == 0 || time >= currentMaxTime) return Pair(-11, lastMove)

        val moves = board.getLegalMovesFromMiddleOut()

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = moves.first()
            for (move in moves) {
                val minied =
                    minimax2(
                        board = board.deepCopy().dropLockedToken(move),
                        depth = depth - 1,
                        maximizingPlayer = false,
                        alpha = alpha,
                        beta = beta,
                        forLastSide = -forLastSide,
                    )
                if (minied.first > value) {
                    bestMove = move
                    value = minied.first
                }

//                val alpha2 = max(alpha, value)
//                if (beta <= alpha2) break
            }
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = moves.first()
            for (move in moves) {
                val minied =
                    minimax2(
                        board = board.deepCopy().dropLockedToken(move),
                        depth = depth - 1,
                        maximizingPlayer = true,
                        alpha = alpha,
                        beta = beta,
                        forLastSide = -forLastSide,
                    )
                if (minied.first < value) {
                    bestMove = move
                    value = minied.first
                }

//                val beta2 = min(beta, value)
//                if (beta2 <= alpha) break
            }
            return Pair(value, bestMove)
        }
    }

    fun lastMovesValue5(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
    ): Int {
        val startingPoint = Point(x, y)
        val neededForWin = board.boardConfig.neededForWin

        var counter = 0

        for (way in Way.entries) {
            val doubleLineOma =
                board.doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = forSide,
                    way = way,
                )
//            println("doubleLineOma: ${doubleLineOma.summa()}")
            val doubleLineAir =
                board.doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = 0,
                    way = way,
                )
//            println("doubleLineAir: ${doubleLineAir.summa()}")
            val doubleLineVihu =
                board.doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = -forSide,
                    way = way,
                )

            val doubleLineVihu2 =
                board.doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = -forSide,
                    way = way,
                )
//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            if (doubleLineOma.summa() >= neededForWin) {
                counter = HEURESTIC_WIN
//                counter = Int.MAX_VALUE
            } else if (doubleLineVihu.summa() >= neededForWin) {
                    counter = HEURESTIC_LOSE
//                counter = Int.MIN_VALUE
            } else if (doubleLineVihu2.summa() >= neededForWin -1){
                counter = BLOCK_WIN
            }
        }
        return counter
    }
}