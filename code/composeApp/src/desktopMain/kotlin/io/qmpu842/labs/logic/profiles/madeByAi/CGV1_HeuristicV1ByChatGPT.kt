package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile

/**
 * Made by chatgpt
 *
 */
@Suppress("ktlint:standard:class-naming")
class CGV1_HeuristicV1ByChatGPT : OpponentProfile() {
    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        val width = board.getWells()
        val needed = board.boardConfig.neededForWin
        val ourSign = if (forSide >= 0) 1 else -1
        val oppSign = -ourSign

        // collect legal moves in a sensible order (use middle-out if available)
        val legalCols = mutableListOf<Int>()
        val seq = board.getLegalsMiddleOutSeq().iterator()
        while (seq.hasNext()) {
            val c = seq.next()
            if (c == -1) break
            if (c in 0 until width && board.getWellSpace(c) > 0 && c !in legalCols) legalCols.add(c)
        }
        // fallback to simple pass over columns if sequence gave nothing
        if (legalCols.isEmpty()) {
            for (c in 0 until width) if (board.getWellSpace(c) > 0) legalCols.add(c)
        }
        if (legalCols.isEmpty()) return 0 // no moves, should not happen

        // helper: simulate board after placing a sign in column col
        fun simulateAfter(
            col: Int,
            sign: Int,
        ): Board? {
            val y = board.getHighestSpaceIndex(col)
            if (y < 0) return null
            val newArr = Array(board.board.size) { i -> board.board[i].copyOf() }
            newArr[col][y] = if (sign >= 0) 1 else -1
            return Board(
                board = newArr,
                boardConfig = board.boardConfig,
                history = board.history + col,
            )
        }

        // 1) Win immediately if possible
        for (c in legalCols) {
            val sim = simulateAfter(c, ourSign) ?: continue
            if (sim.doesPlaceHaveWinning(c, sim.getHighestSpaceIndex(c), neededForWin = needed)) {
                return c
            }
        }

        // 2) Find all opponent immediate winning moves on current board
        val oppImmediateWins = mutableSetOf<Int>()
        for (c in legalCols) {
            val simOpp = simulateAfter(c, oppSign) ?: continue
            if (simOpp.doesPlaceHaveWinning(c, simOpp.getHighestSpaceIndex(c), neededForWin = needed)) {
                oppImmediateWins.add(c)
            }
        }

        // 3) If opponent has immediate win(s), try to find a move that prevents all of them
        if (oppImmediateWins.isNotEmpty()) {
            // if single immediate threat -> block it directly if possible
            if (oppImmediateWins.size == 1) {
                val block = oppImmediateWins.first()
                if (block in legalCols) return block
            }

            // try to find a move that, after our play, leaves no immediate wins for opponent
            for (c in legalCols) {
                val afterOur = simulateAfter(c, ourSign) ?: continue
                // construct opponent's legal moves on this simulated board
                val oppRemainingWins = mutableListOf<Int>()
                val seq2 = afterOur.getLegalsMiddleOutSeq().iterator()
                val oppLegalOnAfter = mutableListOf<Int>()
                while (seq2.hasNext()) {
                    val cc = seq2.next()
                    if (cc == -1) break
                    if (cc in 0 until width && afterOur.getWellSpace(cc) > 0 && cc !in oppLegalOnAfter) {
                        oppLegalOnAfter.add(
                            cc,
                        )
                    }
                }
                if (oppLegalOnAfter.isEmpty()) {
                    for (cc in 0 until width) if (afterOur.getWellSpace(cc) > 0) oppLegalOnAfter.add(cc)
                }
                for (cc in oppLegalOnAfter) {
                    val simOpp = simulateAfter(cc, oppSign) ?: continue
                    // but we must pass the board state before our move: instead of reusing simulateAfter which uses original board,
                    // we create a copy from afterOur's array
                    val newArr = Array(afterOur.board.size) { i -> afterOur.board[i].copyOf() }
                    val yOpp = afterOur.getHighestSpaceIndex(cc)
                    if (yOpp < 0) continue
                    newArr[cc][yOpp] = if (oppSign >= 0) 1 else -1
                    val simOppBoard = Board(newArr, afterOur.boardConfig, afterOur.history + cc)
                    if (simOppBoard.doesPlaceHaveWinning(cc, yOpp, neededForWin = needed)) {
                        oppRemainingWins.add(cc)
                    }
                }
                if (oppRemainingWins.isEmpty()) {
                    return c
                }
            }

            // no single perfect block found; choose the blocking move if it reduces number of immediate opponent wins
            var bestBlock: Int? = null
            var bestRemainingThreats = Int.MAX_VALUE
            for (c in legalCols) {
                val afterOur = simulateAfter(c, ourSign) ?: continue
                val oppRemaining = mutableSetOf<Int>()
                val seq3 = afterOur.getLegalsMiddleOutSeq().iterator()
                val oppLegalOnAfter = mutableListOf<Int>()
                while (seq3.hasNext()) {
                    val cc = seq3.next()
                    if (cc == -1) break
                    if (cc in 0 until width && afterOur.getWellSpace(cc) > 0 && cc !in oppLegalOnAfter) {
                        oppLegalOnAfter.add(
                            cc,
                        )
                    }
                }
                if (oppLegalOnAfter.isEmpty()) {
                    for (cc in 0 until width) {
                        if (afterOur.getWellSpace(cc) > 0) {
                            oppLegalOnAfter.add(
                                cc,
                            )
                        }
                    }
                }
                for (cc in oppLegalOnAfter) {
                    val newArr = Array(afterOur.board.size) { i -> afterOur.board[i].copyOf() }
                    val yOpp = afterOur.getHighestSpaceIndex(cc)
                    if (yOpp < 0) continue
                    newArr[cc][yOpp] = if (oppSign >= 0) 1 else -1
                    val simOppBoard = Board(newArr, afterOur.boardConfig, afterOur.history + cc)
                    if (simOppBoard.doesPlaceHaveWinning(cc, yOpp, neededForWin = needed)) {
                        oppRemaining.add(cc)
                    }
                }
                if (oppRemaining.size < bestRemainingThreats) {
                    bestRemainingThreats = oppRemaining.size
                    bestBlock = c
                }
            }
            if (bestBlock != null) return bestBlock
        }

        // 4) No immediate crisis: choose move maximizing static heuristic (delta between our and opponent "line values")
        data class MoveScore(
            val col: Int,
            val score: Long,
        )

        val scored = mutableListOf<MoveScore>()
        for (c in legalCols) {
            val sim = simulateAfter(c, ourSign) ?: continue
            val (p1, p2) = sim.getFullBoardValues()
            val ourVal = if (ourSign == 1) p1 else p2
            val oppVal = if (ourSign == 1) p2 else p1
            val delta = ourVal.toLong() - oppVal.toLong()
            scored.add(MoveScore(c, delta))
        }
        if (scored.isNotEmpty()) {
            // pick the max-scoring move; tie-break randomly
            val maxScore = scored.maxOf { it.score }
            val best = scored.filter { it.score == maxScore }.map { it.col }
            return best.random(rand)
        }

        // 5) fallback: pick a random legal column
        return legalCols.random(rand)
    }
}
