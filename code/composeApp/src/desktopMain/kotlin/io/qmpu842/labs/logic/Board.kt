package io.qmpu842.labs.logic

data class Board(
    val board: Array<IntArray>,
    val history: MutableList<Int> = mutableListOf(-1)
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
    ): Int {
        val thing = board[column]
        var lastZero = -1
        thing.forEachIndexed { index, t ->
            if (t == 0) {
                lastZero = index
            }
        }
        if (lastZero != -1) {
            board[column][lastZero] = token
            history.add(column)
        }
        return lastZero
    }

    fun undoLastMove(): Int {
        return -1
    }
}
