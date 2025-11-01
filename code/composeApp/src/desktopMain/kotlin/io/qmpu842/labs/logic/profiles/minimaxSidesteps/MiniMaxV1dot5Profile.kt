package io.qmpu842.labs.logic.profiles.minimaxSidesteps

import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.*

class MiniMaxV1dot5Profile(
    override var depth: Int = 10,
    override var timeLimit: Long = TRILLION,
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
                neededForWin = board.boardConfig.neededForWin,
                lastX = thinn,
                lastY = if (thinn != -1) board.getWellSpace(thinn) else -1,
                token = abs(board.getOnTurnToken()),
            )
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startingTime
//        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
//        println("The ${this.name} valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
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
        token: Int = 1,
    ): Pair<Int, Int> {
        val terminal =
            board.doesPlaceHaveWinning(
                x = lastX,
                y = lastY,
                neededForWin = neededForWin,
            )
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
        var alpha2 = alpha
        var beta2 = beta

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = false,
                        alpha = alpha2,
                        beta = beta2,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                        token = token + 1,
                    )
                board.board[move][things.second] = 0
                if (minied.first > value) {
                    bestMove = move
                    value = minied.first
                }

                alpha2 = max(alpha2, value)
                if (beta2 <= alpha2) break
            }
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = true,
                        alpha = alpha2,
                        beta = beta2,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                        token = token + 1,
                    )
                board.board[move][things.second] = 0
                if (minied.first < value) {
                    bestMove = move
                    value = minied.first
                }

                beta2 = min(beta2, value)
                if (beta2 <= alpha2) break
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
        val w = board.boardConfig.width
        val h = board.boardConfig.height
        val size = sqrt(0.0 + w * w + h * h).toInt() + 2
        val omatLinjat = IntArray(size)
        val vihuLinjat = IntArray(size)
        val ilmaLinjat = IntArray(size)
        val ilmaLinjatOpp = IntArray(size)

        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
//            omat linjat
            val resOma1: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = forSide,
                    way = way,
                )
            val resOma2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
            val omaSum = resOma1 + resOma2
            omatLinjat[omaSum] += 1

            //        vihu linjat
            val resVih1: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = -forSide,
                    way = way,
                )
            val resVih2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = -forSide,
                    way = opposite,
                )
            val vihuSum = resVih1 + resVih2
            vihuLinjat[vihuSum] += 1

            if (omaSum < 1) continue
//            ilma linjat
            val resAir1: Int =
                board.checkLine(
                    x = x + (resOma1 * way.x),
                    y = y + (resOma1 * way.y),
                    sign = 0,
                    way = way,
                )
            if (resAir1 > 0) {
                ilmaLinjat[omaSum] += 1
            }
            val resAir2: Int =
                board.checkLine(
                    x = x + opposite.x + (resOma2 * opposite.x),
                    y = y + opposite.y + (resOma2 * opposite.y),
                    sign = 0,
                    way = opposite,
                )
            if (resAir2 > 0) {
                ilmaLinjatOpp[omaSum] += 1
            }
        }
        var omaCounter = 0
        var vihuCounter = 0
        for (i in size - 1 downTo 0) {
            val ekaIlma = ilmaLinjat[i]
            val tokaIlma = ilmaLinjatOpp[i]
            val oma = omatLinjat[i]
            if (ekaIlma > 0 && tokaIlma > 0) {
                omaCounter += (oma * (10f.pow(i)).toInt()) * 2
            } else {
                omaCounter += oma * (10f.pow(i)).toInt()
            }
            val vihu = vihuLinjat[i]
            vihuCounter += -(vihu * (10f.pow(i)).toInt())
        }
        return if (abs(omaCounter) >= abs(vihuCounter)) {
            omaCounter
        } else {
            vihuCounter
        }
    }
}
