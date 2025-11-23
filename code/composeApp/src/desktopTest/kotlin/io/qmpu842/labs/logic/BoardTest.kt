package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig

class BoardTest :
    FunSpec({

        beforeTest {
        }

        test("getLastMove on empty") {
            val board = Board()

            board.getLastMove() shouldBe null
        }

        test("getLastMove after moves should give the last well") {
            val height = 3
            var board =
                Board(
                    boardWidth = 3,
                    boardHeight = height,
                )
            board.getLastMove() shouldBe null

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
                    ),
                )

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
// ----------------------------------------
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

        test("getLegalMovesFromMiddleOut on empty odd board") {
            var board =
                Board(
                    boardWidth = 7,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            val list = board.getLegalMovesFromMiddleOut()
            list shouldContainInOrder listOf(3, 4, 2, 5, 1, 6, 0)
            list shouldContainExactly listOf(3, 4, 2, 5, 1, 6, 0)
        }
        test("getLegalMovesFromMiddleOut on empty even board") {
            var board =
                Board(
                    boardWidth = 6,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            val list = board.getLegalMovesFromMiddleOut()
            list shouldContainInOrder listOf(3, 2, 4, 1, 5, 0)
            list shouldContainExactly listOf(3, 2, 4, 1, 5, 0)
        }

        test("getLegalsMiddleOutSeq on empty odd board") {
            var board =
                Board(
                    boardWidth = 7,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
            }
            list shouldContainInOrder listOf(3, 4, 2, 5, 1, 6, 0, -1)
        }

        test("getLegalsMiddleOutSeq on empty odd board #22") {
            var board =
                Board(
                    boardWidth = 7,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            val list2 = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
                for (move1 in board.getLegalsMiddleOutSeq()) {
                    list2.add(move1)
                    print(move1)
                    if (move1 == -1) break
                }
            }
            list shouldContainInOrder listOf(3, 4, 2, 5, 1, 6, 0, -1)
        }

        test("getLegalsMiddleOutSeq on empty odd board #2222") {
            var board =
                Board(
                    boardWidth = 7,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            fun thinggg(
                boarde: Board,
                list: MutableList<Int>,
            ): MutableList<Int> {
                for (move1 in boarde.getLegalsMiddleOutSeq()) {
                    list.add(move1)
                    print(move1)
                    if (move1 == -1) break
                }
                return list
            }

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            val list2 = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
                for (move1 in board.getLegalsMiddleOutSeq()) {
                    list2.add(move1)
                    print(move1)
                    if (move1 == -1) break
                }
            }
            list shouldContainInOrder listOf(3, 4, 2, 5, 1, 6, 0, -1)
        }
        test("getLegalsMiddleOutSeq on empty even board") {
            var board =
                Board(
                    boardWidth = 6,
                    boardHeight = 6,
                    neededForWin = 4,
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
            }
            list shouldContainInOrder listOf(2, 3, 1, 4, 0, 5, -1)
        }

        test("getLegalsMiddleOutSeq on partly full board") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(13, -12, -10, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 11, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
            }
            println()
            list shouldContainInOrder listOf(4, 2, 5, 1, 6, 0, -1)
        }

        test("getLegalsMiddleOutSeq on partly full board twice") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(13, -12, -10, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 11, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            var thing = false
            for (move in seq) {
                list.add(move)
                if (thing && move == -1) break
                if (move == -1) thing = true
            }
            list shouldContainInOrder listOf(4, 2, 5, 1, 6, 0, -1, 4, 2, 5, 1, 6, 0, -1)
        }

        test("getLegalsMiddleOutSeq on quite full board") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(-20, 19, -18, 17, 3, -2),
                            intArrayOf(-16, 15, -14, 7, 5, -4),
                            intArrayOf(13, -12, -10, 9, -8, -6),
                            intArrayOf(-24, 23, -22, 21, 11, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            for (move in seq) {
                list.add(move)
                print(move)
                if (move == -1) break
            }
            list shouldContainInOrder listOf(5, 6, 0, -1)
        }

        test("getLegalsMiddleOutSeq on full board") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(1),
                            intArrayOf(-2),
                            intArrayOf(3),
                            intArrayOf(-4),
                        ),
                )

            val seq = board.getLegalsMiddleOutSeq()
            val list = mutableListOf<Int>()
            var thing = false
            for (move in seq) {
                list.add(move)
                if (thing && move == -1) break
                if (move == -1) thing = true
            }
            list shouldContainInOrder listOf(-1, -1)
        }

        test("getOnTurnToken on empty board") {
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )
            board.getOnTurnToken() shouldBe 1
        }

        test("getOnTurnToken on board with one turn") {
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )
            board = board.dropLockedToken(1)
            board.getOnTurnToken() shouldBe -2
        }

        test("getOnTurnToken on board with 32 turns") {
            val height = 100
            val width = 1
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )
            repeat(32) {
                board = board.dropLockedToken(0)
                board = board.dropLockedToken(0)
            }
            board.getOnTurnToken() shouldBe 65
        }

        test("isAtMaxSize on empty board") {
            val board = Board()
            board.isAtMaxSize() shouldBe false
        }

        test("isAtMaxSize on board with line") {
            var board = Board()

            repeat(6) {
                board = board.dropLockedToken(3)
            }
            board.isAtMaxSize() shouldBe false
            board.getLegalMoves() shouldContainExactly listOf(0, 1, 2, 4, 5, 6)
        }

        test("isAtMaxSize on full board") {
            var board = Board()
            repeat(7) { well ->
                repeat(6) { wellSize ->
                    board = board.dropLockedToken(well)
                }
            }
            board.isAtMaxSize() shouldBe true
        }

