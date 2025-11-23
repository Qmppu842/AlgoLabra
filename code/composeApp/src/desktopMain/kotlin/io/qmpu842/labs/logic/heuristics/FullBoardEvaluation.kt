package io.qmpu842.labs.logic.heuristics

import kotlin.math.sign

fun fullBoardEvaluation(heuristicArgs: HeuristicArgs): Int {
    val (board, _, _, forSide, _) = heuristicArgs
    val (eka, toka) = board.getFullBoardValues()
    return eka * forSide.sign + toka * forSide.sign * -1
}
