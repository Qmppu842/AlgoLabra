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

            var legals: MutableList<Int>
            legals = board.getLegalMoves()
            legals shouldContainAll listOf(0, 1, 2)
            board.dropToken(legals.removeLastOrNull()!!)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf(1, 2)
            board.dropToken(legals.removeLastOrNull()!!)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf(2)
            board.dropToken(legals.removeLastOrNull()!!)

            legals = board.getLegalMoves()
            legals shouldContainAll listOf()
        }

        test("dropToken returns the 0 height") {
            val board =
                Board(
                    boardWidth = 1,
                    boardHeight = 3,
                )
            val thing = board.dropToken(0)
            thing shouldBe 0
        }

        test("dropToken 3 times to same well will return the height") {
            val board =
                Board(
                    boardWidth = 1,
                    boardHeight = 3,
                )
            var endHeight: Int
            repeat(3) { num ->
                endHeight = board.dropToken(0)
                endHeight shouldBe num
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
                endHeight = board.dropToken(0)
            }
            endHeight shouldBe -1
        }
    })
