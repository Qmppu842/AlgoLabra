package io.qmpu842.labs.logic.profiles.minimaxSidesteps

import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.*

class MiniMaxV2SelfTuningProfile(
    override var depth: Int = 10,
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
        println("The Minimax self tuning valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
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
        val terminal =
            board.doesPlaceHaveWinning(
                x = lastX,
                y = lastY,
                neededForWin = neededForWin,
            )
        val hasStopped = isBoardFull(board.board)

        if (terminal) {
            SoBaaaad2.updateOnWin(board, lastX, lastY, forLastSide, this.depth- depth)
            SoBaaaad2.updateOnLose(board, lastX, lastY, -forLastSide, this.depth - depth)
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
        }
        val moves = board.getLegalsMiddleOutSeq()
        var alpha2 = alpha
        var beta2 = beta

        if (maximizingPlayer) {
            var value = Int.MIN_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = false,
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

                alpha2 = max(alpha2, value)
                if (beta2 <= alpha2) break
            }
            return Pair(value, bestMove)
        } else {
            var value = Int.MAX_VALUE
            var bestMove = 0
            for (move in moves) {
                if (move == -1) break
                val things = board.dropTokenWithOutHistory(move, -forLastSide * token)
                val minied =
                    minimax2(
                        board = things.first,
                        depth = depth - 1,
                        maximizingPlayer = true,
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
            val ggfgf = SoBaaaad2.getter(combo)
//            println("ggfgf value: $ggfgf")
            if (abs(ggfgf) > holder) {
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
                    thinnge +=
                        if (counter > 1) {
                            if (counter % 2 == 1) "¤" else "*"
                        } else {
                            "o"
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
}

object SoBaaaad2 {
    private val lineToRule = HashMap<String, Int>(100_000)
    private val ruleToValue = HashMap<Int, Int>(100_000)
    private var latestRule = 0

    fun getter(key: String): Int {
//        val rule =
//            lineToRule[key] ?: lineToRule[mirror(key)] ?: lineToRule[otherSide(key)]
//                ?: lineToRule[otherSide(mirror(key))] ?: (latestRule + 1)
        val rule =
            lineToRule[key] ?: lineToRule[mirror(key)] ?: (latestRule + 1)

        if (latestRule >= rule) return ruleToValue[latestRule]!!

        latestRule = rule
        ruleToValue[latestRule] = 0
        return 0
    }

    fun adder(
        key: String,
        potency: Int,
    ) {
//        val rule =
//            lineToRule[key] ?: lineToRule[mirror(key)] ?: lineToRule[otherSide(key)]
//                ?: lineToRule[otherSide(mirror(key))] ?: (latestRule + 1)
//        val rule =
//            lineToRule[key] ?: lineToRule[mirror(key)] ?: (latestRule + 1)
        var rule = lineToRule[key]
        var mirror = lineToRule[mirror(key)]
        if (rule == null && mirror == null) {
            val asddsd = latestRule + 1
            lineToRule[key] = asddsd
            lineToRule[mirror(key)] = asddsd
            ruleToValue[asddsd] = potency
//            rule = asddsd
//            mirror = asddsd
        } else if (rule == null && mirror != null) {
            lineToRule[key] = mirror
            rule = mirror
            ruleToValue[rule] = ruleToValue.getOrDefault(rule, 0) + potency
        } else if (rule != null && mirror == null) {
            lineToRule[key] = rule
//            mirror = rule
            ruleToValue[rule] = ruleToValue.getOrDefault(rule, 0) + potency
        }
//        println("Rules:")
//        println("ruleToValue: $ruleToValue")
//        println("rule pairs:")
//        lineToRule.forEach { (t, u) ->
//            val gg = ruleToValue[u]
//            if (gg != null && gg > 0) {
//                println("line: $t and potency: $gg")
//            }
//        }
    }

    fun mirror(key: String): String = key.reversed()

    fun updateOnWin(
        board2: Board,
        lastX: Int,
        lastY: Int,
        forSide: Int,
        depth: Int,
    ) {
        val board = board2.deepCopy()
//        println("boarde:")
//        println(board.board.contentDeepToString())
        var theWay = Way.Up
        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
            val resOma =
                board.checkLine(
                    x = lastX,
                    y = lastY,
                    sign = forSide,
                    way = way,
                )

            val resOmaback =
                board.checkLine(
                    x = lastX + opposite.x,
                    y = lastY + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
//            println("wayyyyyy: $way")
//            println("resOma: $resOma")
//            println("resOmaback: $resOmaback")
//            println("board.boardConfig.neededForWin: ${board.boardConfig.neededForWin}")
            if (resOmaback + resOma >= board.boardConfig.neededForWin) {
                theWay = way
//                println("chosen way is: $way")
                break
            }
        }
//        println("combo way: $theWay")
        var combo = thingyyy(theWay, board, lastX, lastY, forSide)
        val nenenen = hashMapOf<Int, Pair<Int, Int>>()

//        adder(
//            key = combo,
//            potency = 4
//        )

        board.board.forEachIndexed { xxxx, thingee ->
            thingee.forEachIndexed { yyyy, tuote ->
                if (tuote != 0) {
                    nenenen[abs(tuote)] = Pair(xxxx, yyyy)
//                    nenenen[tuote] = Pair(xxxx,yyyy)
                }
            }
        }

        val ggg = nenenen.entries.sortedBy { (key, _) -> key }

//        println("ggg: $ggg")
//        println("combo1: $combo")

        var counter = 0
        combo = ""
        val forWin = board.boardConfig.neededForWin
        ggg.reversed().forEach { (_, cooords) ->
            var combo2 = thingyyy(theWay, board, lastX, lastY, forSide)
            if (combo != combo2) {
                adder(
                    key = combo2,
//                    potency = forWin * 2 - counter,
//                    potency = 10f.pow((depth + forWin) * 2 - counter).toInt(),
                    potency = (depth + forWin) * 2 - counter
                )
                counter += 1
                combo = combo2
            }
            val (xe, ye) = cooords
            board.board[xe][ye] = 0
        }

//        println("combo: $combo")
    }

    private fun thingyyy(
        theWay: Way,
        board: Board,
        lastX: Int,
        lastY: Int,
        forSide: Int,
    ): String {
        // AAAAAAAAAAAAAAAAAAAAAAAA

        val opposite = Way.opp[theWay.ordinal]
        val resOma =
            checkLine(
                board2 = board,
                x = lastX + theWay.x,
                y = lastY + theWay.y,
                sign = forSide,
                way = theWay,
            )

        val resOmaback =
            checkLine(
                board2 = board,
                x = lastX + opposite.x,
                y = lastY + opposite.y,
                sign = forSide,
                way = opposite,
            )
        val combo = resOmaback.reversed() + "+" + resOma
        return combo
    }

    fun updateOnLose(
    board2: Board,
    lastX: Int,
    lastY: Int,
    forSide: Int,
    depth: Int,
    ) {
        val board = board2.deepCopy()
//        println("boarde:")
//        println(board.board.contentDeepToString())
        var theWay = Way.Up
        for (way in Way.half) {
            val opposite = Way.opp[way.ordinal]
            val resOma =
                board.checkLine(
                    x = lastX,
                    y = lastY,
                    sign = forSide,
                    way = way,
                )

            val resOmaback =
                board.checkLine(
                    x = lastX + opposite.x,
                    y = lastY + opposite.y,
                    sign = forSide,
                    way = opposite,
                )
//            println("wayyyyyy: $way")
//            println("resOma: $resOma")
//            println("resOmaback: $resOmaback")
//            println("board.boardConfig.neededForWin: ${board.boardConfig.neededForWin}")
            if (resOmaback + resOma >= board.boardConfig.neededForWin) {
                theWay = way
//                println("chosen way is: $way")
                break
            }
        }
//        println("combo way: $theWay")
        var combo = thingyyy(theWay, board, lastX, lastY, forSide)
        val nenenen = hashMapOf<Int, Pair<Int, Int>>()

//        adder(
//            key = combo,
//            potency = 4
//        )

        board.board.forEachIndexed { xxxx, thingee ->
            thingee.forEachIndexed { yyyy, tuote ->
                if (tuote != 0) {
                    nenenen[abs(tuote)] = Pair(xxxx, yyyy)
//                    nenenen[tuote] = Pair(xxxx,yyyy)
                }
            }
        }

        val ggg = nenenen.entries.sortedBy { (key, _) -> key }

//        println("ggg: $ggg")
//        println("combo1: $combo")

        var counter = 0
        combo = ""
        val forWin = board.boardConfig.neededForWin
        ggg.reversed().forEach { (_, cooords) ->
            var combo2 = thingyyy(theWay, board, lastX, lastY, forSide)
            if (combo != combo2) {
                adder(
                    key = combo2,
//                    potency = forWin * 2 - counter,
//                    potency = -10f.pow((depth + forWin) * 2 - counter).toInt(),
                    potency = -(depth + forWin) * 2 - counter
                )
                counter += 1
                combo = combo2
            }
            val (xe, ye) = cooords
            board.board[xe][ye] = 0
        }

//        println("combo: $combo")
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
                    thinnge +=
                        if (counter > 1) {
                            if (counter % 2 == 1) "¤" else "*"
                        } else {
                            "o"
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
}
