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
            val board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )
            board.getLastMove() shouldBe -1

            board.dropToken(0, 1)
            board.dropToken(0, -2)
            board.getLastMove() shouldBe 0


            board.dropToken(1, 3)
            board.getLastMove() shouldBe 1

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
            val board =
                Board(
                    boardWidth = 1,
                    boardHeight = height,
                )
            val endHeight = board.dropToken(0, 1)
            endHeight shouldBe height - 1
            board.board.first().toList() shouldContainAll listOf(0, 0, 1)
        }

        test("dropToken 3 times to same well will return the height") {
            val height = 3
            val board =
                Board(
                    boardWidth = 1,
                    boardHeight = height,
                )
            var endHeight: Int
            repeat(3) { num ->
                endHeight = board.dropToken(0, 1)
                endHeight shouldBe height - num - 1
            }
        }

        test("dropToken returns -1 on over fill") {
            val board =
                Board(
                    boardWidth = 1,
                    boardHeight = 3,
                )
            var endHeight = 0
            repeat(4) { num ->
                endHeight = board.dropToken(0, 1)
            }
            endHeight shouldBe -1
            board.board.first().toList() shouldContainAll listOf(1, 1, 1)
        }
    })
