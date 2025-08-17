package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.BLOCK_WIN
import io.qmpu842.labs.helpers.HEURESTIC_WIN
import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import kotlin.math.*

class MiniMaxV1Profile(
    var depth: Int = 10,
    override var timeLimit: Long = 100L,
) : OpponentProfile() {
    constructor(depth: Int, timeLimit: Int) : this(depth = depth, timeLimit = timeLimit.toLong())

    var currentMaxTime = Long.MAX_VALUE

    /**
     * This is dumb
     * it only purpose is that min and max imports stay even if I comment a/b part in minimax.
     */
    fun dumm() {
        val asd = min(1, 3)
        val qwe = max(1, 3)
    }

    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        currentMaxTime = System.currentTimeMillis() + timeLimit
        val startingTime = System.currentTimeMillis()
        val thinn = board.getLastMove() ?: -1
        val minimaxResult =
            minimax2(
                board = board,
                depth = depth,
                maximizingPlayer = true,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                forLastSide = -forSide,
                neededForWin = board.boardConfig.neededForWin,
                lastX = thinn,
                lastY = if (thinn != -1) board.getWellSpace(thinn) else -1,
//                lastX = -1,
//                lastY = -1,
                token = abs(board.getOnTurnToken()),
            )
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startingTime
        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
        println("The Minimax valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
        return minimaxResult.second
    }

    /**
     * @param forLastSide you should put here the value of last turns side.
     *  Why this way?
     *  Because the first round of minimax does nothing, only after it can do the first moves
     */
    fun minimax2(
        board: Board,
        depth: Int = this.depth,
        maximizingPlayer: Boolean = true,
        alpha: Int = Int.MIN_VALUE,
        beta: Int = Int.MAX_VALUE,
        forLastSide: Int,
        neededForWin: Int = 4,
        lastX: Int = 0,
        lastY: Int = 0,
        token: Int = 1,
    ): Pair<Int, Int> {
//        println("nyt syvyydessä: $depth")
        val terminal =
            board.doesPlaceHaveWinning(
                x = lastX,
                y = lastY,
                neededForWin = neededForWin,
            )
        val hasStopped = isBoardFull(board.board)

        if (terminal) {
            return if (!maximizingPlayer) {
                Pair(MINIMAX_WIN + depth, lastX)
            } else {
                Pair(MINIMAX_LOSE - depth, lastX)
            }
        } else if (hasStopped) {
            // In case of Draw
            return Pair(0, lastX)
        }

        val time = System.currentTimeMillis()
//        val y = lastY // board.getWellSpace(lastX)

        if (depth == 0 || time >= currentMaxTime) {
            return Pair(
                lastMovesValue5(
                    board = board,
                    x = lastX,
                    y = lastY,
                    forSide = forLastSide * if (maximizingPlayer) -1 else 1,
                    neededForWin = neededForWin,
                ),
                lastX,
            )

//            return Pair(
//                simpleEval(board, forLastSide * if (maximizingPlayer) -1 else 1),
//                lastX,
//            )
        }
//        val moves = board.getLegalMovesFromMiddleOut()
        val moves = board.getLegalsMiddleOutSeq()
        var alpha2 = alpha
        var beta2 = beta

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
//                val things = board.dropLockedTokenWithOutHistory(move)
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = false,
//                        alpha = alpha,
//                        beta = beta,
                        alpha = alpha2,
                        beta = beta2,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                        token = token + 1,
                    )
                board.board[move][things.second] = 0
                if (minied.first > value) {
                    bestMove = move
                    value = minied.first
                }

//                val alpha2 = max(alpha, value)
//                if (beta <= alpha2) break
                alpha2 = max(alpha2, value)
                if (beta2 <= alpha2) break
            }
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
//                val things = board.dropLockedTokenWithOutHistory(move)
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = true,
//                        alpha = alpha,
//                        beta = beta,
                        alpha = alpha2,
                        beta = beta2,
                        forLastSide = -forLastSide,
                        neededForWin = neededForWin,
                        lastX = move,
                        lastY = things.second,
                        token = token + 1,
                    )
                board.board[move][things.second] = 0
                if (minied.first < value) {
                    bestMove = move
                    value = minied.first
                }

