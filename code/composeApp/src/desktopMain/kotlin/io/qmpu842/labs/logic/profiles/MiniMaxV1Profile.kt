package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.HEURESTIC_WIN
import io.qmpu842.labs.helpers.MINIMAX_LOSE
import io.qmpu842.labs.helpers.MINIMAX_WIN
import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.Way
import io.qmpu842.labs.otherSide
import java.util.*
import kotlin.math.*

class MiniMaxV1Profile(
    override var depth: Int = 10,
    override var timeLimit: Long = TRILLION,
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
        val minimaxResult1 =
            iterativeDeepening(
                board = board,
                forSide = forSide,
            )
//        val thinn = board.getLastMove() ?: -1
//        val minimaxResult1 =
//            minimax2(
//                board = board,
//                depth = depth,
//                maximizingPlayer = true,
//                alpha = Int.MIN_VALUE,
//                beta = Int.MAX_VALUE,
//                forLastSide = -forSide,
//                neededForWin = board.boardConfig.neededForWin,
//                lastX = thinn,
//                lastY = if (thinn != -1) board.getWellSpace(thinn) else -1,
// //                lastX = -1,
// //                lastY = -1,
//                token = abs(board.getOnTurnToken()),
//            )
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startingTime
//        println("aaaa")
//        println(minimaxResult1.toString())
//        val minimaxResult = minimaxResult1[0] ?: Pair( 0,0)
        val minimaxResult = minimaxResult1.lastEntry().value
//        println("It took me ${round(totalTime / 1000f)}s (or ${totalTime}ms) to do depth $depth")
//        println("The ${this.name} valinnat: ${minimaxResult.first} | ${minimaxResult.second}")
        return minimaxResult.second
    }

    fun iterativeDeepening(
        board: Board,
        forSide: Int,
    ): TreeMap<Int, Pair<Int, Int>> {
        var theOrdering = board.getLegalsMiddleOutSeq()
        var treeee = TreeMap<Int, Pair<Int, Int>>()
        val thinn = board.getLastMove() ?: -1
        var idDepth = 0
        while (System.currentTimeMillis() < currentMaxTime && idDepth < depth) {
            println("new iteration at depth $idDepth")
            val aaaa =
                treeee.values
                    .reversed()
                    .map { (_, move) -> move }
                    .asSequence() + sequenceOf(-1)
            println("aaaa1 ${aaaa.toList()}")

            val minimaxResult1 =
                minimax2(
                    board = board,
                    depth = idDepth,
                    maximizingPlayer = true,
                    alpha = Int.MIN_VALUE,
                    beta = Int.MAX_VALUE,
                    forLastSide = -forSide,
                    neededForWin = board.boardConfig.neededForWin,
                    lastX = thinn,
                    lastY = if (thinn != -1) board.getWellSpace(thinn) else -1,
                    token = abs(board.getOnTurnToken()),
                    seqseq = if (treeee.size < 2) theOrdering else aaaa,
                )
            treeee = minimaxResult1
            idDepth += 1
            println("moimoim")
            println("depth: ${this.depth}")
            println("idDepth: $idDepth")
            println("treee: $treeee")
            println("aaaa ${aaaa.toList()}")
            println("idDepth < depth ${idDepth < depth}")
        }
        return treeee
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
        seqseq: Sequence<Int> = board.getLegalsMiddleOutSeq(),
//    ): Pair<Int, Int> {
    ): TreeMap<Int, Pair<Int, Int>> {
//        println("nyt syvyydessä: $depth")
        val terminal =
            board.doesPlaceHaveWinning(
                x = lastX,
                y = lastY,
                neededForWin = neededForWin,
            )
        val hasStopped = isBoardFull(board.board)
        val uintiReissu = TreeMap<Int, Pair<Int, Int>>()

        if (terminal) {
//            return
            if (!maximizingPlayer) {
//                Pair(MINIMAX_WIN + depth, lastX)
                uintiReissu[MINIMAX_WIN + depth] = Pair(MINIMAX_WIN + depth, lastX)
//                uintiReissu
            } else {
//                Pair(MINIMAX_LOSE - depth, lastX)
                uintiReissu[MINIMAX_LOSE - depth] = Pair(MINIMAX_LOSE - depth, lastX)
            }
            return uintiReissu
        } else if (hasStopped) {
            // In case of Draw
//            return Pair(0, lastX)
            uintiReissu[0] = Pair(0, lastX)
            return uintiReissu
        }

        val time = System.currentTimeMillis()
//        val y = lastY // board.getWellSpace(lastX)

        if (depth == 0 || time >= currentMaxTime) {
//            return Pair(
//                lastMovesValue5(
//                    board = board,
//                    x = lastX,
//                    y = lastY,
//                    forSide = forLastSide * if (maximizingPlayer) -1 else 1,
//                    neededForWin = neededForWin,
//                ),
//                lastX,
//            )
            val value =
                lastMovesValue5(
                    board = board,
                    x = lastX,
                    y = lastY,
                    forSide = forLastSide * if (maximizingPlayer) -1 else 1,
                    neededForWin = neededForWin,
                ) * if (maximizingPlayer) -1 else 1

            uintiReissu[value] = Pair(value, lastX)
            return uintiReissu

//            return Pair(
//                simpleEval(board, forLastSide * if (maximizingPlayer) -1 else 1),
//                lastX,
//            )
        }
//        val moves = board.getLegalMovesFromMiddleOut()
//        val moves = board.getLegalsMiddleOutSeq()
        val moves = seqseq
        var alpha2 = alpha
        var beta2 = beta

//        val aasinSilta = mutableListOf<Pair<Int, Int>>()
//        val uintiReissu = TreeMap<Int, Pair<Int, Int>>()

//        println("moves: ${moves.takeWhile { i -> i != -1 }.toList()}")

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

//                aasinSilta.add(minied)
//                println("minified on max side: $minied")
                val highest = minied.lastEntry().value // ?: Pair(value, things.second)
//                println("highest: $highest")
                var kohde = highest.first + 1
                do {
                    kohde -= 1
                    val thhht = uintiReissu[kohde]
//                    println("ticking")
                } while (thhht != null)
//                println("end kohde: $kohde")
                uintiReissu[kohde] = Pair(highest.first, highest.second)

                if (highest.first > value) {
                    bestMove = move
                    value = highest.first
                }

//                val alpha2 = max(alpha, value)
//                if (beta <= alpha2) break
                alpha2 = max(alpha2, value)
                if (beta2 <= alpha2) break
            }
