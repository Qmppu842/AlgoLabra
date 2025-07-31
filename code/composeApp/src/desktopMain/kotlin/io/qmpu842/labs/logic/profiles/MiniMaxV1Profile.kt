package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.*
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import java.awt.Point
import kotlin.math.max
import kotlin.math.min

class MiniMaxV1Profile(var depth: Int = 10, override var timeLimit: Int = 100) : OpponentProfile() {
//    var currentMaxTime = System.currentTimeMillis() + timeLimit
    var currentMaxTime = Long.MAX_VALUE

    fun nextMove1(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit
        val winnersAndLoser = collectMinimax(board)
        println("Winners and losers: ${winnersAndLoser.toList()}")

        return if (forSide == -1) {
            winnersAndLoser.getListOfIndexesOfMin().random(MyRandom.random)
        } else {
            winnersAndLoser.getListOfIndexesOfMax().random(MyRandom.random)
        }
    }

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit

        return minimax2(
            board = board,
            depth = depth,
            maximizingPlayer = true,
            alpha = 0,
            beta = 0,
            forLastSide = -forSide
        ).second
    }

    fun collectMinimax(board: Board, depth: Int = this.depth, maximizingPlayer: Boolean = true): IntArray {
        val winnersAndLoser = IntArray(board.getWells()) { 0 }
        val moves = board.getLegalMovesFromMiddleOut()
//        val moves = board.getLegalMoves()
        for (move in moves) {
            val kopiolauta = board.deepCopy()
            val collected =
                minimax(
                    board = kopiolauta.dropLockedToken(move).deepCopy(),
                    depth = depth,
                    maximizingPlayer = !maximizingPlayer,
                )
//            winnersAndLoser[winnersAndLoser.size - move - 1] = collected
            winnersAndLoser[move] = collected
        }
        return winnersAndLoser
    }

    fun minimax(
        board: Board,
        depth: Int,
        maximizingPlayer: Boolean,
        alpha: Int = Int.MIN_VALUE,
        beta: Int = Int.MAX_VALUE,
    ): Int {
        val terminal = board.isLastPlayWinning()
        val hasStopped = board.isAtMaxSize()

        if (terminal && maximizingPlayer && hasStopped) return HUNDRED_K + depth

        if (terminal && hasStopped) return -HUNDRED_K - depth

        val time = System.currentTimeMillis()

        if (depth == 0 || time >= currentMaxTime) return lastMovesValue4(board)

        val moves = board.getLegalMovesFromMiddleOut()

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            for (move in moves) {
                val kopiolauta = board.deepCopy()
                val doTheMove = kopiolauta.dropLockedToken(move)
                value =
                    max(
                        value,
                        minimax(
                            board = doTheMove,
                            depth = depth - 1,
                            maximizingPlayer = false,
                        ),
                    )

//                val alpha2 = max(alpha, value)
//                if (beta <= alpha2) break
            }
            return value
        } else {
            var value = Int.MAX_VALUE
            for (move in moves) {
                val kopiolauta = board.deepCopy()
                val doTheMove = kopiolauta.dropLockedToken(move)
                value =
                    min(
                        value,
                        minimax(
                            board = doTheMove,
                            depth = depth - 1,
                            maximizingPlayer = true,
                        ),
                    )

//                val beta2 = min(beta, value)
//                if (beta2 <= alpha) break
            }
            return value
        }
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
        val lastMove = board.getLastMove() ?: board.getLegalMovesFromMiddleOut().first()

        if (terminal && maximizingPlayer && hasStopped) return Pair(HUNDRED_K + depth, lastMove)

        if (terminal && hasStopped) return Pair(-HUNDRED_K - depth, lastMove)

        val time = System.currentTimeMillis()
        val y = board.getWellSpace(lastMove)

        if (depth == 0 || time >= currentMaxTime) return Pair(lastMovesValue5(
            board = board,
            x = lastMove,
            y = y,
            forSide = forLastSide
        ),lastMove)

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
                        alpha = 0,
                        beta = 0,
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
                        alpha = 0,
                        beta = 0,
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



    fun lastMovesValue4(
        board: Board
    ): Int {
        if (board.history.isEmpty()) return 0
        val lastOne = board.history.last()
        val wellSpace = board.getWellSpace(lastOne)
        val startingPoint = Point(lastOne, wellSpace)
        val neededForWin = board.boardConfig.neededForWin

//        println("board: ${board.board.contentDeepToString()}")
//
//        println("needed for win: $neededForWin")

        var counter = 0

        for (way in Way.entries) {
//            println("Looking at way $way")
            val doubleLineOma =
                board.doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = 1,
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
                    sign = -1,
                    way = way,
                )
//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            if (doubleLineOma.summa() >= neededForWin) {
                counter = MAX_WIN
//                counter = Int.MAX_VALUE
            } else {
                if (doubleLineVihu.summa() >= neededForWin) {
                    counter = MIN_LOSE
//                counter = Int.MIN_VALUE
                }
            }
        }
        return counter
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
//            println("Looking at way $way")
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
//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            if (doubleLineOma.summa() >= neededForWin) {
                counter = MAX_WIN
//                counter = Int.MAX_VALUE
            } else {
                if (doubleLineVihu.summa() >= neededForWin) {
                    counter = MIN_LOSE
//                counter = Int.MIN_VALUE
                }
            }
        }
        return counter
    }
}