//                val beta2 = min(beta, value)
//                if (beta2 <= alpha) break
                beta2 = min(beta2, value)
                if (beta2 <= alpha2) break
            }
            return Pair(value, bestMove)
        }
    }

    fun isBoardFull(board: Array<IntArray>): Boolean {
        val size = board.size
        for (aaa in 0..<size) {
            if (board[aaa][0] == 0) return false
        }
        return true
    }

    fun lastMovesValue51(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        var counter = 0

//        for (way in Way.entries) {
        for (way in Way.half) {
            var vali = 0
            val result: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = forSide,
                    way = way,
                )
//            val opposite = way.getOpposite()
            val opposite = Way.opp[way.ordinal]
            val result2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
            val doubleLineOma = result + result2

//            println("doubleLineOma: ${doubleLineOma.summa()}")
//            val doubleLineAir =
//                board.doubleLineWithJumpStart(
//                    current = startingPoint,
//                    sign = 0,
//                    way = way,
//                )
// //            println("doubleLineAir: ${doubleLineAir.summa()}")
//            val doubleLineVihu =
//                board.doubleLineNoJumpStart(
//                    current = startingPoint,
//                    sign = -forSide,
//                    way = way,
//                )

            val resultV: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = -forSide,
                    way = way,
                )
            val resultV2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = -forSide,
                    way = opposite,
                )
            val doubleLineVihu2 = resultV + resultV2
//            println("doubleLineVihu: ${doubleLineVihu.summa()}")
            if (doubleLineOma >= neededForWin) {
                vali = HEURESTIC_WIN
//                counter = Int.MAX_VALUE
//            } else if (doubleLineVihu.summa() >= neededForWin) {
//                    counter = HEURESTIC_LOSE
//                counter = Int.MIN_VALUE
            } else if (doubleLineVihu2 >= neededForWin - 1) {
                vali = BLOCK_WIN
            }

            if (abs(vali) > counter) {
                counter = vali
            }
        }
        return counter
    }

    fun lastMovesValue52(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        var counter = 0
        val mappe = hashMapOf<Int, Int>()
        println("x : $x")
        println("y : $y")
        var combo = 0
        for (way in Way.half) {
            val aaaaa =
                checkDoubleLine(
                    board = board,
                    x = x,
                    y = y,
                    forSide = forSide,
                    way = way,
                )
            println("the way: $way")
            println("Aaaa ${aaaaa.contentDeepToString()}")
//            mappe[aaaaa[0]] = mappe.getOrDefault(aaaaa[0], 0) + 1

            val oma = aaaaa[0] * aaaaa[0] * aaaaa[0] * 1000
            mappe[oma] = mappe.getOrDefault(oma, 0) + 1

            val air = aaaaa[1] * aaaaa[1] * 100
            mappe[air] = mappe.getOrDefault(air, 0) + 1

            val vihu = aaaaa[2] * -1
            mappe[vihu] = mappe.getOrDefault(vihu, 0) + 1
        }

        println("mappe: $mappe")

        return counter
    }

    fun checkDoubleLine(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        way: Way,
    ): Array<Int> {
        /**
         * Oma, ilma, vihu, oma + ilma, vihu + ilma
         */
        val resres = arrayOf(0, 0, 0, 0, 0)
        val opposite = Way.opp[way.ordinal]

//        omat linjat
        val resOma1: Int =
            board.checkLine(
                x = x,
                y = y,
                sign = forSide,
                way = way,
            )
        val resOma2: Int =
            board.checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = forSide,
                way = opposite,
            )