//            return Pair(value, bestMove)
//            return uintiReissu
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

//                aasinSilta.add(minied)
//                uintiReissu[minied[0].first] = minied
//
//                if (minied.first < value) {
//                    bestMove = move
//                    value = minied.first
//                }
//                println("minified on mini side: $minied")
                val lowest = minied.firstEntry().value
//                uintiReissu[lowest.first] = Pair(-lowest.first, things.second)

                var kohde = lowest.first - 1
                do {
                    kohde += 1
                    val thhht = uintiReissu[kohde]
                } while (thhht != null)
                uintiReissu[kohde] = Pair(-lowest.first, lowest.second)

                if (lowest.first < value) {
                    bestMove = move
                    value = lowest.first
                }

//                val beta2 = min(beta, value)
//                if (beta2 <= alpha) break
                beta2 = min(beta2, value)
                if (beta2 <= alpha2) break
            }
//            return Pair(value, bestMove)
        }
        return uintiReissu
    }

    /**
     * @param forLastSide you should put here the value of last turns side.
     *  Why this way?
     *  Because the first round of minimax does nothing, only after it can do the first moves
     */
    fun minimax1(
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
                    minimax1(
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
                    minimax1(
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
            if (abs(ggfgf) > holder) {
                holder = ggfgf
            }
        }
        return holder
    }

    fun lastMovesValue55(
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

            val tier0 = listOf("++++", "++o+") // voitto
            val tier1 = listOf("o+++o") // varma voitto, 3 ply
            val tier2 =
                listOf(
                    "++¤+",
                    "+++¤",
                    "++¤o",
                    "++o¤",
                    "+¤+o",
                    "+¤o+",
                    "+o+¤",
                    "¤++o",
                    "o++oo",
                ) // varmaish voitto, ~+3 ply
            val tier3 =
                listOf(
                    "++¤¤",
                    "+¤+¤",
                    "+¤¤+",
                    "¤++¤",
                    "+¤¤o+",
                    "+¤o¤+",
                ) // meh

            var value = 0

            tier3.forEach { t ->
                if (combo.contains(t)) {
                    value = HEURESTIC_WIN / 3
                } else if (combo.contains(otherSide(t))) {
                    value = -HEURESTIC_WIN / 3
                }
            }
            tier2.forEach { t ->
                if (combo.contains(t)) {
                    value = HEURESTIC_WIN / 2
                } else if (combo.contains(otherSide(t))) {
                    value = -HEURESTIC_WIN / 2
                }
            }

            tier1.forEach { t ->
                if (combo.contains(t)) {
                    value = HEURESTIC_WIN - 10
                } else if (combo.contains(otherSide(t))) {
                    value = -HEURESTIC_WIN + 10
                }
            }
            tier0.forEach { t ->
                if (combo.contains(t)) {
                    value = HEURESTIC_WIN
                } else if (combo.contains(otherSide(t))) {
                    value = -HEURESTIC_WIN
                }
            }

            if (abs(value) > holder) {
                holder = value
            }

//            val tier0 = listOf("++++")
//            val tierNeg0 = listOf("----")
//            val tier1 = listOf("o+++o")
//
//            val tier2 = listOf("+++o","++o+","+o++","o+++",)
//            val tierNeg1 = listOf("---o","--o-","-o--","o---",)
//
//            val tier3 = listOf("o++o","++o+","+o++","o+++",)

//            println("combo: $combo")
//            val ggfgf = SoBaaaad.getter(combo)
//            if (abs(ggfgf) > holder){
//                holder = ggfgf
//            }
        }
        return holder
    }

    fun lastMovesValue5(
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
        val ilmaLinjat = IntArray(size)
        val ilmaLinjatOpp = IntArray(size)

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

            if (omaSum < 1) continue
//            ilma linjat
            val resAir1: Int =
//                0
                board.checkLine(
                    x = x + (resOma1 * way.x),
                    y = y + (resOma1 * way.y),
                    sign = 0,
                    way = way,
                )
            if (resAir1 > 0) {
                ilmaLinjat[omaSum] += 1
            }
            val resAir2: Int =
//                0
                board.checkLine(
                    x = x + opposite.x + (resOma2 * opposite.x),
                    y = y + opposite.y + (resOma2 * opposite.y),
                    sign = 0,
                    way = opposite,
                )
//            if (resAir1 > 0 && resAir2 > 0) {
//                ilmaLinjat[omaSum + 1] += 1
//            }
//            val airSum = resAir1 + resAir2
//            ilmaLinjat[omaSum] += 1
            if (resAir2 > 0) {
                ilmaLinjatOpp[omaSum] += 1
            }
        }
