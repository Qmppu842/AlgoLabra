package io.qmpu842.labs.logic.heuristics

fun fullBoardEvaluation(heuristicArgs: HeuristicArgs): Int {
    val (board, x, y, forSide, neededForWin) = heuristicArgs

    board.board
    var fullPotentialPositive = 0
    var fullPotentialNegative = 0

    var ilmaaEnnen = 0
    var ilmaaJalkeen = 0
//    for board

    return forSide * fullPotentialPositive + forSide * -1 * fullPotentialNegative
}
