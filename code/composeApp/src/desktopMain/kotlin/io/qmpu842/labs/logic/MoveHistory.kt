package io.qmpu842.labs.logic

data class MoveHistory(
    val list: List<Int> = listOf(),
    val boardHeight: Int = 6,
    val boardWidth: Int = 7,
) {
    constructor(vararg things: Int) : this(things.asList())

    /**
     * Adds move to the history
     */
    fun add(x: Int): MoveHistory = MoveHistory(list + x)

    fun size(): Int = list.size

    /**
     * Remove the last move from the history
     */
    fun undoLast(): MoveHistory = MoveHistory(list.subList(0, list.size - 1))

    /**
     * Adds only if there is space for it
     */
    fun limitedAdd(
        x: Int,
        limit: Int = boardHeight,
    ): MoveHistory = if (list.count { it == x } < limit) add(x) else this

    /**
     * @return MutableList<MutableList<Int>> that represents the board state filled with zeros and flipped.
     * This allows us to think as if the moves have fallen to the bottom of the board.
     * In format of width or x as top level.
     * And as height or y as inner level.
     */
    fun getBoardPaddedWithZeros(): MutableList<MutableList<Int>> {
        val board4 = getBoard()

        for (column in board4) {
            while (column.size < boardHeight) {
                column.add(0)
            }
            column.reverse()
        }
        return board4
    }

    /**
     * @return MutableList<MutableList<Int>> that represents the board state.
     * In format of width or x as top level.
     * And as height or y as inner level.
     *
     * Positive numbers are for player one
     * Negatives for player two
     */
    fun getBoard(): MutableList<MutableList<Int>> {
        val board4 =
            MutableList(boardWidth) {
                mutableListOf<Int>()
            }
        list.forEachIndexed { index, i ->
            board4[i - 1].add(i * if (index % 2 == 0) 1 else -1)
        }
        return board4
    }

    /**
     * @return list of all possible drop places.
     * The columns that are not full
     */
    fun getLegalMoves(): List<Int> {
        val result = mutableListOf<Int>()
        val board = getBoard()
        board.forEachIndexed { index, column ->
            if (column.size < boardHeight) result.add(index + 1)
        }
        return result.toList()
    }

    fun didLastMoveWin(): Boolean {
        val result = false


        return result
    }
}
