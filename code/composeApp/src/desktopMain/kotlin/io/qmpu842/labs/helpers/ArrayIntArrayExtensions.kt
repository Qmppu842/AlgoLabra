package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.Way
import java.awt.Point

fun Array<IntArray>.get(current: Point): Int? {
    val x = current.x
    if (x !in 0..<this.size) return null

    val y = current.y
    if (y !in 0..<this[x].size) return null

    return this[current.x][current.y]
}

fun Array<IntArray>.next(
    current: Point,
    way: Way,
): Point? {
    val x = current.x + way.x
    if (x !in 0..<this.size) return null

    val y = current.y + way.y
    if (y !in 0..<this[x].size) return null
    return Point(x, y)
}


fun Array<IntArray>.collectedSize(): Int {
    var collectSize = 0
    this.forEach {
        collectSize += it.size
    }
    return collectSize
}

fun Array<IntArray>.collectedSize2(): Int = this.fold(0) { acc, next -> acc + next.size }
