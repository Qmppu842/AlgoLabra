package io.qmpu842.labs.logic.heuristics

import io.qmpu842.labs.logic.Board

/**
 * I guess this is neat param packing.
 * Thanks for suggesting this chatgpt.
 * Since there is no way of naming typealias params.
 */
data class HeuristicArgs(
    val board: Board,
    val x: Int,
    val y: Int,
    val forSide: Int,
    val neededForWin: Int,
)
typealias HeuristicFun = (HeuristicArgs) -> Int

interface HeuristicUser {
    val heuristic: HeuristicFun
}
