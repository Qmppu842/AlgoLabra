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
import kotlin.math.round

class MiniMaxV3Profile(
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
        ): List<MiniMaxV3Profile> {
            val profiles = mutableListOf<MiniMaxV3Profile>()
            for (depth in depths) {
                for (heuristicFun in heuristicFunList) {
                    if (depth == -1) {
                        for (timeLimit in timeLimits) {
                            profiles.add(
                                MiniMaxV3Profile(
                                    depth = depth,
                                    timeLimit = timeLimit,
                                    heuristic = heuristicFun,
                                ),
                            )
                        }
                    } else {
                        profiles.add(
                            MiniMaxV3Profile(
                                depth = depth,
                                heuristic = heuristicFun,
                            ),
                        )
                    }
                }
            }

            return profiles
        }
    }

    private val process = "$heuristic".split(" ")[1].split("(")[0]
    override val name: String
        get() {
            return "${this::class.simpleName}($process), depth $depth, timelimit $timeLimit"
        }

    var currentMaxTime = Long.MAX_VALUE

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        println("---next move---")
        currentMaxTime = System.currentTimeMillis() + timeLimit
        return if (depth == -1) {
            iterativeDeepening(board, forSide)
        } else {
            justMinimaxNoDeepening(board = board, forSide = forSide)
        }
    }

    var bestMoveSet = HashMap<String, Int>()

    init {

        bestMoveSet = HashMap()
//        bestMoveSet[board.toString()] = board.getLegalsMiddleOutSeq().first()
    }

    override fun resetSelf(): OpponentProfile {
        bestMoveSet = HashMap()
        return super.resetSelf()
    }

    fun iterativeDeepening(
        board: Board,
        forSide: Int,
    ): Int {
        val lastMoveX = board.getLastMove() ?: -1
        var time = System.currentTimeMillis()
        var currentMaxDepth = 1
        val amountOfSpaceLeft = board.boardConfig.width * board.boardConfig.height - board.history.size + 1

//        bestMoveSet = HashMap()
//        bestMoveSet[board.toString()] = board.getLegalsMiddleOutSeq().first()

        val currentBestMove = bestMoveSet[board.toString()]
        if (currentBestMove == null) {
            bestMoveSet[board.toString()] = board.getLegalsMiddleOutSeq().first()
        }
        var minimaxResult = Pair(0, 0)
//        var maxDepth = 0
        while (currentMaxDepth <= amountOfSpaceLeft && time < currentMaxTime) {
//            maxDepth = max(maxDepth, depthi)
//            println("now running to depth: $depthi")
            minimaxResult =
                minimax2(
                    board = board,
                    depth = currentMaxDepth,
                    maximizingPlayer = true,
                    alpha = Int.MIN_VALUE,
                    beta = Int.MAX_VALUE,
                    forLastSide = -forSide,
                    neededForWin = board.boardConfig.neededForWin,
                    lastX = lastMoveX,
                    lastY = if (lastMoveX != -1) board.getWellSpace(lastMoveX) else -1,
                    token = abs(board.getOnTurnToken()),
                )
            bestMoveSet[board.toString()] = minimaxResult.second
            currentMaxDepth++
            time = System.currentTimeMillis()
        }
        println("Achieved depth: $currentMaxDepth")
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - (currentMaxTime - this.timeLimit)
        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $currentMaxDepth")
        println("The ${this.name} valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
        return bestMoveSet[board.toString()]!!
    }

    fun justMinimaxNoDeepening(
        board: Board,
        forSide: Int,
    ): Int {
        val startingTime = System.currentTimeMillis()
        val lastMoveX = board.getLastMove() ?: -1
        val minimaxResult =
            minimax2(
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
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startingTime
        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
        println("The ${this.name} valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
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
        val bestMoveOfAllTime = bestMoveSet[board.toString()]
        val moves =
            if (bestMoveOfAllTime == null) board.getLegalsMiddleOutSeq() else sequenceOf(bestMoveOfAllTime) + board.getLegalsMiddleOutSeq()
//        val moves = board.getLegalsMiddleOutSeq()
        var alpha2 = alpha
        var beta2 = beta

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
//                if (move == bestMoveOfAllTime) continue
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
            bestMoveSet[board.toString()] = bestMove
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
//                if (move == bestMoveOfAllTime) continue
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
            bestMoveSet[board.toString()] = bestMove
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
