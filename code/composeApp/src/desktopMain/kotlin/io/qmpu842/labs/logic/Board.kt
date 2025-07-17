package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import java.awt.Point
import kotlin.math.abs
import kotlin.math.min

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


    fun isLastPlayWinning(neededForWin : Int = 4): Boolean {
        val lastOne = history.last()
        val wellSpace = min(getWellSpace(lastOne), board[lastOne].size-1)
        val startingPoint = Point(lastOne, wellSpace)
        val ways = Way.entries
        var result: Boolean
        for (way in ways) {
            result =
                checker(
                    currentPoint = startingPoint,
                    way = way,
                    counter = 1,
                    value = 0,
                    maxCounter = neededForWin,
                )
            if (result) return true
        }
        return false
    }

    /**
     * This is the actual win checker that follows
     * @param way until there is no reason or conclusion has been reached.
     *
     * @param currentPoint is the starting point for the investigation
     * @param way is the direction to go.
     * @param counter is how many steps we have followed this path or how many point we have researched.
     * @param maxCounter is how many consecutive steps are needed for win.
     */
    fun checker(
        currentPoint: Point,
        way: Way = Way.Up,
        counter: Int = 1,
        value: Int = 0,
        maxCounter: Int = 4,
    ): Boolean {
        val currentPointValue = board.get(currentPoint) ?: return false

        val valueSum = value + currentPointValue
        if (abs(value) >= abs(valueSum)) return false

        val next = board.next(currentPoint, way)
        if (next == null && counter < maxCounter) return false

        if (counter < maxCounter) {
            check(next != null) { "Next should not be null at this point of checker" }
            return checker(
                currentPoint = next,
                way = way,
                counter = counter + 1,
                value = valueSum,
                maxCounter = maxCounter,
            )
        }
        return true
    }
}
