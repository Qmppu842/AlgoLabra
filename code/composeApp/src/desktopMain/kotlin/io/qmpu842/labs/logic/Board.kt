package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.get
import io.qmpu842.labs.helpers.next
import io.qmpu842.labs.helpers.summa
import java.awt.Point
import kotlin.math.sign

data class Board(
    val board: Array<IntArray>,
    val boardConfig: BoardConfig,
    val history: List<Int> = listOf(),
) {
    /**
     * @param boardWidth kuinka monta kuilua, same as x
     * @param boardHeight kuinka monta paikkaa per kuilu, same as y
     */
    constructor(
        boardWidth: Int = 7,
        boardHeight: Int = 6,
        neededForWin: Int = 4,
    ) : this(
        board = Array(boardWidth) { IntArray(boardHeight) { 0 } },
        boardConfig =
            BoardConfig(
                width = boardWidth,
                height = boardHeight,
                neededForWin = neededForWin,
            ),
    )

    /**
     * This is dumb...
     */
    constructor(boardConfig: BoardConfig) : this(
        boardHeight = boardConfig.height,
        boardWidth = boardConfig.width,
        neededForWin = boardConfig.neededForWin,
    )

    constructor(board: Array<IntArray>) : this(board = board, boardConfig = BoardConfig())

    /**
     * @return the last token put in to the board
     */
    fun getLastMove(): Int? = history.lastOrNull()

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
     * @return list of all the wells with space
     * Each entry is index of well with space
     */
    fun getLegalMovesFromMiddleOut(): MutableList<Int> {
        val moves = getLegalMoves()
        val result2 = mutableListOf<Int>()
        for (i in 0..<moves.size) {
            val nouse = i
            val laskeva = moves.size - i - 1
            if (nouse >= laskeva) {
                if (moves.size % 2 == 1) {
                    result2.add(moves[nouse])
                }
                break
            }
            result2.add(moves[nouse])
            result2.add(moves[laskeva])
        }
        result2.reverse()
        return result2
    }

    fun dropLockedToken(column: Int): Board = dropToken(column, getOnTurnToken())

    /**
     * @return next turn token so on turn 32 token is -32
     */
    fun getOnTurnToken(): Int = history.size * if (history.size % 2 == 0) 1 else -1

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
            boardConfig,
            history + column,
        )
    }

    fun undoLastMove(): Board {
        if (history.isEmpty()) return this
        val lastWell = history.last()
//        if (lastWell == -1) return this

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
            boardConfig,
            history.take(history.size - 1),
        )
    }

    /**
     * @return width of the board
     */
    fun getWells() = boardConfig.width

    fun clear(): Board = Board(boardConfig)

    /**
     *  @return the zeroes still in the well
     *  That -1 is equal to next free spaces index
     */
    fun getWellSpace(column: Int): Int = board[column].count({ it == 0 })

    fun getHighestSpaceIndex(column: Int) = getWellSpace(column) - 1

    fun isLastPlayWinning(neededForWin: Int = 4): Boolean {
        if (history.isEmpty()) return false
        val lastOne = history.last()
//        if (lastOne == -1) return false
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
        val copiedHistory = mutableListOf<Int>()
        for (move in history) {
            copiedHistory.add(move)
        }

        return Board(board2, boardConfig, copiedHistory)
    }

    fun lastMovesValue(neededForWin: Int = boardConfig.neededForWin): Int {
        if (history.isEmpty()) return 0
        val lastOne = history.last()
//        if (lastOne == -1) return 0
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
//            println("doubleLineOma: ${doubleLineOma.summa()}")
            val doubleLineAir =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = 0,
                    way = way,
                )
//            println("doubleLineAir: ${doubleLineAir.summa()}")
            val doubleLineVihu =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = -sp.sign,
                    way = way,
                )

//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            val valivaihe = 0
            +doubleLineOma.summa()
            +doubleLineAir.summa() / 2
            -doubleLineVihu.summa()
            counter = valivaihe
        }

        return counter
    }

    fun lastMovesValue2(neededForWin: Int = 4): Int {
        if (history.isEmpty()) return 0
        val lastOne = history.last()
//        if (lastOne == -1) return 0
        val wellSpace = getWellSpace(lastOne)
        val startingPoint = Point(lastOne, wellSpace)
        val listaa = SecondHeuristicThing.combinedWells(this, 1)
        return listaa[startingPoint.x]
    }

    fun lastMovesValue3(neededForWin: Int = 4): Int {
        if (history.isEmpty()) return 0
        val lastOne = history.last()
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
//            println("doubleLineOma: ${doubleLineOma.summa()}")
            val doubleLineAir =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = 0,
                    way = way,
                )
//            println("doubleLineAir: ${doubleLineAir.summa()}")
            val doubleLineVihu =
                doubleLineWithJumpStart(
                    current = startingPoint,
                    sign = -sp.sign,
                    way = way,
                )

//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
//            val valivaihe = 0
//            +doubleLineOma.summa()
//            +doubleLineAir.summa() / 2
//            -doubleLineVihu.summa()

            val summa = doubleLineOma.summa()
            if (summa >= 4) {
//                val valivaihe = doubleLineOma.summa() / 2
//                counter = valivaihe
                counter++
            }
        }

        return counter / 2
    }

    fun isAtMaxSize(): Boolean {
        return history.size == board.fold(0) { acc, ints -> acc + ints.size }
    }
}
