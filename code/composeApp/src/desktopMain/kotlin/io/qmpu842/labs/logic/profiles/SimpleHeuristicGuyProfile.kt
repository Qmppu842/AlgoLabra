package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.HeuristicThing

class SimpleHeuristicGuyProfile : OpponentProfile() {
    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        val heuristics = HeuristicThing.allTheWells(board, forSide = forSide, maxDepth = 5)
        var maxIndex = 0
        var maxValue = heuristics.first()

        heuristics.forEachIndexed { index, i ->
            if (maxValue < i) {
                maxIndex = index
                maxValue = i
            }
        }
        return maxIndex
    }
}
