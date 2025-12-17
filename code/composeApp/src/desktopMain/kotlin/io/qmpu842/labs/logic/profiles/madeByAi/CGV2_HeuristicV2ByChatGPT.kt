package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs
import kotlin.math.max

/**
 * HeuristicProfile:
 * - One-file OpponentProfile implementation.
 * - Strategy:
 *   1) Play immediate win if available.
 *   2) Block opponent immediate win if needed.
 *   3) Score all legal moves by:
 *        - local heuristic (board line-values via getFullBoardValues),
 *        - center proximity preference,
 *        - penalize moves that allow opponent immediate win on their next turn.
 *   4) Break ties randomly.
 *
 * Notes:
 * - Uses only the provided nextMove(board: Board, forSide: Int): Int signature.
 * - Uses Board methods exposed in the snippet (getWellSpace, getWells, getFullBoardValues, isLastPlayWinning).
 */
@Suppress("ktlint:standard:class-naming")
class CGV2_HeuristicV2ByChatGPT : OpponentProfile() {

    override fun nextMove(board: Board, forSide: Int): Int {
        // Helper: collect legal columns (not full)
        fun legalColumns(b: Board): List<Int> =
            (0 until b.getWells()).filter { b.getWellSpace(it) > 0 }

        // Helper: simulate placing `side` into column `col` returning new Board
        fun simulatePlace(orig: Board, col: Int, side: Int): Board {
            val width = orig.getWells()
            val newBoardArray: Array<IntArray> = Array(width) { idx -> orig.board[idx].copyOf() }
            val y = orig.getHighestSpaceIndex(col)
            if (y >= 0) {
                newBoardArray[col][y] = side
            }
            val newHistory = orig.history + col
            return Board(newBoardArray, orig.boardConfig, newHistory)
        }

        val legals = legalColumns(board)
        if (legals.isEmpty()) return 0 // no moves - arbitrary valid integer

        // 1) Immediate win: if any move makes us win now, take it (prefer center-most wins)
        val center = board.getWells() / 2.0
        var bestCenterWin: Int? = null
        var bestCenterDist = Double.MAX_VALUE
        for (c in legals) {
            val sim = simulatePlace(board, c, forSide)
            if (sim.isLastPlayWinning()) {
                val dist = abs(c - center)
                if (dist < bestCenterDist) {
                    bestCenterDist = dist
                    bestCenterWin = c
                }
            }
        }
        if (bestCenterWin != null) return bestCenterWin

        // 2) Immediate block: if opponent has a winning move next turn, block it.
        val oppSide = -forSide
        // find any opponent-winning column (they would play next). Prefer center-most column to block.
        var bestCenterBlock: Int? = null
        bestCenterDist = Double.MAX_VALUE
        for (c in legals) {
            // If opponent plays c and wins, we must consider blocking c now.
            val simOpp = simulatePlace(board, c, oppSide)
            if (simOpp.isLastPlayWinning()) {
                val dist = abs(c - center)
                if (dist < bestCenterDist) {
                    bestCenterDist = dist
                    bestCenterBlock = c
                }
            }
        }
        if (bestCenterBlock != null) return bestCenterBlock

        // 3) Heuristic scoring for remaining columns
        data class Candidate(val col: Int, val score: Double)

        val candidates = mutableListOf<Candidate>()
        val baseFullBoardValues = board.getFullBoardValues() // maybe for tie-breaking if wanted

        for (c in legals) {
            val sim = simulatePlace(board, c, forSide)

            // Basic heuristic from board.getFullBoardValues (player1 vs player2)
            val (p1, p2) = sim.getFullBoardValues()
            val rawAdvantage = if (forSide > 0) (p1 - p2).toDouble() else (p2 - p1).toDouble()

            // Center preference (closer to center gets a bonus)
            val centerBonus = (board.getWells() / 2.0) - abs(c - center) // bigger is better

            // Penalize if any opponent immediate winning reply exists after this move
            var opponentHasImmediateWinAfterThis = false
            val oppLegalsAfter = (0 until sim.getWells()).filter { sim.getWellSpace(it) > 0 }
            for (oppC in oppLegalsAfter) {
                val simOppReply = simulatePlace(sim, oppC, oppSide)
                if (simOppReply.isLastPlayWinning()) {
                    opponentHasImmediateWinAfterThis = true
                    break
                }
            }

            // Depth-2 rough evaluation: check best opponent reply advantage and subtract it (simple lookahead)
            var worstOppReplyAdvantage = Double.NEGATIVE_INFINITY
            for (oppC in oppLegalsAfter) {
                val simOppReply = simulatePlace(sim, oppC, oppSide)
                val (p1r, p2r) = simOppReply.getFullBoardValues()
                val oppAdv = if (forSide > 0) (p2r - p1r).toDouble() else (p1r - p2r).toDouble()
                worstOppReplyAdvantage = max(worstOppReplyAdvantage, oppAdv)
            }
            if (oppLegalsAfter.isEmpty()) worstOppReplyAdvantage = 0.0

            // Compose final score
            var score = 0.0
            score += rawAdvantage * 1.0            // primary: board-line evaluator
            score += centerBonus * 0.8             // secondary: center control
            score -= worstOppReplyAdvantage * 1.2  // subtract opponent's best reply advantage
            if (opponentHasImmediateWinAfterThis) score -= 10000.0 // huge penalty if we allow immediate win

            // Slight randomness for tie-breaking
            score += (rand.nextDouble() - 0.5) * 1e-3

            candidates.add(Candidate(c, score))
        }

        // pick max score candidate (ties naturally resolved by ordering and small randomness)
        val best = candidates.maxByOrNull { it.score }
        if (best != null) return best.col

        // fallback: random legal column
        return legals.random(rand)
    }
}