//        println("omatLinjat ${omatLinjat.toList()}")
//        println("vihuLinjat ${vihuLinjat.toList()}")
//        println("ilmaLinjat ${ilmaLinjat.toList()}")
//        println("ilmaLinOpp ${ilmaLinjatOpp.toList()}")
//        println("pullaa")
        var omaCounter = 0
        var vihuCounter = 0
        for (i in size - 1 downTo 0) {
            val ekaIlma = ilmaLinjat[i]
            val tokaIlma = ilmaLinjatOpp[i]
            val oma = omatLinjat[i]
//            omaCounter += (oma + ekaIlma + tokaIlma) * (10f.pow(i)).toInt()
            if (ekaIlma > 0 && tokaIlma > 0) {
                omaCounter += (oma * (10f.pow(i)).toInt()) * 2
            } else {
                omaCounter += oma * (10f.pow(i)).toInt()
            }
//            omaCounter += oma * (10f.pow(i)).toInt()
            val vihu = vihuLinjat[i]
            vihuCounter += -(vihu * (10f.pow(i)).toInt())
//            vihuCounter += -((vihu + ekaIlma + tokaIlma) * (10f.pow(i)).toInt())
        }
        return if (abs(omaCounter) >= abs(vihuCounter)) {
            omaCounter
        } else {
            vihuCounter
        }
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
}

object SoBaaaad {
    private val mappe = HashMap<String, Int>(20)

    init {
        mappe["oo++++o"] = HEURESTIC_WIN
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
        mappe[""] = 0
    }

    fun getter(key: String): Int {
        var result: Int?
        result = mappe.getOrDefault(key, null)

        if (result == null) {
            result = mappe.getOrDefault(mirror(key), null)
        }
        if (result == null) {
            result = mappe.getOrDefault(otherSide(key), null)
            if (result != null) {
                result = -result
            }
        }
        if (result == null) {
            result = mappe.getOrDefault(otherSide(mirror(key)), null)
            if (result != null) {
                result = -result
            }
        }
        println(key)
        println("needed this: $key, got $result")

        if (result == null) {
            result = 0
        }

        return result
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
}
