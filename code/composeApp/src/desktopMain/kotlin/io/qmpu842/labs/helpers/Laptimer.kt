package io.qmpu842.labs.helpers

var lastTime = System.currentTimeMillis()

fun lapTime() {
    val thing = System.currentTimeMillis()
    val timeTook = thing - lastTime
    lastTime = thing
    println("Round took ~$timeTook ms")
}