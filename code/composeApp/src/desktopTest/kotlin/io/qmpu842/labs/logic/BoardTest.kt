package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class BoardTest :
    FunSpec({

        beforeTest {
        }

        test("getLastMove on empty") {
            val board = Board()

            board.getLastMove() shouldBe -1
        }

        test("getLastMove after moves should give the last well") {
            val height = 3
            var board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )
            board.getLastMove() shouldBe -1

            board = board.dropToken(0, 1)
            board = board.dropToken(0, -2)
            board.getLastMove() shouldBe 0

            board = board.dropToken(1, 3)
            board.getLastMove() shouldBe 1
        }

        test("getLastMove full column should not go to history") {
            val height = 3
            var board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )

            board = board.dropToken(0, 1)
            board = board.dropToken(0, -2)
            board = board.dropToken(0, 3)
            board = board.dropToken(0, -4)
            board.history shouldContainAll listOf(0, 0, 0)
        }

        test("getLegalMoves should give all the empty wells") {
            val board =
                Board(
                    boardWidth = 2,
                    boardHeight = 3,
                )

            val legals = board.getLegalMoves()

            legals shouldContainAll listOf(0, 1)
        }

        test("getLegalMoves should give only wells with space") {
            val board =
                Board(
                    arrayOf(
                    IntArray(5) {
                        it
                    },
                    IntArray(5) {
                        if (it < 3) {
                            it
                        } else {
                            0
                        }
                    },
                    IntArray(5) {
                        -it
                    },
                ))

            val legals = board.getLegalMoves()

            legals shouldContainAll listOf(1)
        }

        test("getLegalMoves should reduce when filling up") {
            val board =
                Board(
                    boardWidth = 3,
                    boardHeight = 1,
                )
            var legals: MutableList<Int> = board.getLegalMoves()
            legals shouldContainAll listOf(0, 1, 2)
            board.dropToken(legals.removeLastOrNull()!!, 1)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf(0, 1)
            board.dropToken(legals.removeLastOrNull()!!, 1)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf(0)
            board.dropToken(legals.removeLastOrNull()!!, 1)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf()
        }

        test("dropToken returns the 0 height") {
            val height = 3
            var board =
                Board(
                    boardWidth = 1,
                    boardHeight = height,
                )
            board = board.dropToken(0, 1)
            board.board.first().toList() shouldContainAll listOf(0, 0, 1)
        }

        test("dropToken 3 times to same well will return the height") {
            val height = 3
            var board =
                Board(
                    boardWidth = 1,
                    boardHeight = height,
                )
            repeat(3) { num ->
                board = board.dropToken(0, num)
            }
            board.board.first().toList() shouldContainAll listOf(0, 1, 2)
        }

        test("dropToken returns -1 on over fill") {
            var board =
                Board(
                    boardWidth = 1,
                    boardHeight = 3,
                )
            repeat(4) { num ->
                board = board.dropToken(0, 1)
            }
            board.board.first().toList() shouldContainAll listOf(1, 1, 1)
        }

        test("undoLastMove should remove the last until there is nothing to remove") {
            val height = 3
            var board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )

            board = board.dropToken(1, 1)
            board = board.dropToken(2, -2)
            board = board.dropToken(0, 3)
            board.board.flatMap { it.toList() } shouldContainAll listOf(0, 1, -2, 3)

            board = board.undoLastMove()
            board.board.flatMap { it.toList() } shouldContainAll listOf(0, 1, -2)

            board = board.undoLastMove()
            board.board.flatMap { it.toList() } shouldContainAll listOf(0, 1)

            board = board.undoLastMove()
            board.board.flatMap { it.toList() } shouldContainAll listOf(0)

        }

        test("getWellSpace should give the correct amount of zeros still present") {
            val height = 3
            var board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )

            board = board.dropToken(1, 1)
            board = board.dropToken(2, -2)
            board = board.dropToken(1, 3)

            var thing = board.getWellSpace(0)
            thing shouldBe 3

            thing = board.getWellSpace(1)
            thing shouldBe 1

            thing = board.getWellSpace(2)
            thing shouldBe 2
        }

        test("isLastPlayWinning on straight line down not winning") {
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )
            repeat(3) {
                board = board.dropToken(1, (it + 1) * 2 - 1)
                board.isLastPlayWinning(4) shouldBe false

                board = board.dropToken(2, -((it + 1) * 2))
                board.isLastPlayWinning(4) shouldBe false
            }
        }

        test("isLastPlayWinning on straight line down winning") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, -6, -4, -2),
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            board = board.dropToken(3, 7)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("isLastPlayWinning on straight line down with win") {
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )
            repeat(4) {
                board = board.dropToken(1, it + 1)
            }

            board.isLastPlayWinning(4) shouldBe true
        }

        test("isLastPlayWinning both should be winning when two are asked") {
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )

            repeat(3) {
                board = board.dropToken(1, (it + 1) * 2 - 1)
                board.isLastPlayWinning(4) shouldBe false

                board = board.dropToken(2, -(it + 1) * 2)
                board.isLastPlayWinning(4) shouldBe false
            }

            board = board.dropToken(1, 7)
            board.isLastPlayWinning(4) shouldBe true

            board = board.dropToken(2, -8)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("isLastPlayWinning with rising diagonal win") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, -10),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            board = board.dropToken(3, 11)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("isLastPlayWinning with lowering diagonal win") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 11, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, -12, 1),
                            intArrayOf(0, 0, 0, 0, 0, -10),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            board = board.dropToken(0, 13)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("isLastPlayWinning with negative straight win") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, -10, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 11, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            board = board.dropToken(0, -12)
            board.isLastPlayWinning(4) shouldBe true
        }
    })
