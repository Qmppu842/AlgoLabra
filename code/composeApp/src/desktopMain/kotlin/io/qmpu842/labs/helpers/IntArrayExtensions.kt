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

fun IntArray.getListOfIndexesOfMax(): MutableList<Int> {
    val indexList = mutableListOf<Int>()
    var max = Int.MIN_VALUE
    this.forEachIndexed { index, value ->
        if (value > max) {
            indexList.clear()
            max = value
        }
        if (value == max) {
            indexList.add(index)
        }
    }
    return indexList
}
