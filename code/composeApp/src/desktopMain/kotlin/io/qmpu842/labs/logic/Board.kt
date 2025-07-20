package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import io.qmpu842.labs.helpers.summa
import java.awt.Point
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

    fun dropLockedToken(column: Int): Board = dropToken(column, history.size * if (history.size % 2 == 0) -1 else 1)

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
        if (toRemove == -1) return this
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
        return Board(board2, listOf(-1))
    }

    /**
     *  @return the zeroes still in the well
     *  That -1 is equal to next free spaces index
     */
    fun getWellSpace(column: Int): Int = board[column].count({ it == 0 })

    fun getHighestSpaceIndex(column: Int) = getWellSpace(column) - 1

    fun isLastPlayWinning(neededForWin: Int = 4): Boolean {
        val lastOne = history.last()
        if (lastOne == -1) return false
        val wellSpace = getWellSpace(lastOne)
        val startingPoint = Point(lastOne, wellSpace)
        val sp = board.get(startingPoint) ?: return false

        for (way in Way.entries) {
            val doubleLine =
                doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = sp.sign,
                    way = way,
                )
            if (doubleLine.summa() >= neededForWin) return true
        }
        return false
    }

    fun doubleLineNoJumpStart(
        current: Point,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Pair<Int, Int> {
        val result =
                checkLine(
                    current = current,
                    sign = sign,
                    way = way,
                    length = length,
                )

        val antiSp = board.next(current, way.getOpposite())
        var result2 = 0
        if (antiSp != null) {
            result2 =
                checkLine(
                    current = antiSp,
                    sign = sign,
                    way = way.getOpposite(),
                )
        }
        return Pair(result, result2)
    }

    fun doubleLineWithJumpStart(
        current: Point,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Pair<Int, Int> {
        var result = 0
        val spJumped =
            board.next(current, way)
        if (spJumped != null) {
            result =
                checkLine(
                    current = spJumped,
                    sign = sign,
                    way = way,
                    length = length,
                )
        }

        val antiSp = board.next(current, way.getOpposite())
        var result2 = 0
        if (antiSp != null) {
            result2 =
                checkLine(
                    current = antiSp,
                    sign = sign,
                    way = way.getOpposite(),
                )
        }
        return Pair(result, result2)
    }


    fun checkLine(
        current: Point,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Int {
        var realLength = length
        val currentValue = board.get(current)
        if (currentValue == null) return realLength

        if (currentValue.sign != sign) return realLength

        realLength += 1

        val next = board.next(current, way)
        if (next == null) return realLength

        return checkLine(
            current = next,
            sign = sign,
            way = way,
            length = realLength,
        )
    }

    fun countInLineForWin(
        current: Point,
        sign: Int,
        way: Way,
        length: Int = 0,
        aaa: Triple<Int, Int, Int>
    ): Int {
        var realLength = length
        val currentValue = board.get(current)
        if (currentValue == null) return realLength

        if (currentValue.sign != sign) return realLength

        realLength += 1

        val next = board.next(current, way)
        if (next == null) return realLength

        return checkLine(
            current = next,
            sign = sign,
            way = way,
            length = realLength,
        )
    }

    fun startingPoints(): MutableList<Point> {
        val startingPoints = mutableListOf<Point>()

        for (move in getLegalMoves()) {
            val startIndex = getHighestSpaceIndex(move)
            startingPoints.add(Point(move, startIndex))
        }
        return startingPoints
    }

    fun deepCopy(): Board {
        val board2 =
            Array(board.size) {
                IntArray(board[it].size) { 0 }
            }
        for (x in board.indices) {
            for (y in board[x].indices) {
                board2[x][y] = board[x][y]
            }
        }
        val hist = mutableListOf<Int>()
        for (histe in history) {
            hist.add(histe)
        }

        return Board(board2, hist)
    }

    fun lastMovesValue(neededForWin: Int = 4): Int {
        val lastOne = history.last()
        if (lastOne == -1) return 0
        val wellSpace = getWellSpace(lastOne)
        val startingPoint = Point(lastOne, wellSpace)
        val sp = board.get(startingPoint) ?: return 0

        var counter = 0

        for (way in Way.entries) {
            val doubleLineOma =
                doubleLineNoJumpStart(
                    current = startingPoint,
                    sign = sp.sign,
                    way = way,
                )
            val doubleLineAir =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = 0,
                    way = way,
                )
            val doubleLineVihu =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = -sp.sign,
                    way = way,
                )
            val valivaihe = 0
            +doubleLineOma.summa()
//            +doubleLineAir.summa() / 2
//            -doubleLineVihu.summa()
            counter = valivaihe
        }

        return counter
    }
}
