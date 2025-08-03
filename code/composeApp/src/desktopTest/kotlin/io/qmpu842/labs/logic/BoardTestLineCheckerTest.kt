package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.summa
import java.awt.Point

class BoardTestLineCheckerTest :
    FunSpec({

        test("doubleLineWithJumpStart flat line") {
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
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 5),
                    sign = -1,
                    way = Way.Right,
                )
            lineLength.summa() shouldBe 4
        }

        test("doubleLineWithJumpStart down diagonal line") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, -14, -12, 11, -10),
                            intArrayOf(0, 0, 0, 0, 0, 13),
                        ),
                )
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 3),
                    sign = -1,
                    way = Way.RightDown,
                )
            lineLength.summa() shouldBe 3
        }

        test("doubleLineWithJumpStart up diagonal line") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 9, -8, -6),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, -14, -12, 11, -10),
                            intArrayOf(0, 0, 0, 0, 0, 13),
                        ),
                )
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 3),
                    sign = -1,
                    way = Way.LeftUp,
                )
            lineLength.summa() shouldBe 3
        }

//        -----------------------
        test("doubleLine flat line") {
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
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 5),
                    sign = -1,
                    way = Way.Right
                )
            lineLength.summa() shouldBe 4
        }

        test("doubleLine down diagonal line") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 9, -8, -6),
                            intArrayOf(0, 0, 0,  0, 0,  0),
                            intArrayOf(0, 0, -14, -12, 11, -10),
                            intArrayOf(0, 0, 0, 0, 0, 13),
                        ),
                )
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 3),
                    sign = -1,
                    way = Way.RightDown
                )
            lineLength.summa() shouldBe 3
        }

        test("doubleLine up diagonal line") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 1),
                            intArrayOf(0, 0, 0, 0, 3, -2),
                            intArrayOf(0, 0, 0, 7, 5, -4),
                            intArrayOf(0, 0, 0, 9, -8, -6),
                            intArrayOf(0, 0, 0,  0, 0,  0),
                            intArrayOf(0, 0, -14, -12, 11, -10),
                            intArrayOf(0, 0, 0, 0, 0, 13),
                        ),
                )
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(4, 3),
                    sign = -1,
                    way = Way.LeftUp
                )
            lineLength.summa() shouldBe 3
        }

        test("checkLine on flat line at (3|5)") {
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
            val lineLength =
                board.checkLine(
                    current = Point(3, 5),
                    sign = -1,
                    way = Way.Right
                )
            lineLength shouldBe 3
        }

        test("checkLine on flat line at (5|5)") {
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
            val lineLength =
                board.checkLine(
                    current = Point(5, 5),
                    sign = -1,
                    way = Way.Right
                )
            lineLength shouldBe 1
        }

        test("checkLine on flat line at (3|4)") {
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
            val lineLength =
                board.checkLine(
                    current = Point(3, 3),
                    sign = 1,
                    way = Way.Right
                )
            lineLength shouldBe 2
        }

        test("checkLine on down diagonal line at (2|3)") {
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
            val lineLength =
                board.checkLine(
                    current = Point(2, 3),
                    sign = 1,
                    way = Way.RightDown
                )
            lineLength shouldBe 3
        }

        test("checkLine on straight line down") {
            val dropThisMany = 5
            val height = 6
            val width = 7
            var board =
                Board(
                    boardWidth = width,
                    boardHeight = height,
                )

            repeat(dropThisMany) {
                board = board.dropToken(1, it + 1)
            }
            val lineLength =
                board.checkLine(
                    current = Point(1, 1),
                    sign = 1,
                    way = Way.Down
            )
            lineLength shouldBe dropThisMany
        }


        test("DoubleLine with opponent as jump start") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, -2, 1),
                            intArrayOf(0, 0, 0, 0, -4, 3),
                            intArrayOf(0, 0, 0, 0, 0, -8),
                            intArrayOf(0, 0, 0, 0, -6, 5),
                            intArrayOf(0, 0, 0, 0, 0, 7),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )
            val lineLength =
                board.doubleLineWithJumpStart(
                    current = Point(2, 5),
                    sign = 1,
                    way = Way.Right,
                )
            lineLength.summa() shouldBe 4
        }
 })
