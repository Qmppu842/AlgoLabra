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
}
