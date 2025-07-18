package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import java.awt.Point
import kotlin.math.min
import kotlin.math.sign

data class Board(
    val board: Array<IntArray>,
    val history: List<Int> = listOf(-1),
) {
    /**
     * @param boardWidth kuinka monta kuilua, same as x
     * @param boardHeight kuinka monta paikkaa per kuilu, same as y
     */
    constructor(
        boardWidth: Int = 7,
        boardHeight: Int = 6,
    ) : this(Array(boardWidth) { IntArray(boardHeight) { 0 } })

    /**
     * @return the last token put in to the board
     */
    fun getLastMove(): Int = history.last()

    /**
     * @return list of all the wells with space
     * Each entry is index of well with space
     */
    fun getLegalMoves(): MutableList<Int> {
        val result = mutableListOf<Int>()
        board.forEachIndexed { index, ints ->
            if (ints.first() == 0) result.add(index)
        }
        return result
    }

    /**
     * Drops the token to the well
     * @param column the well to drop in
     * @param token the token to drop
     *
     * @return -1 if no space otherwise the height the token was put in
     */
    fun dropToken(
        column: Int,
        token: Int,
    ): Board {
        val thing = board[column]
        var lastZero = -1
        thing.forEachIndexed { index, t ->
            if (t == 0) {
                lastZero = index
            }
        }
        if (lastZero != -1) {
            board[column][lastZero] = token
        }
        return this.copy(
            board,
            history + column,
        )
    }

    fun undoLastMove(): Board {
        val lastWell = history.last()
        if (lastWell == -1) return this

        val thing = board[lastWell]
        var toRemove = -1
        thing.forEachIndexed { index, t ->
            if (toRemove != -1) return@forEachIndexed

            if (t != 0) {
                toRemove = index
            }
        }
        board[lastWell][toRemove] = 0
        return this.copy(
            board,
            history.take(history.size - 1),
        )
    }

    /**
     * @return width of the board
     */
    fun getWells() = board.size

    fun clear(): Board {
        val board2 = board
        for (x in board.indices) {
            for (y in board[x].indices) {
                board2[x][y] = 0
            }
        }
        return this.copy(board = board2)
    }

    /**
     *  @return the zeroes still in the well
     *  That -1 is equal to next free spaces index
     */
    fun getWellSpace(column: Int): Int = board[column].count({ it == 0 })

    fun isLastPlayWinning(neededForWin: Int = 4): Boolean {
        val lastOne = history.last()
        val wellSpace = min(getWellSpace(lastOne), board[lastOne].size - 1)
        val startingPoint = Point(lastOne, wellSpace)
        val spSign = (board.get(startingPoint) ?: return false).sign
        for (way in Way.entries) {
            val result =
                checkLine(
                    current = startingPoint,
                    sign = spSign,
                    way = way,
                )
            val antiSp = board.next(startingPoint, way.getOpposite())
            var result2 = 0
            if (antiSp != null) {
                result2 =
                    checkLine(
                        current = antiSp,
                        sign = spSign,
                        way = way.getOpposite(),
                    )
            }

            if (way.y == 1) result2 += 1
            if (result + result2 >= neededForWin) return true
        }
        return false
    }

    fun checkLine(
        current: Point,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Int {
        val currentValue = board.get(current)
        if (currentValue == null) return length

        if (currentValue.sign != sign) return length

        val next = board.next(current, way)
        if (next == null) return length

        return checkLine(
            current = next,
            sign = sign,
            way = way,
            length = length + 1,
        )
    }
}
