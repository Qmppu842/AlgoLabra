//noinspection DuplicatedCode
package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.heuristics.HeuristicArgs
import io.qmpu842.labs.logic.heuristics.HeuristicFun
import io.qmpu842.labs.logic.heuristics.HeuristicUser
import io.qmpu842.labs.logic.heuristics.zeroHeuristics
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MiniMaxV25Profile(
    override var depth: Int = 10,
    override var timeLimit: Long = TRILLION,
    override val heuristic: HeuristicFun = ::zeroHeuristics,
) : OpponentProfile(),
    HeuristicUser {
    constructor(depth: Int, timeLimit: Int) : this(depth = depth, timeLimit = timeLimit.toLong())

    companion object {
        /**
         *  With this you can handily get multiple versions' of minimax.
         *  Especially handy for tournaments.
         *  Just pass list of all these params:
         *  @param depths what depths should be
         *  @param timeLimits what time limits there should be
         *  @param heuristicFunList what heuristic functions to use.
         *
         * @return list of profile that contain all the possible combinations of these lists.
         */
        operator fun invoke(
            depths: List<Int>,
            timeLimits: List<Long> = listOf(TRILLION),
            heuristicFunList: List<HeuristicFun>,
        ): List<MiniMaxV25Profile> {
            val profiles = mutableListOf<MiniMaxV25Profile>()
            for (depth in depths) {
                for (timeLimit in timeLimits) {
                    for (heuristicFun in heuristicFunList) {
                        profiles.add(MiniMaxV25Profile(depth = depth, timeLimit = timeLimit, heuristic = heuristicFun))
                    }
                }
            }

            return profiles
        }
    }

    override val name: String
        get() {
            val process = "$heuristic".split(" ")[1].split("(")[0]
            return "${this::class.simpleName}($process)"
        }

    var currentMaxTime = Long.MAX_VALUE

        override
    fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit
//        val startingTime = System.currentTimeMillis()
        val lastMoveX = board.getLastMove() ?: -1
        val minimaxResult =
            minimax25(
                board = board,
                depth = depth,
                maximizingPlayer = true,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                forLastSide = -forSide,
                neededForWin = board.boardConfig.neededForWin,
                lastX = lastMoveX,
                lastY = if (lastMoveX != -1) board.getWellSpace(lastMoveX) else -1,
                token = abs(board.getOnTurnToken()),
            )
//        val endTime = System.currentTimeMillis()
//        val totalTime = endTime - startingTime
//        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
//        println("The ${this.name} valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
        return minimaxResult.second
    }



    /**
     * @param forLastSide you should put here the value of last turns side.
     *  Why this way?
     *  Because the first round of minimax does nothing, only after it can do the first moves
     */
    fun minimax25(
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
                heuristic(
                    HeuristicArgs(
                        board = board,
                        x = lastX,
                        y = lastY,
                        forSide = forLastSide * if (maximizingPlayer) -1 else 1,
                        neededForWin = neededForWin,
                    ),
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
                    minimax25(
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
                    minimax25(
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
}
