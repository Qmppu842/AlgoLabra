package io.qmpu842.labs.logic

data class Board(
    val board: Array<IntArray>,
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
    fun getLastMove(): Int = 1

    /**
     * @return list of all the wells with space
     */
    fun getLegalMoves(): MutableList<Int> {
        val result = mutableListOf<Int>()
//        board.forEachIndexed { index, ints ->
//        }
        return result
    }

    /**
     * Drops the token to the well
     * @param column the well to drop in
     *
     * @return -1 if no space otherwise the height the token was put in
     */
    fun dropToken(column: Int): Int {
        return -1
    }
}