//        vihu linjat
        val resVih1: Int =
            board.checkLine(
                x = x,
                y = y,
                sign = -forSide,
                way = way,
            )
        val resVih2: Int =
            board.checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = -forSide,
                way = opposite,
            )
//        ilma linjat
        val resAir1: Int =
            board.checkLine(
                x = x,
                y = y,
                sign = 0,
                way = way,
            )
        val resAir2: Int =
            board.checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = 0,
                way = opposite,
            )

//        oma trap
        val resOmaTrap1: Int =
            board.checkLine(
                x = x + (way.x * (resOma1 + 0)),
                y = y + (way.y * (resOma1 + 1)),
                sign = 0,
                way = way,
            )
        val resOmaTrap2: Int =
            board.checkLine(
                x = x + (opposite.x * (resOma2 + 1)),
                y = y + (opposite.y * (resOma2 + 1)),
                sign = 0,
                way = opposite,
            )
//        val resOmaTrap1 = if (board.board[x + (way.x * (resOma1 + 0))][y + (way.y * (resOma1 + 1))] == 0) 1 else 0
//        val resOmaTrap2 =
//            if (board.board[x + (opposite.x * (resOma2 + 1))][y + (opposite.y * (resOma2 + 1))] == 0) 1 else 0

//        vihu trap
        val resVihuTrap1: Int =
            board.checkLine(
                x = x + (way.x * (resVih1 + 1)),
                y = y + (way.y * (resVih1 + 1)),
                sign = 0,
                way = way,
            )
        val resVihuTrap2: Int =
            board.checkLine(
                x = x + (opposite.x * (resVih2 + 1)),
                y = y + (opposite.y * (resVih2 + 1)),
                sign = 0,
                way = opposite,
            )
//        val resVihuTrap1 = if (board.board[x + (way.x * (resVih1 + 1))][y + (way.y * (resVih1 + 1))] == 0) 1 else 0
//        val resVihuTrap2 = if (board.board[x + (opposite.x * (resVih2 + 1))][y + (opposite.y * (resVih2 + 1))] == 0) 1 else 0

        resres[0] = resOma1 + resOma2
        resres[1] = resAir1 + resAir2
        resres[2] = resVih1 + resVih2
        resres[3] = resOmaTrap1 + resOmaTrap2
        resres[4] = resVihuTrap1 + resVihuTrap2
        return resres
    }

    /**
     * current one
     */
    fun lastMovesValue53(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        val w = board.boardConfig.width
        val h = board.boardConfig.height
        val size = sqrt(0.0 + w * w + h * h).toInt() + 2
        val omatLinjat = IntArray(size)
        val vihuLinjat = IntArray(size)

        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
//            omat linjat
            val resOma1: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = forSide,
                    way = way,
                )
            val resOma2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
            val omaSum = resOma1 + resOma2
            omatLinjat[omaSum] += 1

            //        vihu linjat
            val resVih1: Int =
                board.checkLine(
                    x = x,
                    y = y,
                    sign = -forSide,
                    way = way,
                )
            val resVih2: Int =
                board.checkLine(
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = -forSide,
                    way = opposite,
                )
            val vihuSum = resVih1 + resVih2
            vihuLinjat[vihuSum] += 1
        }
