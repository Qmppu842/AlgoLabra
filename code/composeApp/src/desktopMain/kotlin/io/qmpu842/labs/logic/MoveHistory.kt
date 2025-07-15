package io.qmpu842.labs.logic

data class MoveHistory(
    val list: List<Int>,
    val boardHeight: Int = 6,
    val boardWidth: Int = 7,
) {
    constructor(vararg things: Int) : this(things.asList())

    fun add(x: Int): MoveHistory = MoveHistory(list + x)

    fun size(): Int = list.size

    fun undoLast(): MoveHistory = MoveHistory(list.subList(0, list.size - 1))

    fun limitedAdd(
        x: Int,
        limit: Int = 6,
    ): MoveHistory = if (list.count { it == x } < limit) add(x) else this

    fun getBoardPaddedWithZeros(
        boardHeight: Int = this.boardHeight,
        boardWidth: Int = this.boardWidth,
    ): MutableList<MutableList<Int>> {
        val board4 =
            MutableList(boardWidth) {
                mutableListOf<Int>()
            }
        var counter = 0
        for (thing in list) {
            val thing1 = board4[thing - 1]
            val thingToPut = thing * if (counter % 2 == 0) 1 else -1
            thing1.add(thingToPut)

            counter++
        }

        for (aa in board4) {
            while (aa.size < boardHeight) {
                aa.add(0)
            }
            aa.reverse()
        }
        return board4
    }

    fun getBoard(): MutableList<MutableList<Int>> {
        val board4 =
            MutableList(boardWidth) {
                mutableListOf<Int>()
            }
//        var counter = 0
//        for (move in list) {
//            val thingToPut = move * if (counter % 2 == 0) 1 else -1
//            board4[move - 1].add(thingToPut)
//            counter++
//        }

        list.forEachIndexed { index, i ->
            board4[i - 1].add(i * if (index % 2 == 0) 1 else -1)
        }
        return board4
    }
}
