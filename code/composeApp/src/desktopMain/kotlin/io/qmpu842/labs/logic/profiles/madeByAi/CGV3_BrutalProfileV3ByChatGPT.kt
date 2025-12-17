package io.qmpu842.labs.logic.profiles.madeByAi

import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile
import java.lang.System.nanoTime
import kotlin.math.max

/**
 * CGV3_BrutalProfileV3ByChatGPT — single-file "meanest" opponent.
 *
 * Strategy summary (implemented inside nextMove only — no external contracts required):
 * 1) Immediate wins first (play winning move).
 * 2) Immediate blocks second (block opponent's immediate wins).
 * 3) Iterative-deepening negamax with alpha-beta, move ordering (middle-out + history),
 *    transposition table, and quick heuristic evaluation based on Board.getFullBoardValues().
 * 4) Time-limited: uses the profile's `timeLimit` (ms) to stop search and return best found move.
 *
 * Notes:
 * - This class only overrides nextMove; all helpers are local inside that method to keep the file self-contained.
 * - It simulates moves by cloning the board columns (cheap for typical Connect-4 sizes).
 */
@Suppress("ktlint:standard:class-naming")
class CGV3_BrutalProfileV3ByChatGPT : OpponentProfile() {
    override fun nextMove(
        board: Board,
        forSide: Int,
    ): Int {
        // Local helpers & state (kept inside method for single-file containment)
        val startNano = nanoTime()
        val timeBudgetMs = max(1L, timeLimit) // use provided timeLimit (caller may set to 1000ms)
        val deadline = startNano + timeBudgetMs * 1_000_000L - 5_000_000L // small safety margin

        val width = board.getWells()
        if (width <= 0) return 0

        val myPiece = if (forSide >= 0) 1 else -1
        val oppPiece = -myPiece
        val WIN_SCORE = 10_000_000
        val LOSS_SCORE = -WIN_SCORE

        // fast legal generator (center-out preferred)
        fun legalMovesOrdered(b: Board): List<Int> {
            val order = mutableListOf<Int>()
            // Use board.getLegalsMiddleOutSeq() if available, else build center-out
            try {
                val seq = b.getLegalsMiddleOutSeq()
                for (x in seq) {
                    if (x == -1) break
                    if (b.getWellSpace(x) > 0) order.add(x)
                }
                if (order.isNotEmpty()) return order
            } catch (_: Throwable) {
                // fallthrough to center-out fallback
            }
            val mid = width / 2
            var offset = 0
            while (order.size < width) {
                val l = mid - offset
                val r = mid + offset
                if (l >= 0 && board.getWellSpace(l) > 0) order.add(l)
                if (r != l && r < width && board.getWellSpace(r) > 0) order.add(r)
                offset++
                if (offset > width) break
            }
            // ensure uniqueness & full coverage
            return (order + (0 until width).filter { board.getWellSpace(it) > 0 && !order.contains(it) }).toList()
        }

        // clone board arrays
        fun cloneBoardArray(src: Array<IntArray>): Array<IntArray> {
            val out = Array(src.size) { i -> src[i].clone() }
            return out
        }

        // simulate placing piece at column col, returning new Board or null if illegal
        fun simulatePlace(
            b: Board,
            col: Int,
            piece: Int,
        ): Board? {
            val free = b.getWellSpace(col)
            if (free <= 0) return null
            val yIndex = free - 1 // getHighestSpaceIndex equivalent
            val newArr = cloneBoardArray(b.board)
            newArr[col][yIndex] = piece
            val newHist = b.history + col
            return Board(newArr, b.boardConfig, newHist)
        }

        // quick immediate tactical checks: win or block
        val legals = (0 until width).filter { board.getWellSpace(it) > 0 }
        if (legals.isEmpty()) return 0

        // 1) Win immediately if possible
        for (col in legalMovesOrdered(board)) {
            val free = board.getWellSpace(col)
            if (free <= 0) continue
            val y = free - 1
            val b2 = simulatePlace(board, col, myPiece) ?: continue
            try {
                if (b2.doesPlaceHaveWinning(col, y, board.boardConfig.neededForWin)) {
                    return col
                }
            } catch (_: Throwable) {
                // fallback: if doesPlaceHaveWinning misbehaves, rely on other checks
            }
        }

        // 2) Block opponent immediate wins (if multiple threats, pick one that also threatens back later)
        val opponentThreats = mutableListOf<Int>()
        for (col in legalMovesOrdered(board)) {
            val free = board.getWellSpace(col)
            if (free <= 0) continue
            val y = free - 1
            val b2 = simulatePlace(board, col, oppPiece) ?: continue
            try {
                if (b2.doesPlaceHaveWinning(col, y, board.boardConfig.neededForWin)) {
                    opponentThreats.add(col)
                }
            } catch (_: Throwable) {
                // ignore
            }
        }
        if (opponentThreats.size == 1) {
            // single forced block — play it
            return opponentThreats[0]
        } else if (opponentThreats.size > 1) {
            // multiple immediate threats: choose a blocking move if available that also creates self-win,
            // otherwise pick the blocking that minimizes opponent followups (heuristic: prefer center)
            // Try also to play a move that both blocks and wins (rare but checked above).
            val center = width / 2
            return opponentThreats.minByOrNull { kotlin.math.abs(it - center) } ?: opponentThreats.first()
        }

        // --- Iterative deepening negamax with alpha-beta, transposition table & history ---
        val tt = HashMap<String, Int>() // transposition table (simple)
        val historyHeuristic = IntArray(width) { 0 } // add preference to moves that historically worked
        var bestMoveSoFar = legals.first()
        var bestScoreSoFar = Int.MIN_VALUE

        // simple evaluation: oriented to `forSide`
        fun evaluate(b: Board): Int {
            val (p1, p2) =
                try {
                    b.getFullBoardValues()
                } catch (_: Throwable) {
                    0 to 0
                }
            val base = if (forSide >= 0) p1 - p2 else p2 - p1
            // additional small biases: prefer central occupancy, prefer mobility
            var centerBonus = 0
            val mid = width / 2
            for (c in 0 until width) {
                val colArr = b.board[c]
                for (y in colArr.indices) {
                    val s = colArr[y]
                    if (s == 0) continue
                    val dist = kotlin.math.abs(c - mid)
                    if (s == myPiece) {
                        centerBonus += (width - dist)
                    } else {
                        centerBonus -= (width - dist)
                    }
                }
            }
            // combine, scaled
            return base * 100 + centerBonus
        }

        // negamax with alpha-beta
        var nodesSearched = 0L

        fun boardKey(
            b: Board,
            depth: Int,
        ): String = b.toString() + "|d$depth"

        // return score for side "player" (piece sign)
        fun negamax(
            b: Board,
            depth: Int,
            alphaInit: Int,
            betaInit: Int,
            player: Int,
        ): Int {
            // time check
            if (nanoTime() > deadline) throw java.util.concurrent.TimeoutException()
            nodesSearched++

            // transposition lookup
            val key = boardKey(b, depth)
            tt[key]?.let { return it }

            // terminal checks: last move win, full
            if (b.history.isNotEmpty()) {
                // If last move was a win for the player who played it, return accordingly
                try {
                    if (b.isLastPlayWinning(b.boardConfig.neededForWin)) {
                        // last play winner is whoever moved last: that is -player (since player is to move)
                        val score = LOSS_SCORE + (1000 - depth) // prefer quicker wins higher; here losing for current player
                        tt[key] = score
                        return score
                    }
                } catch (_: Throwable) {
                    // ignore
                }
            }
            if (b.isAtMaxSize()) {
                tt[key] = 0
                return 0
            }
            if (depth <= 0) {
                val ev = evaluate(b)
                tt[key] = ev
                return ev
            }

            var alpha = alphaInit
            var beta = betaInit
            var best = Int.MIN_VALUE

            // move ordering: generate legal moves ordered, then sort by history heuristic & center proximity
            val moves =
                legalMovesOrdered(b)
                    .map { col ->
                        val hist = historyHeuristic.getOrNull(col) ?: 0
                        val centerDist = kotlin.math.abs(col - (width / 2))
                        Triple(col, -hist, centerDist)
                    }.sortedWith(compareBy({ it.second }, { it.third }))
                    .map { it.first }

            for (col in moves) {
                val free = b.getWellSpace(col)
                if (free <= 0) continue
                val y = free - 1
                val child = simulatePlace(b, col, player) ?: continue

                // immediate win detection
                try {
                    if (child.doesPlaceHaveWinning(col, y, b.boardConfig.neededForWin)) {
                        val score = WIN_SCORE - (1000 - depth)
                        // update history heuristic
                        historyHeuristic[col] = historyHeuristic.getOrNull(col)!! + (1 shl (depth.coerceAtMost(30)))
                        tt[key] = score
                        return score
                    }
                } catch (_: Throwable) {
                    // ignore
                }

                val valN =
                    try {
                        -negamax(child, depth - 1, -beta, -alpha, -player)
                    } catch (t: java.util.concurrent.TimeoutException) {
                        throw t
                    } catch (e: Throwable) {
                        // on any internal failure, use shallow eval fallback
                        -evaluate(child)
                    }

                if (valN > best) {
                    best = valN
                }
                alpha = max(alpha, valN)
                if (alpha >= beta) {
                    // fail-hard beta cutoff: update history heuristic
                    historyHeuristic[col] = historyHeuristic.getOrNull(col)!! + (1 shl (depth.coerceAtMost(20)))
                    break
                }
            }

            val finalScore = if (best == Int.MIN_VALUE) evaluate(b) else best
            tt[key] = finalScore
            return finalScore
        }

        // Iterative deepening
        try {
            var maxDepth = 1
            val maxAllowedDepth =
                when {
                    depth > 0 -> depth

                    // if external depth set, honor it as cap
                    else -> 12 // safety cap; typical connect4 solving depth rarely needs >12 in 1s for small boards
                }

            while (maxDepth <= maxAllowedDepth) {
                // time check
                if (nanoTime() > deadline) break

                var localBestMove = bestMoveSoFar
                var localBestScore = Int.MIN_VALUE

                // root move loop (ordered)
                val rootMoves = legalMovesOrdered(board)
                for (col in rootMoves) {
                    if (nanoTime() > deadline) break
                    val free = board.getWellSpace(col)
                    if (free <= 0) continue
                    val y = free - 1
                    val child = simulatePlace(board, col, myPiece) ?: continue

                    // immediate win was checked earlier, but do again
                    try {
                        if (child.doesPlaceHaveWinning(col, y, board.boardConfig.neededForWin)) {
                            return col
                        }
                    } catch (_: Throwable) {
                    }

                    val score =
                        try {
                            -negamax(child, maxDepth - 1, LOSS_SCORE, WIN_SCORE, -myPiece)
                        } catch (t: java.util.concurrent.TimeoutException) {
                            // ran out of time during a child search: stop ID
                            throw t
                        } catch (e: Throwable) {
                            // fallback
                            -evaluate(child)
                        }

                    if (score > localBestScore) {
                        localBestScore = score
                        localBestMove = col
                    }
                }

                // if we completed this depth, accept its best
                bestMoveSoFar = localBestMove
                bestScoreSoFar = localBestScore

                // small optimistic escape: if score is a decisive win, stop early
                if (localBestScore >= WIN_SCORE / 2) break

                maxDepth++
            }
        } catch (_: java.util.concurrent.TimeoutException) {
            // time's up — return best move found so far
        } catch (_: Throwable) {
            // any uncaught exceptions: fall through to returning best known
        }

        // Fallback: if no search moved bestMoveSoFar, choose center-preferred legal
        if (board.getWellSpace(bestMoveSoFar) <= 0) {
            val ordered = legalMovesOrdered(board)
            if (ordered.isNotEmpty()) bestMoveSoFar = ordered.first()
        }

        return bestMoveSoFar.coerceIn(0, width - 1)
    }
}
