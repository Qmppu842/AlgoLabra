package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sign

/**
 * @param board Array<IntArray> contains positives, negatives and zero numbers.
 * @param boardConfig look boardConfig for better info
 * @param history list containing all columns in order they had something put in them.
 */
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
                        wellMap[abs(target)] = index
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
     * @return the last column that had token put in to.
     */
    fun getLastMove(): Int? = history.lastOrNull()

    /**
     * @return returns int pair, first one is the row and second is the column
     * It will return -1, -1 if no moves has been made on the board.
     */
    fun getLastTokenLocation(): Pair<Int, Int> {
        val token = getLastMove() ?: return Pair(-1, -1)
        return Pair(getWellSpace(token), token)
    }

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
     * @yield is the free index from middle to out
     * Once shown all possible will give -1 once.
     * if no possible moves left, will give only -1
     */
    fun getLegalsMiddleOutSeq() =
        sequence {
            val sizee = boardConfig.width
            val middle = sizee / 2 + (sizee % 2)
            var negWalk = 1
            var posWalk = 0

            while (true) {
                val negneg = middle - negWalk
                val pospos = middle + posWalk
                if (negneg < 0 && pospos > sizee) {
                    yield(-1)
                    negWalk = 0
                    posWalk = -1
                }

                if (negneg >= 0 && board[negneg][0] == 0) {
                    yield(negneg)
                }

                if (pospos < sizee && board[pospos][0] == 0) {
                    yield(pospos)
                }
                negWalk += 1
                posWalk += 1
            }
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
            board = board,
            boardConfig = boardConfig,
            history = history + column,
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
     * @return Pair where first is the board, and second is the y value where the token ended. -1 if did not have space.
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
        return Pair(this.copy(board = board), lastZero)
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
            board = board,
            boardConfig = boardConfig,
            history = history.take(history.size - 1),
        )
    }

    /**
     * @return width of the board
     */
    fun getWells() = boardConfig.width

    fun clear(): Board = Board(boardConfig)

    /**
     *  @return the zeroes still in the well \n
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
        val x = history[history.size - 1]
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
        if (x == -1) return false
        val eka = board[x]
        val sign = eka[y].sign
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
//            val opposite = way.getOpposite()
            val opposite = Way.opp[way.ordinal]
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
    ): Pair<Int, Int> {
        val result: Int =
            checkLine(
                x = x,
                y = y,
                sign = sign,
                way = way,
            )
//        val opposite = way.getOpposite()
        val opposite = Way.opp[way.ordinal]
        val result2: Int =
            checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = sign,
                way = opposite,
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
    ): Pair<Int, Int> {
        val result: Int =
            checkLine(
                x = x + way.x,
                y = y + way.y,
                sign = sign,
                way = way,
            )
        val opposite = Way.opp[way.ordinal]
        val result2: Int =
            checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = sign,
                way = opposite,
            )
        return Pair(result, result2)
    }

    /**
     * Counts how many of each thing in the line
     * @param x todo
     * @param y todo
     * @param sign what things to count -1/+1/0
     * @param way what way the line should go
     *
     * @return the amount of sign countered before other sign broke the chain
     */
    fun checkLine(
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
    ): Int {
        var arvo = 0
        var x = x
        var y = y
        val xMax = board.size
        while (x in 0 until xMax) {
            val row = board[x]
            if (y !in 0..<row.size || row[y].sign != sign) break
            x += way.x
            y += way.y
            arvo += 1
        }
        return arvo
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
        board.forEachIndexed { _, ints ->
            allSize += ints.size
        }
        return history.size == allSize
    }

    fun maxSize(): Int {
        var allSize = 0
        board.forEachIndexed { _, ints ->
            allSize += ints.size
        }
        return allSize
    }

    fun getFullBoardValues(): Pair<Int, Int> {
        var player1 = 0
        var player2 = 0

        val (vert1, vert2) = getVerticalLineValues()
        player1 += vert1
        player2 += vert2

        val (hori1, hori2) = getHorizontalLineValues()
        player1 += hori1
        player2 += hori2

        val (diagUp1, diagUp2) = getDiagonalUpLineValues()
        player1 += diagUp1
        player2 += diagUp2

        val (diagDown1, diagDown2) = getDiagonalDownLineValues()
        player1 += diagDown1
        player2 += diagDown2

        return Pair(player1, player2)
    }

    data class Point(
        val x: Int,
        val y: Int,
    )

    val katto = (0..<boardConfig.width).map { x -> Point(x, 0) }
    val pohja = (0..<boardConfig.width).map { x -> Point(x, boardConfig.height - 1) }
    val rightSeina = (0..<boardConfig.height).map { y -> Point(0, y) }
    val leftSeina = (0..<boardConfig.height).map { y -> Point(boardConfig.width - 1, y) }

    val rightSeinaJaPohja = (pohja + rightSeina).toSet().toList()
    val leftSeinaJaKatto = (katto + leftSeina).toSet().toList()

    /**
     * What a monster of function
     * Maybe I will get around making it neat and tidy.
     * (Narrator: No, he won't, and he already knows it, but yet he keeps the hope alive.)
     */
    fun getVerticalLineValues1(): Pair<Int, Int> {
        var justTotalValue = 0
        var totalPlayer1 = 0
        var totalPlayer2 = 0
        val winn = boardConfig.neededForWin

//        val verticalLines = board
        for (xIndex in 0..<board.size) {
            val line = board[xIndex]
            if (line.size < winn) continue

            var justValue = 0
//            var currentEka = 0
//            var currentToka = 0
            for (yIndex in 0..<line.size) {
                val value = line[yIndex]
                val sing = value.sign
                if (sing == 1) {
//                    currentEka += 1
                    justValue += 1
                }
                if (sing == -1) {
                    justValue += 10
                }
//                println("the sign:  $sing")
                if (yIndex > winn) {
//                    println("karsinta")
                    val value2 = line[yIndex - winn - 1].sign
                    if (value2 == 1) {
//                    currentEka += 1
                        justValue -= 1
                    }
                    if (value2 == -1) {
                        justValue -= 10
                    }
//                    println("just value: $justValue")
                    if (justValue > 0) {
                        if (justValue % 10.0 == 0.0) {
//                            currentToka += 1
                            val amount = justValue / 10
                            totalPlayer2 += amount * amount
//                            println("toka player total: $totalPlayer2")
                        } else if (justValue < 10) {
//                            currentEka += 1
                            totalPlayer1 += justValue * justValue
//                            println("eka player total: $totalPlayer1")
                        }
                    }
                }
            }
        }

        return Pair(totalPlayer1, totalPlayer2)
    }

    fun getVerticalLineValues(): Pair<Int, Int> =
        getGeneralLineValues(
            startingPoints = katto,
            way = Way.Down,
        )

    fun getHorizontalLineValues(): Pair<Int, Int> {
        var justTotalValue = 0
        var totalPlayer1 = 0
        var totalPlayer2 = 0
        val winn = boardConfig.neededForWin

        val alkuPisteet = listOf(1)

        return Pair(totalPlayer1, totalPlayer2)
    }

    fun getDiagonalUpLineValues(): Pair<Int, Int> {
        TODO()
        return Pair(0, 0)
    }

    fun getDiagonalDownLineValues(): Pair<Int, Int> {
        TODO()
        return Pair(0, 0)
    }

    fun getGeneralLineValues(
        startingPoints: List<Point>,
        way: Way,
    ): Pair<Int, Int> {
        var totalPlayer1 = 0
        var totalPlayer2 = 0
        val winn = boardConfig.neededForWin

        for (point in startingPoints) {
            var nextX = point.x // + way.x
            var nextY = point.y // + way.y
            var counter = 0
            var justValue = 0
//            while (!((nextX >= boardConfig.width || nextX < 0) && (nextY >= boardConfig.height || nextY < 0))) {
            println("new point: $point")
            while (true) {
                if (!(nextX < boardConfig.width && nextX >= 0)) break
                if (!(nextY < boardConfig.height && nextY >= 0)) break

                val current = board[nextX][nextY].sign
//                val current = board[nextY][nextX].sign
                if (current == 1) {
                    justValue += 1
                } else if (current == -1) {
                    justValue += 10
                }
                println("justValue: $justValue")
                println("counter: $counter")
//                println("(x|y): ($nextX|$nextY) = ${board[nextX][nextY]}")
//                println("(y|x): ($nextY|$nextX) = ${board[nextY][nextX]}")
                if (counter >= winn) {
                    println("nyt sisällä")
                    val value2 = board[nextX - way.x * winn][nextY - way.y * winn].sign
//                    val value2 = board[nextY - way.y * winn][nextX - way.x * winn].sign
                    if (value2 == 1) {
                        justValue -= 1
                    }
                    if (value2 == -1) {
                        justValue -= 10
                    }
                }
                if (counter >= winn-1) {
                    if (justValue > 0) {
                        println("mahollista kamaa: $justValue")
                        if (justValue % 10.0 == 0.0) {
                            val amount = justValue / 10
                            totalPlayer2 += amount * amount
                            println("totalPlayer2: $totalPlayer2")
                        } else if (justValue < 10) {
                            totalPlayer1 += justValue * justValue
                            println("totalplayer1: $totalPlayer1")
                        }
                    }
                }

                nextX += way.x
                nextY += way.y
//                println("(x|y)2: ($nextX|$nextY)")
                counter += 1
//                if (nextX >= boardConfig.width || nextX < 0) break
//                if (nextY >= boardConfig.height || nextY < 0) break
//
//                if (!(nextX < boardConfig.width && nextX >= 0) )break
//                if (!(nextY < boardConfig.height && nextY >= 0)) break
            }
        }
        println("loppu")
        return Pair(totalPlayer1, totalPlayer2)
    }
}