//        println("omatLinjat ${omatLinjat.toList()}")
//        println("vihuLinjat ${vihuLinjat.toList()}")
//        println("pullaa")
        var omaCounter = 0
        var vihuCounter = 0
        for (i in size - 1 downTo 0) {
            val oma = omatLinjat[i]
            omaCounter += oma * (10f.pow(i)).toInt()
            val vihu = vihuLinjat[i]
            vihuCounter += -(vihu * (10f.pow(i)).toInt())
        }
        return if (abs(omaCounter) >= abs(vihuCounter)) {
            omaCounter
        } else {
            vihuCounter
        }
    }

    fun lastMovesValue54(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        val w = board.boardConfig.width
        val h = board.boardConfig.height
        val size = sqrt(0.0 + w * w + h * h).toInt() + 2
        val omatLinjat = IntArray(size)
        val vihuLinjat = IntArray(size)

        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
//            omat linjat
//            val aaa = IntArray(size)
            val resOma =
                checkLine(
                    board2 = board,
                    x = x,
                    y = y,
                    sign = forSide,
                    way = way,
//                    aaa,
//                    size
                )
//            println("resOma: $resOma")

            val resOmaback =
                checkLine(
                    board2 = board,
                    x = x,
                    y = y,
                    sign = forSide,
                    way = opposite,
//                    aaa = aaa,
//                    size
                )
//            println("resOmaback: $resOmaback")
            val combo =
                resOmaback.reversed() +
                    resOma.filterIndexed { aa, _ ->
                        aa != 0
                    }
//            if (combo.size < neededForWin) continue
            println("combo: $combo")
//            val joined = aaa.joinToString(", ")
//            println("joined: $joined")
        }
        return 0
    }

    fun lastMovesValue5(
        board: Board,
        x: Int,
        y: Int,
        forSide: Int,
        neededForWin: Int = 4,
    ): Int {
        var holder = 0
        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
//            println("way: $way")
            val resOma =
                checkLine(
                    board2 = board,
                    x = x + way.x,
                    y = y + way.y,
                    sign = forSide,
                    way = way,
                )

            val resOmaback =
                checkLine(
                    board2 = board,
                    x = x + opposite.x,
                    y = y + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
//            val combo = resOmaback.reversed() + board.board[x][y] + resOma
            val combo = resOmaback.reversed() + "+" + resOma
//            if (combo.size < neededForWin) continue
            if (combo.length < neededForWin) continue
//            println("combo: $combo")
            val ggfgf = SoBaaaad.getter(combo)
            if (abs(ggfgf) > holder){
                holder = ggfgf
            }
        }
        return holder
    }

    /**
     * Counts how many of each thing in the line
     * @param x todo
     * @param y todo
     * @param sign what things to count -1/+1/0
     * @param way what way the line should go
     *
     * @return the amount of sign countered before other sign broke the chain
     */
    fun checkLine(
        board2: Board,
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
    ): String {
        val board = board2.board
        var x1 = x
        var y1 = y
        val xMax = board.size
        var thinnge = ""
        while (x1 in 0 until xMax) {
            val row = board[x1]
            if (y1 !in 0..<row.size) break
            val mm = row[y1].sign * sign
//            println("row[y1]: ${row[y1]}")
//            println("mm: $mm")
            if (mm == 1) {
                thinnge += "+"
            } else if (mm == -1) {
                thinnge += "-"
            } else {
                if (way != Way.Down && way != Way.Up) {
                    var y2 = y1
                    var merkki = row[y2]
                    var counter = -1
                    while (merkki == 0 && y2 < row.size) {
                        merkki = row[y2]
                        counter += 1
                        y2 += 1
                    }
                    if (counter > 1) {
                        thinnge += if (counter % 2 == 1) "¤" else "*"
                    } else {
                        thinnge += "o"
                    }
                } else {
                    thinnge += "o"
                }
            }
            x1 += way.x
            y1 += way.y
        }
        return thinnge
    }

    fun checkLine3(
        board2: Board,
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
    ): String {
        val board = board2.board
        var x1 = x
        var y1 = y
        val xMax = board.size
        var thinnge = ""
        val nnn = mutableListOf<Int>()
        val laste = -99
        while (x1 in 0 until xMax) {
            val row = board[x1]
            if (y1 !in 0..<row.size) break
            val mm = row[y1].sign * sign
            if (mm == 1) {
//                thinnge += "o1"
                thinnge += "+"
            } else if (mm == -1) {
//                thinnge += "v1"
                thinnge += "-"
            } else {
                if (way != Way.Down && way != Way.Up) {
                    var y2 = y1
                    var merkki = row[y2]
                    var counter = 0
                    while (merkki == 0 && y2 < row.size) {
                        merkki = row[y2]
                        counter += 1
                        y2 += 1
//                        println("merkki $merkki")
                    }
//                    thinnge += "d$counter"
//                    thinnge += "$counter"
//                    thinnge += "${counter % 2 }"
                    thinnge += if (counter % 2 == 1) "¤" else "*"
//                    thinnge += "x"
                } else {
//                    thinnge += "t1"
                    thinnge += "o"
                }
            }
            nnn.add(row[y1])
//            thinnge += "${row[y1]}, "
            x1 += way.x
            y1 += way.y
        }
//        println("Thinge: $thinnge")
        return thinnge
    }

    fun checkLine2(
        board2: Board,
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
    ): MutableList<Int> {
        val board = board2.board
        var x1 = x
        var y1 = y
        val xMax = board.size
        var thinnge = ""
        val nnn = mutableListOf<Int>()
        val laste = -99
        while (x1 in 0 until xMax) {
            val row = board[x1]
            if (y1 !in 0..<row.size) break
            val mm = row[y1].sign * sign
            if (mm == 1) {
//                thinnge += "o1"
                thinnge += "+"
            } else if (mm == -1) {
//                thinnge += "v1"
                thinnge += "-"
            } else {
                if (way != Way.Down && way != Way.Up) {
//                    thinnge += "t1"
                    thinnge += "o"
                } else {
                    var y2 = y1
                    var merkki = row[y2]
                    var counter = 0
                    while (merkki == 0 || counter < row.size) {
                        merkki = row[y2]
                        counter += 1
                        y2 += 1
                    }
//                    thinnge += "d$counter"
                    thinnge += "$counter"
                }
            }
            nnn.add(row[y1])
//            thinnge += "${row[y1]}, "
            x1 += way.x
            y1 += way.y
        }
        println("Thinge: $thinnge")
        return nnn
    }

    fun checkLine1(
        board2: Board,
        x: Int,
        y: Int,
        sign: Int,
        way: Way,
//        aaa: IntArray,
//        koko: Int
    ): MutableList<Int> {
        val board = board2.board
        var x1 = x
        var y1 = y
        val xMax = board.size
        var thinnge = ""
//        var arvo = 0
//        val aaa = IntArray(koko)
//        println("aaaa: ${aaa.toList()}")
        val nnn = mutableListOf<Int>()
        while (x1 in 0 until xMax) {
//            println("1x $x")
            val row = board[x1]
//            if (y !in 0..<row.size || row[y].sign != sign) break
            if (y1 !in 0..<row.size) break
//            println("x $x")
//            val ohgod = x-x1+y-y1
            nnn.add(row[y1])
//            aaa[arvo] = row[y1]
            thinnge += "${row[y1]}, "
//            thinnge += "${row[y].sign}, "
            x1 += way.x
            y1 += way.y
//            arvo += 1
        }
//        println("aaaa2: ${aaa.toList()}")
//        println("Thinge: $thinnge")
//        return thinnge
//        return aaa
        return nnn
    }
}

object SoBaaaad {
    private val mappe = HashMap<String, Int>(20)

    init {
        mappe[""] = 0
    }

    fun getter(key: String): Int {
        var result: Int?
        result = mappe.getOrDefault(key, null)

        if (result == null){
            result = mappe.getOrDefault(mirror(key), null)
        }
        if (result == null){
            result = mappe.getOrDefault(otherSide(key), null)
            if (result != null){
                result = -result
            }
        }
        if (result == null){
            result = mappe.getOrDefault(otherSide(mirror(key)), null)
            if (result != null){
                result = -result
            }
        }

        println("needed this: $key, got $result")

        if (result == null){
            result = 0
        }

        return result
    }

    fun mirror(key: String): String = key.reversed()

    fun otherSide(key: String): String {
        var result = ""
        key.forEach {
            result += when (it) {
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
}
