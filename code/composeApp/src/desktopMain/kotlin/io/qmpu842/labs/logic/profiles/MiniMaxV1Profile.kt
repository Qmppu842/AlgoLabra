package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.helpers.getListOfIndexesOfMax
import io.qmpu842.labs.helpers.getListOfIndexesOfMin
import io.qmpu842.labs.helpers.summa
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import java.awt.Point
import kotlin.math.max
import kotlin.math.min

class MiniMaxV1Profile(var depth: Int = 10, override var timeLimit: Int = 100) : OpponentProfile() {
    var currentMaxTime = System.currentTimeMillis() + timeLimit

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
//        println("Starting minimax")
        currentMaxTime = System.currentTimeMillis() + timeLimit
        val secondBoard = board.deepCopy()
        val winnersAndLoser = IntArray(board.getWells()) { 0 }
        val moves = secondBoard.getLegalMovesFromMiddleOut()
        for (move in moves) {
            val valuee =
                minimax(
                    board = board.dropLockedToken(move).deepCopy(),
                    depth = depth,
                    maximizingPlayer = true,
                )
            winnersAndLoser[winnersAndLoser.size-move -1] = valuee
        }
//        val endtime = System.currentTimeMillis() -(currentMaxTime - timeLimit)
//        println("Stopping minimax, spend time $endtime ms")
        println("Winners and losers: ${winnersAndLoser.toList()}")

//        return winnersAndLoser.getIndexOfMax()
//        return winnersAndLoser.getListOfIndexesOfMax().random(MyRandom.random)

        //        return winnersAndLoser.getIndexOfMin()
//        return winnersAndLoser.getListOfIndexesOfMin().random(MyRandom.random)

        return if (forSide == -1) {
            winnersAndLoser.getListOfIndexesOfMin().random(MyRandom.random)
        } else {
            winnersAndLoser.getListOfIndexesOfMax().random(MyRandom.random)
        }
    }

    fun minimax(
        board: Board,
        depth: Int,
        maximizingPlayer: Boolean,
        alpha: Int = Int.MIN_VALUE,
        beta: Int = Int.MAX_VALUE,
    ): Int {
        val terminal = board.isLastPlayWinning()

//        if (terminal && maximizingPlayer) return Int.MAX_VALUE
//
//        if (terminal) return Int.MIN_VALUE

        val time = System.currentTimeMillis()

        if (depth == 0 || time >= currentMaxTime || terminal) return lastMovesValue4(board)

        val moves = board.getLegalMovesFromMiddleOut()

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

//                val alpha2 = max(alpha, value)
//                if (beta <= alpha2) break
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

//                val beta2 = min(beta, value)
//                if (beta2 <= alpha) break
            }
            return value
        }
    }

    fun lastMovesValue3(
        board: Board
    ): Int {
        if (board.history.isEmpty()) return 0
        val lastOne = board.history.last()
        val wellSpace = board.getWellSpace(lastOne)
        val startingPoint = Point(lastOne, wellSpace)
//        val sp = board.board.get(startingPoint) ?: return 0
        val neededForWin = board.boardConfig.neededForWin

        var counter = 0

        for (way in Way.entries) {
            val doubleLineOma =
                board.doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = 1,
                    way = way,
                )
            val doubleLineAir =
                board.doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = 0,
                    way = way,
                )
            val doubleLineVihu =
                board.doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = -1,
                    way = way,
                )

            if (doubleLineOma.summa() >= neededForWin) {
                counter += 1000
//                counter = Int.MAX_VALUE
            }else
            if (doubleLineVihu.summa() >= neededForWin) {
                counter -= 1000
//                counter = Int.MIN_VALUE
            }
        }

        return counter /2
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
//                counter += 1000
                counter = Int.MAX_VALUE
            }
//            else
                if (doubleLineVihu.summa() >= neededForWin) {
//                    counter -= 1000
                counter = Int.MIN_VALUE
                }
        }

        return counter
    }
}
