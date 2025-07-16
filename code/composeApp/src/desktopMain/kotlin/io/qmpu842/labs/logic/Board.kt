package io.qmpu842.labs.logic

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
//            history.add(column)
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
//        history.removeLast()
//        val removed = board[lastWell][toRemove]
        board[lastWell][toRemove] = 0
        return this.copy(
            board,
            history.take(history.size - 1),
        )
    }

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
     */
    fun getWellSpace(column: Int): Int = board[column].count({ it == 0 })


    fun isLastPlayWinning(neededForWin : Int = 4): Boolean {
        val lastOne = history.last()
        val wellSpace = min(getWellSpace(lastOne), board[lastOne].size-1)
        val startingPoint = Point(lastOne, wellSpace)
        val ways = Way.entries.toTypedArray()
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

    fun checker(
        currentPoint: Point,
        way: Way = Way.Up,
        counter: Int = 0,
        value: Int = 0,
        maxCounter: Int = 4,
    ): Boolean {
        val currentPointValue = board.get(currentPoint) ?: return false

        val valueSum = value + currentPointValue
        if (abs(value) >= abs(valueSum)) return false

        val next = board.next(currentPoint, way)
        if (next == null && counter < maxCounter) return false

        if (counter < maxCounter) {
            return checker(
                currentPoint = next!!,
                way = way,
                counter = counter + 1,
                value = valueSum,
                maxCounter = maxCounter,
            )
        }
        return true
    }
}

private fun Array<IntArray>.get(current: Point): Int? {
    val x = current.x
    if (x !in 0..<this.size) return null

    val y = current.y
    if (y !in 0..<this[x].size) return null

    return this[current.x][current.y]
}

private fun Array<IntArray>.next(
    current: Point,
    way: Way,
): Point? {
    val x = current.x + way.x
    if (x !in 0..<this.size) return null

    val y = current.y + way.y
    if (y !in 0..<this[x].size) return null
    return Point(x, y)
}

/**
 * Right +x
 * Left  -x
 * Up    -y
 * Down  +y
 */
enum class Way(
    val x: Int,
    val y: Int,
) {
    Up(0, -1),
    UpRight(1, -1),
    Right(1, 0),
    RightDown(1, 1),
    Down(0, 1),
    DownLeft(-1, 1),
    Left(-1, 0),
    LeftUp(-1, -1),
}
