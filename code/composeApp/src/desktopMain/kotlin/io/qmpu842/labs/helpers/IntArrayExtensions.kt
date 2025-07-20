package io.qmpu842.labs.helpers

fun IntArray.getIndexOfMax(): Int {
    var indexe = 0
    var max = this[indexe]
    this.forEachIndexed { index, value ->
        if (value > max){
            indexe = index
            max = value
        }
    }
    return indexe
}