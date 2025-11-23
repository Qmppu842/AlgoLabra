package io.qmpu842.labs.logic.heuristics

import kotlin.math.sign

@HeurName("fullBoardEvaluation")
fun fullBoardEvaluation(heuristicArgs: HeuristicArgs): Int {
    val (board, x, y, forSide, neededForWin) = heuristicArgs
    val (eka, toka) = board.getFullBoardValues()

//    if (forSide)
//    val ads = eka * forSide.sign + toka * forSide.sign * -1

    return eka * forSide.sign + toka * forSide.sign * -1
}

fun fullBoardEvaluation2(heuristicArgs: HeuristicArgs): Int {
    val (board, x, y, forSide, neededForWin) = heuristicArgs

    val checkArea = neededForWin * 2 - 1
    val relevancy = neededForWin + 1
    var fullPotentialPositive = 0
    var fullPotentialNegative = 0

    var ilmaaEnnen = 0
    var ilmaaJalkeen = 0
    var ilmaa = 0
    var charCount = 0
    var lastChar = ' '
    val board2 = board.board
    board2.forEachIndexed { x, column ->
        column.forEachIndexed { y, row ->
//            if (lastChar != row){
//
//            }
            if (row.sign == forSide) {
            } else if (row.sign != forSide) {
            } else {
                ilmaa++
            }
        }
    }

    board2.forEachIndexed { x, column ->
//        val
    }

    val starters = mutableSetOf<Pair<Int, Int>>()

    return forSide * fullPotentialPositive + forSide * -1 * fullPotentialNegative
}

fun fullBoardEvaluation1(heuristicArgs: HeuristicArgs): Int {
    val (board, x, y, forSide, neededForWin) = heuristicArgs

    val checkArea = neededForWin * 2 - 1
    val relevancy = neededForWin + 1
    var fullPotentialPositive = 0
    var fullPotentialNegative = 0

    var ilmaaEnnen = 0
    var ilmaaJalkeen = 0
    val board2 = board.board
    board2.forEachIndexed { x, column ->
        column.forEachIndexed { y, row ->
        }
    }

    return forSide * fullPotentialPositive + forSide * -1 * fullPotentialNegative
}

object Hmm {
    private val dicti = HashMap<String, Int>()

    fun getValue(key: String): Int {
        if (dicti.isEmpty()) initDicti()
        return dicti[key] ?: 0
    }

    private fun initDicti(neededForWin: Int = 4) {
    }
}
