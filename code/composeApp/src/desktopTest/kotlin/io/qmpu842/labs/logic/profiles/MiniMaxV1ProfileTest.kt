package io.qmpu842.labs.logic.profiles

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.*
import io.qmpu842.labs.logic.Board

class MiniMaxV1ProfileTest :
    FunSpec({

        beforeTest {
        }

        test("lastMovesValue5 with win #1") {
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
            minimax.lastMovesValue5(
                board = board,
                x = 4,
                y = 5,
                forSide = -1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 with win #2") {
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
            minimax.lastMovesValue5(
                board = board,
                x = 3,
                y = 2,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 with win #3") {
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
            val minimax = MiniMaxV1Profile(depth = 1)

            board = board.dropToken(4, -10)
            minimax.lastMovesValue5(
                board = board,
                x = 4,
                y = 5,
                forSide = -1,
            ) shouldBe HEURESTIC_WIN

            board = board.dropToken(3, 11)
            minimax.lastMovesValue5(
                board = board,
                x = 3,
                y = 2,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 with win #4") {
            var board = Board()

            board = board.dropLockedToken(0)

            repeat(3) {
                board = board.dropLockedToken(1)
                board = board.dropLockedToken(0)
            }

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 0,
                y = 2,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 with trap #5") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                        ),
                )

            board = board.dropToken(2, 5)

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 2,
                y = 5,
                forSide = 1,
            ) shouldBe TRAP_WIN
        }

        test("lastMovesValue5 with corner two #6") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                        ),
                )

            board = board.dropToken(4, 5)

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 4,
                y = 5,
                forSide = 1,
            ) shouldBe 99 //TODO
        }
        test("lastMovesValue5 with win #7") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  -6, 5),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                        ),
                )

            board = board.dropToken(1, 7)

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 1,
                y = 5,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 with gap win #8") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  -6, 5),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                        ),
                )

            board = board.dropToken(2, 7)

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 2,
                y = 5,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }
        test("lastMovesValue5 #9") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 7,   5, -4),
                            intArrayOf(0, 0, 0, 0,   0, 0),
                            intArrayOf(0, 0, 0, 0,   3, -2),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0,11, 9,  -8, -6),
                            intArrayOf(0, 0, 0, 0,   0, 1),
                            intArrayOf(0, 0, 0, 0,  -12, -10),
                        ),
                )

            board = board.dropLockedToken(1 )

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 1,
                y = 5,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 #9-5") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, -14, 7,   5, -4),
                            intArrayOf(0, 0, 0, 0,   0, 0),
                            intArrayOf(0, 0, 0, 0,   3, -2),
                            intArrayOf(0, 0, 0, 0,  0, 0),
                            intArrayOf(0, 0,11, 9,  -8, -6),
                            intArrayOf(0, 0, 0, 0,   13, 1),
                            intArrayOf(0, 0, 0, 0,  -12, -10),
                        ),
                )

            board = board.dropLockedToken(1 )

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 1,
                y = 5,
                forSide = 1,
            ) shouldBe HEURESTIC_WIN
        }

        test("lastMovesValue5 #10") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0,  0,  0,   9),
                            intArrayOf(0, 0, 0,  0,  0,  -8),
                            intArrayOf(0, 0, 0,  0,  -6,  5),
                            intArrayOf(0, 0, 0,  0,  -4,  3),
                            intArrayOf(0, 0, 0, -12, -2,  1),
                            intArrayOf(0, 0, 0,  0,  11, -10),
                            intArrayOf(0, 0, 0,  0,  13,  7),
                        ),
                )

            board = board.dropLockedToken(1)

            val minimax = MiniMaxV1Profile()
            minimax.lastMovesValue5(
                board = board,
                x = 1,
                y = 4,
                forSide = -1,
            ) shouldBe HEURESTIC_WIN
        }
        test("MiniMax #1") {
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
                            intArrayOf(0, 0, 0, 0, 0, -10),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(MINIMAX_WIN, 3)
        }

        test("MiniMax #2") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(0, 3)
        }

        test("MiniMax #3") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 11, -10, 9),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -8, -6),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = 1,
                )
            result shouldBe Pair(BLOCK_WIN, 3)
        }

        test("MiniMax #4") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, -6, -4, -2),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(MINIMAX_WIN, 0)
        }

        test("MiniMax #5") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, -6, -4, -2),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 7),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = 1,
                )
            result shouldBe Pair(MINIMAX_WIN, 1)
        }

        test("MiniMax #6") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(MINIMAX_WIN, 2)
        }

        test("MiniMax #7") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, 0, 7),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = TRILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = 1,
                    lastX = 4,
                    lastY = 5,
                )
            result shouldBe Pair(BLOCK_WIN, 2)
        }

        test("MiniMax #8") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, 0, -8),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 7),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(MINIMAX_WIN, 0)
        }

        test("MiniMax #9") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 7),
                            intArrayOf(0, 0, 0, 0, 0, -8),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val minimax = MiniMaxV1Profile(depth = 1, timeLimit = MILLION)
            val result =
                minimax.minimax2(
                    board = board,
                    forLastSide = -1,
                )
            result shouldBe Pair(MINIMAX_WIN, 6)
        }
    })
