package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
import kotlin.math.abs
import kotlin.math.round
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

    companion object {
        /**
         * Allows us to use only board as constructor
         * And build the history from it.
         */
        operator fun invoke(board: Array<IntArray>): Board {
            val width = board.size
            val height = board.first().size

            return Board(
                board = board,
                boardConfig =
                    BoardConfig(
                        width = width,
                        height = height,
                        neededForWin = round((width * height) / 10f).toInt(),
                    ),
                history = makeHistoryFromBoard(board),
            )
        }

        private fun makeHistoryFromBoard(board: Array<IntArray>): MutableList<Int> {
            val wellMap = hashMapOf<Int, Int>()

            board.forEachIndexed { index, well ->
                well.forEach { target ->
                    if (target != 0) {
                        wellMap.put(abs(target), index)
                    }
                }
            }

            val thing = wellMap.entries.sortedWith(compareBy { entry -> entry.key })
            val history = mutableListOf<Int>()
            thing.forEach { (_, value) -> history.add(value) }
            return history
        }

        /**
         * This allows us to make the board from string and board config
         * @param boardConfig is normal board config.
         * @param historyAsText is history in this kind a format: "44444222245355266776662611135533" where odds are player A and player B is evens
         * @param offset is if the text is in other format like the above one needs -1 as their wells are 1 indexed instead of 0 indexed.
         */
        operator fun invoke(
            boardConfig: BoardConfig,
            historyAsText: String,
            offset: Int = 0,
        ): Board = invoke(boardConfig = boardConfig, history = historyAsText.map { h -> h.digitToInt() + offset })

        /**
         * This allows us to make the board from history and board config
         */
        operator fun invoke(
            boardConfig: BoardConfig,
            history: List<Int>,
        ): Board {
            var board = Board(boardConfig)
            history.forEach { i ->
                board = board.dropLockedToken(i)
            }
            return board
        }
    }

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
        for (nouseva in 0..<moves.size) {
            val laskeva = moves.size - nouseva - 1
            if (nouseva >= laskeva) {
                if (moves.size % 2 == 1) {
                    result2.add(moves[nouseva])
                }
                break
            }
            result2.add(moves[nouseva])
            result2.add(moves[laskeva])
        }
        result2.reverse()
        return result2
    }

    /**
     * Drops token that is determined by the player on turn and length of history
     */
    fun dropLockedToken(column: Int): Board = dropToken(column, getOnTurnToken())

    /**
     * @return next turn token so on turn 32 token is -32
     * First token is 1
     */
    fun getOnTurnToken(): Int = (history.size + 1) * if (history.size % 2 == 0) 1 else -1

    /**
     * Drops the token to the well
     * @param column the well to drop in
     * @param token the token to drop
     *
     * @return the modified board
     */
    fun dropToken(
        column: Int,
        token: Int,
    ): Board {
        val theWell = board[column]
        var lastZero = -1
        theWell.forEachIndexed { index, theY ->
            if (theY == 0) {
                lastZero = index
            }
        }

        if (lastZero == -1) return this

        board[column][lastZero] = token
        return this.copy(
            board,
            boardConfig,
            history + column,
        )
    }


    /**
     * Drops token that is determined by the player on turn and length of history
     */
    fun dropLockedTokenWithOutHistory(column: Int): Pair<Board, Int> = dropTokenWithOutHistory(column, getOnTurnToken())

    /**
     * Drops the token to the well
     * @param column the well to drop in
     * @param token the token to drop
     *
     * @return the modified board
     */
    fun dropTokenWithOutHistory(
        column: Int,
        token: Int,
    ): Pair<Board, Int> {
        var lastZero = -1
        board[column].forEachIndexed { index, theY ->
            if (theY == 0) {
                lastZero = index
            }
        }

        if (lastZero == -1) return Pair(this, -1)

        board[column][lastZero] = token
        return Pair(this.copy(board), lastZero)
    }

    /**
     * @return the board but last move removed.
     */
    fun undoLastMove(): Board {
        if (history.isEmpty()) return this
        val lastWell = history.last()

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

    /**
     * @return returns the index of zero closest to the fill line.
     * This is equal to first available position if you were to count from top 0-indexed on real board.
     *
     * Meaning putting something to this index will overwrite only 0.
     *
     * Example:
     * Board with height of 6 will give 5 on first turn.
     */
    fun getHighestSpaceIndex(column: Int) = getWellSpace(column) - 1

    /**
     * Checks in any direction if last move is winning move
     *
     * (Gets last move from history)
     */
    fun isLastPlayWinning(neededForWin: Int = 4): Boolean {
        if (history.isEmpty()) return false
//        val x = history.last()
        val x = history[history.size-1]
        return doesPlaceHaveWinning(
            x = x,
            y = getWellSpace(x),
            neededForWin = neededForWin,
        )
    }

    /**
     * Checks if (x|y) is winning in any direction
     *
     * Caution: Uses the sing at the place
     * So at empty board basically any place is "winning" as this counts the zero lines as wins
     */
    fun doesPlaceHaveWinning(
        x: Int,
        y: Int,
        neededForWin: Int,
    ): Boolean {
        val sign = board[x][y].sign
        if (sign == 0) return false

//        for (way in Way.entries) {
        for (way in Way.half) {
            val result: Int =
                checkLine(
                    x = x,
                    y = y,
                    sign = sign,
                    way = way,
                )
            val opposite = way.getOpposite()
            val result2: Int =
                checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = sign,
                    way = opposite,
                )
            if ((result + result2) >= neededForWin) return true
        }
        return false
    }

    /**
     * Checks the line and the "anti-line" without allowing starting zero.
     * Thus, this is good for situation like 1121  where the 2 is the latest
     */
    fun doubleLineNoJumpStart(
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Pair<Int, Int> {
        val result: Int =
            checkLine(
                x = x,
                y = y,
                sign = sign,
                way = way,
                length = length,
            )
        val opposite = way.getOpposite()
        val result2: Int =
            checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = sign,
                way = opposite,
                length = length,
            )
        return Pair(result, result2) // TODO: look at these pairs, i dont think they are needed
    }

    /**
     * Checks the line and the "anti-line" with allowing starting zero.
     * Thus, this is good for situation like 1101  where the 0 is the space we want to check
     */
    fun doubleLineWithJumpStart(
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Pair<Int, Int> {
        val result: Int =
            checkLine(
                x = x + way.x,
                y = y + way.y,
                sign = sign,
                way = way,
                length = length,
            )
        val opposite = way.getOpposite()
        val result2: Int =
            checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = sign,
                way = opposite,
                length = length,
            )
        return Pair(result, result2)
    }

    /**
     * Counts recursively how many of each thing in the line
     * @param x todo
     * @param y todo
     * @param sign what things to count -1/+1/0
     * @param way what way the line should go
     * @param length how many counted so far.
     *
     * @return the amount of sign countered before other sign broke the chain
     */
    fun checkLine(
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
        length: Int = 0,
    ): Int {
        var realLength = length
        if ((x !in 0..<board.size)) return realLength
        if (y !in 0..<board[x].size) return realLength
        val currentValue = board[x][y]

        if (currentValue.sign != sign) return realLength

        realLength += 1

        val nextX = x + way.x
        val nextY = y + way.y
        if ((nextX !in 0..<board.size)) return realLength
        if (nextY !in 0..<board[x].size) return realLength

        return checkLine(
            x = nextX,
            y = nextY,
            sign = sign,
            way = way,
            length = realLength,
        )
    }

    /**
     * @return mutable list of valid starting points.
     *  So the wells with space and the first free index of that well
     */
    fun startingPoints(): MutableList<Int> {
        val startingPoints2 = mutableListOf<Int>()

        for (move in getLegalMoves()) {
            startingPoints2.add(move)
            val startIndex = getHighestSpaceIndex(move)
            startingPoints2.add(startIndex)
        }
        return startingPoints2
    }

    /**
     * Deep copies the board so the arrays are not linked anymore -.-
     * @return copy of the board without any reference linking
     */
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

    /**
     * @return true if board is full (based on history)
     */
    fun isAtMaxSize(): Boolean {
        var allSize = 0
        board.forEachIndexed { index, ints ->
            allSize += ints.size
        }
        return history.size == allSize
    }
}
