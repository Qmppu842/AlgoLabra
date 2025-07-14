package io.qmpu842.labs.logic

data class MoveHistory(
    val list: List<Int>,
) {
    constructor(vararg things: Int) : this(things.asList())

    fun add(x: Int): MoveHistory = MoveHistory(list + x)

    fun size(): Int = list.size

    fun undoLast(): MoveHistory = MoveHistory(list.subList(0, list.size - 1))

    fun limitedAdd(
        x: Int,
        limit: Int = 6,
    ): MoveHistory = if (list.count { it == x } < limit) add(x) else this
}
