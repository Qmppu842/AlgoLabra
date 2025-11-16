package io.qmpu842.labs

fun counter1() {
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

fun counter2() {
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

fun counter3() {
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
//    val allowedTier1 = listOf('+', '¤', 'o')
    val allowedTier1 = listOf('+', '-', 'o')
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
//    val allowed = listOf('+', '¤', 'o')
//    val allowed = listOf('+', 'o')
    val allowed = listOf('+', '-', 'o')
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
//        (plusCount + oCount <= 4) &&
//        (plusCount + starCount < 4) &&
        uCount < 3 &&
//        (plusCount + oCount < 4) &&
        oCount > 1
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
//                cutSides(res) +
//                cutSides(mirror(res))
//            cutSidesExtreme(res) +
//            cutSidesExtreme(mirror(res)) +
            emptyList()
    thing.forEach { t ->
        if (toSet.contains(t)) {
            return true
        }
    }
    return false
}

fun cutSides(key: String): List<String> = listOf(key.take(key.length - 1), key.takeLast(key.length - 1))

fun cutSidesExtreme(key: String): List<String> = listOf(key.take(key.length - 2), key.takeLast(key.length - 2))

fun counterSensible() {
    val resultSet = mutableSetOf<String>()
    val allowed = listOf('+', '-', 'o')
//    val dicti = hashMapOf<String, Int>()

    val all =
        (1..7).fold(listOf("")) { acc, _ ->
            acc.flatMap { prefix -> allowed.map { prefix + it } }
        }

    all.forEach { text ->
        if (!resultSet.contains(text)) {
            if (!resultSet.contains(text.reversed())) {
                resultSet.add(text)
            }
        }
    }

    println("Set: $resultSet")
    println("total amount: ${resultSet.size}")
    resultSet.forEach { t ->
        println("\"$t\",")
    }
}
