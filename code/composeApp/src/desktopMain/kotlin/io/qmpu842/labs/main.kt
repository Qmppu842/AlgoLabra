@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.qmpu842.labs.helpers.ProfileHolder
import io.qmpu842.labs.logic.GameHolder
import kotlinx.coroutines.delay

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Connect 4,  Bots",
            state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
            alwaysOnTop = true,
        ) {
//            Comment this launchedEffect away if you want to play more than 5 minutes
            LaunchedEffect(true) {
                delay(300_000)
                this@application.exitApplication()
            }
            App2()
        }
    }

fun main2() {
    GameHolder.runWithOutUi(
        10,
        playerA = ProfileHolder.minimaxDepth11TimeInf,
        playerB = ProfileHolder.minimaxDepth7TimeInf,
    )
}

fun main4() {
    val resultSet = mutableSetOf<String>()
//    var totalAmount = 0
    val allowedTier1 = listOf('+', 'o')
    allowedTier1.forEach { eka ->
        allowedTier1.forEach { toka ->
            allowedTier1.forEach { kolmas ->
                allowedTier1.forEach { nelos ->
                    val res = "$eka$toka$kolmas$nelos"
                    val count = countChar(res, 'o')
                    if (count <= 1) {
//                        totalAmount++
//                        println(res)
                        val mirror = mirror(res)
                        val contains = resultSet.contains(mirror)
                        if (!contains) {
                            resultSet.add(res)
                        }
                    }
                }
            }
        }
    }

    val allowed = listOf('+', '*', '¤', 'o')
//    val allowed = listOf('+', 'o')
    allowed.forEach { eka ->
        allowed.forEach { toka ->
            allowed.forEach { kolmas ->
                allowed.forEach { nelos ->
                    allowed.forEach { vitos ->
                        val res = "$eka$toka$kolmas$nelos$vitos"
                        val plusCount = countChar(res, '+')
                        if (plusCount in 2..4) {
//                            println(res)
//                            totalAmount++
//                            val mirror = mirror(res)
//                            val contains = resultSet.contains(mirror)
//                            if (!contains){
//                                resultSet.add(res)
//                            }
                            val isTrue = cutAndMirror(res, resultSet.toSet())
                            if (!isTrue) {
                                resultSet.add(res)
                            }
                        }
                    }
                }
            }
        }
    }
    println("Set: $resultSet")
    println("total amount: ${resultSet.size}")
}

fun main3() {
    var totalAmount = 0
    val allowedTier1 = listOf('+', 'o')
    allowedTier1.forEach { eka ->
        allowedTier1.forEach { toka ->
            allowedTier1.forEach { kolmas ->
                allowedTier1.forEach { nelos ->
                    val res = "$eka$toka$kolmas$nelos"
                    val count = countChar(res, 'o')
                    if (count <= 1) {
                        totalAmount++
                        println(res)
                    }
                }
            }
        }
    }

//    val allowed = listOf('+', '*', '¤', 'o')
    val allowed = listOf('+', 'o')
    allowed.forEach { eka ->
        allowed.forEach { toka ->
            allowed.forEach { kolmas ->
                allowed.forEach { nelos ->
                    allowed.forEach { vitos ->
                        val res = "$eka$toka$kolmas$nelos$vitos"
                        val plusCount = countChar(res, '+')
                        if (plusCount > 1) {
                            println(res)
                            totalAmount++
                        }
                    }
                }
            }
        }
    }
    println("total amount: $totalAmount")
}

fun main5() {
    val resultSet = mutableSetOf<String>()
//    resultSet.add("+*+")
//    resultSet.add("++*")
    resultSet.add("o+++o")
    resultSet.add("++o+")
    resultSet.add("++¤+")
    resultSet.add("++++")
//    var totalAmount = 0
//    val allowedTier1 = listOf('+', 'o')
//    val allowedTier1 = listOf('+', '*', '¤', 'o')
    val allowedTier1 = listOf('+', '¤', 'o')
    allowedTier1.forEach { eka ->
        allowedTier1.forEach { toka ->
            allowedTier1.forEach { kolmas ->
                allowedTier1.forEach { nelos ->
                    val res = "$eka$toka$kolmas$nelos"
//                    val count = countChar(res, 'o')
//                    if (count <= 1) {
//                        val mirror = mirror(res)
//                        val contains = resultSet.contains(mirror)
//                        if (!contains) {
//                            resultSet.add(res)
//                        }
//                    }

                    conditioning(res, resultSet)
                }
            }
        }
    }

//    val allowed = listOf('+', '*', '¤', 'o')
    val allowed = listOf('+', '¤', 'o')
//    val allowed = listOf('+', 'o')
    allowed.forEach { eka ->
        allowed.forEach { toka ->
            allowed.forEach { kolmas ->
                allowed.forEach { nelos ->
                    allowed.forEach { vitos ->
                        val res = "$eka$toka$kolmas$nelos$vitos"
                        conditioning(res, resultSet)
                    }
                }
            }
        }
    }
    println("Set: $resultSet")
    println("total amount: ${resultSet.size}")
//    val res2 = resultSet.map { value -> otherSide(value) }
//    println("Set2: $res2")

    resultSet.forEach { t ->
        println("\"$t\",")
    }
}

private fun conditioning(
    res: String,
    resultSet: MutableSet<String>,
) {
    val plusCount = countChar(res, '+')
    val starCount = countChar(res, '*')
    val oCount = countChar(res, 'o')
    val uCount = countChar(res, '¤')
    if (plusCount in 2..4 &&
        starCount < 2 &&
        (plusCount + oCount <= 4) &&
        (plusCount + starCount < 4) &&
        uCount < 3 &&
        (plusCount + oCount < 4)
    ) {
        val isTrue = cutAndMirror(res, resultSet.toSet())
        if (!isTrue) {
            resultSet.add(res)
        }
    }
}

private fun countChar(
    res: String,
    char: Char,
): Int {
    var count = 0
    res.forEach { c ->
        if (c == char) {
            count++
        }
    }
    return count
}

fun mirror(key: String): String = key.reversed()

fun otherSide(key: String): String {
    var result = ""
    key.forEach {
        result +=
            when (it) {
                '+' -> {
                    '-'
                }

                '-' -> {
                    '+'
                }

                '¤' -> {
                    '*'
                }

                '*' -> {
                    '¤'
                }

                else -> {
                    it
                }
            }
    }
    return result
}

fun cutAndMirror(
    res: String,
    toSet: Set<String>,
): Boolean {
    val thing =
        emptyList<String>() +
            res + mirror(res) +
            cutSides(res) +
            cutSides(mirror(res))
//            cutSidesExtreme(res) +
//            cutSidesExtreme(mirror(res)) +
    thing.forEach { t ->
        if (toSet.contains(t)) {
            return true
        }
    }
    return false
}

fun cutSides(key: String): List<String> = listOf(key.take(key.length - 1), key.takeLast(key.length - 1))

fun cutSidesExtreme(key: String): List<String> = listOf(key.take(key.length - 2), key.takeLast(key.length - 2))
