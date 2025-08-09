package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.BLOCK_WIN
import io.qmpu842.labs.helpers.HEURESTIC_WIN
import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

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
        val startingTime = System.currentTimeMillis()
        val thinn = board.getLastMove() ?: -1
        val minimaxResult =
            minimax2(
                board = board,
                depth = depth,
                maximizingPlayer = true,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                forLastSide = -forSide,
                lastX = thinn,
                lastY = if (thinn != -1) board.getWellSpace(thinn) else -1
            )
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startingTime
        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
        println("The Minimax valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
        return minimaxResult.second
    }

    /**
     * @param forLastSide you should put here the value of last turns side.
     *  Why this way?
     *  Because the first round of minimax does nothing, only after it can do the first moves
     */
    fun minimax2(
        board: Board,
        depth: Int = this.depth,
        maximizingPlayer: Boolean = true,
        alpha: Int = Int.MIN_VALUE,
        beta: Int = Int.MAX_VALUE,
        forLastSide: Int,
        neededForWin: Int = 4,
        lastX: Int = 0,
        lastY: Int = 0,
    ): Pair<Int, Int> {
        val terminal = if (lastX != -1)
            board.doesPlaceHaveWinning(
            x = lastX,
            y = lastY,
            neededForWin = neededForWin
        )else{
            false
        }
//        val hasStopped = board.isAtMaxSize()
        val hasStopped = isBoardFull(board.board)

        if (terminal) {
            return if (!maximizingPlayer) {
                Pair(MINIMAX_WIN + depth, lastX)
            } else {
                Pair(MINIMAX_LOSE - depth, lastX)
            }
        } else if (hasStopped) {
            // In case of Draw
            return Pair(0, lastX)
        }

        val time = System.currentTimeMillis()
//        val y = lastY // board.getWellSpace(lastX)

        if (depth == 0 || time >= currentMaxTime) {
            return Pair(
                lastMovesValue5(
                    board = board,
                    x = lastX,
                    y = lastY,
                    forSide = forLastSide * if (maximizingPlayer) -1 else 1,
                    neededForWin = neededForWin,
                ),
                lastX,
            )
        }

        val moves = board.getLegalsMiddleOutSeq()

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropLockedTokenWithOutHistory(move)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = false,
                        alpha = alpha,
                        beta = beta,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                    )
                board.board[move][things.second] = 0
                if (minied.first > value) {
                    bestMove = move
                    value = minied.first
                }

                val alpha2 = max(alpha, value)
                if (beta <= alpha2) break
            }
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropLockedTokenWithOutHistory(move)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = true,
                        alpha = alpha,
                        beta = beta,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                    )
                board.board[move][things.second] = 0
                if (minied.first < value) {
                    bestMove = move
                    value = minied.first
                }

                val beta2 = min(beta, value)
                if (beta2 <= alpha) break
            }
            return Pair(value, bestMove)
        }
    }

    fun isBoardFull(board: Array<IntArray>): Boolean {
        val size = board.size
        for (aaa in 0..<size) {
            if (board[aaa][0] == 0) return false
        }
        return true
    }

    fun lastMovesValue5(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        var counter = 0

//        for (way in Way.entries) {
        for (way in Way.half) {
            var vali = 0
            val result: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = forSide,
                    way = way,
                )
//            val opposite = way.getOpposite()
            val opposite = Way.opp[way.ordinal]
            val result2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
            val doubleLineOma = result + result2

//            println("doubleLineOma: ${doubleLineOma.summa()}")
//            val doubleLineAir =
//                board.doubleLineWithJumpStart(
//                    current = startingPoint,
//                    sign = 0,
//                    way = way,
//                )
// //            println("doubleLineAir: ${doubleLineAir.summa()}")
//            val doubleLineVihu =
//                board.doubleLineNoJumpStart(
//                    current = startingPoint,
//                    sign = -forSide,
//                    way = way,
//                )

            val resultV: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = -forSide,
                    way = way,
                )
            val resultV2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = -forSide,
                    way = opposite,
                )
            val doubleLineVihu2 = resultV + resultV2
//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            if (doubleLineOma >= neededForWin) {
                vali = HEURESTIC_WIN
//                counter = Int.MAX_VALUE
//            } else if (doubleLineVihu.summa() >= neededForWin) {
//                    counter = HEURESTIC_LOSE
//                counter = Int.MIN_VALUE
            } else if (doubleLineVihu2 >= neededForWin - 1) {
                vali = BLOCK_WIN
            }

            if (abs(vali) > counter) {
                counter = vali
            }
        }
        return counter
    }
}
