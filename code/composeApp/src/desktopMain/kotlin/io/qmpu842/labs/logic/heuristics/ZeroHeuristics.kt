package io.qmpu842.labs.logic.heuristics

import io.qmpu842.labs.logic.Board

@HeurName("zeroHeuristics")
fun zeroHeuristics(
    heuristicArgs: HeuristicArgs =
        HeuristicArgs(
            board = Board(),
            x = 0,
            y = 0,
            forSide = 1,
            neededForWin = 4,
        ),
): Int = 0