// ----------------------------------------
        test("isLastPlayWinning on straight line down not winning #2") {
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
//                board.doesPlaceHaveWinning(
//                    x = 2,
//                    y = TODO(),
//                    neededForWin = 4,
//                ) shouldBe false
            }
        }

        test("isLastPlayWinning on straight line down winning #2") {
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

        test("isLastPlayWinning on straight line down with win #2") {
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

        test("isLastPlayWinning both should be winning when two are asked #2") {
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

        test("isLastPlayWinning with rising diagonal win #2") {
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

        test("isLastPlayWinning with lowering diagonal win #2") {
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

        test("isLastPlayWinning with negative straight win #2") {
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

        test("getGeneralLineValues with empty board") {
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
            board.getVerticalLineValues() shouldBe Pair(0, 0)
        }

        test("getGeneralLineValues with stacked lines of three") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            board.getVerticalLineValues() shouldBe Pair(0, 0)
        }

        test("getGeneralLineValues with two vertical towers of three") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, -6, -4, -2),
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            board.getVerticalLineValues() shouldBe Pair(13, 13)
        }
        test("getGeneralLineValues with two vertical towers of two") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -4, -2),
                            intArrayOf(0, 0, 0, 0, 3, 1),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            board.getVerticalLineValues() shouldBe Pair(4, 4)
        }

        test("getGeneralLineValues with six vertical towers of three and two") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -4, -2),
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, 0, -8, -6),
                            intArrayOf(0, 0, 0, 11, 9, 7),
                            intArrayOf(0, 0, 0, 0, -12, -10),
                        ),
                )
            board.getVerticalLineValues() shouldBe Pair(18, 12)
        }

        test("getGeneralLineValues with stacked lines of three horizontal") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, -4, -2),
                            intArrayOf(0, 0, 0, 5, 3, 1),
                            intArrayOf(0, 0, 0, 0, -8, -6),
                            intArrayOf(0, 0, 0, 11, 9, 7),
                            intArrayOf(0, 0, 0, 0, -12, -10),
                        ),
                )
            board.getVerticalLineValues() shouldBe Pair(18, 12)
        }
        test("getVerticalLineValues with two diagonal towers stacked") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0,  0,  0,  1),
                            intArrayOf(0, 0, 0,  0,  9, -2),
                            intArrayOf(0, 0, 0,  5, -4,  3),
                            intArrayOf(0, 0, 0, -8,  7, -6),
                            intArrayOf(0, 0, 0,  0,  0,  0),
                            intArrayOf(0, 0, 0,  0,  0,  0),
                            intArrayOf(0, 0, 0,  0,  0,  0),
                        ),
                )
//            board.getGeneralLineValues(board.katto + board.seina, Way.UpRight) shouldBe Pair(9, 9)
//            board.getGeneralLineValues(board.katto + board.seina, Way.UpRight) shouldBe Pair(9, 9)
//            board.getGeneralLineValues(board.katto + board.seina, Way.UpRight) shouldBe Pair(9, 9)
//            board.getGeneralLineValues(board.pohja + board.seina, Way.UpRight) shouldBe Pair(9, 9)
            val asddasd = board.pohja + board.rightSeina
            println("asdasd $asddasd")
            asddasd.forEach { point ->
                println(point)
            }
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.LeftUp) shouldBe Pair(9, 9)


//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.Up) shouldBe Pair(10, 9) //nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.UpRight) shouldBe Pair(11, 9) // nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.Right) shouldBe Pair(12, 9) //nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.RightDown) shouldBe Pair(13, 9) //nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.Down) shouldBe Pair(14, 9) // nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.DownLeft) shouldBe Pair(15, 9) nope
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.Left) shouldBe Pair(16, 9) 0,1
//            board.getGeneralLineValues((board.pohja + board.seina).toSet().toList(), Way.LeftUp) shouldBe Pair(17, 9) nope


            board.getGeneralLineValues((board.pohja + board.rightSeina).toSet().toList(), Way.LeftUp) shouldBe Pair(17, 9)
        }
        test("getVerticalLineValues from real board") {
            val board2 = Board(BoardConfig(), "44444222245355266776662611135533", -1)
            board2.getFullBoardValues() shouldBe Pair(6, 4)
        }
    })
