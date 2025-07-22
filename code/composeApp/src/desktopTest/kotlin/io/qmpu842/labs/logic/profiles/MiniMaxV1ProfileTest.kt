package io.qmpu842.labs.logic.profiles

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.logic.Board

class MiniMaxV1ProfileTest : FunSpec({

    beforeTest {
    }

    test("lastMovesValue4 with win #1") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 1),
                        intArrayOf(0, 0, 0, 0, 3, -2),
                        intArrayOf(0, 0, 0, 7, 5, -4),
                        intArrayOf(0, 0, 0, 9, -8, -6),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )

        board = board.dropToken(4, -10)

        val minimax = MiniMaxV1Profile()
        val lastMoveValue = minimax.lastMovesValue4(board)

        lastMoveValue shouldBe Int.MIN_VALUE
    }

    test("lastMovesValue4 with win #2") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 1),
                        intArrayOf(0, 0, 0, 0, 3, -2),
                        intArrayOf(0, 0, 0, 7, 5, -4),
                        intArrayOf(0, 0, 0, 9, -8, -6),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )

        board = board.dropToken(5, -10)
        board = board.dropToken(3, 11)

        val minimax = MiniMaxV1Profile()
        val lastMoveValue = minimax.lastMovesValue4(board)

        lastMoveValue shouldBe Int.MAX_VALUE
    }

    test("lastMovesValue4 with win3") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 1),
                        intArrayOf(0, 0, 0, 0, 3, -2),
                        intArrayOf(0, 0, 0, 7, 5, -4),
                        intArrayOf(0, 0, 0, 9, -8, -6),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )

        board = board.dropToken(5, -10)
        board = board.dropToken(3, 11)

        val minimax = MiniMaxV1Profile()
        val lastMoveValue = minimax.lastMovesValue4(board)

        lastMoveValue shouldBe Int.MAX_VALUE
    }
})
