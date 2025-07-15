package io.qmpu842.labs.helpers

import kotlin.math.abs
import kotlin.math.max

fun MutableList<MutableList<Int>>.getHighestAbs(): Int {
    var highest = 0
    this.forEach {
        it.forEach { num ->
            highest = max(abs(num), highest)
        }
    }
    return highest
}
