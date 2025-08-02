package io.qmpu842.labs.regressions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.logic.Board

class RegressionBoardTest :
    FunSpec({

        test("Check win on these moves #1") {
            var board = Board()

            board = board.dropToken(5, 1)
            board = board.dropToken(5, -2)
            board = board.dropToken(3, 3)
            board = board.dropToken(3, -4)
            board = board.dropToken(2, 5)
            board = board.dropToken(2, -6)

            board.isLastPlayWinning(4) shouldBe false

            board = board.dropToken(4, 7)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("Check no win on these moves #2") {
            var board = Board()

            board = board.dropToken(3, 1)
            board = board.dropToken(2, -2)
            board = board.dropToken(2, 3)
            board = board.dropToken(3, -4)
            board = board.dropToken(3, 5)
            board = board.dropToken(4, -6)
            board = board.dropToken(4, 7)
            board = board.dropToken(4, -8)
            board = board.dropToken(4, 9)

            board.isLastPlayWinning(4) shouldBe false
        }

        test("Check no win on these moves #3") {
            var board = Board()

            board = board.dropToken(3, 1)
            board = board.dropToken(2, -2)
            board = board.dropToken(3, 3)
            board = board.dropToken(2, -4)

            board = board.dropToken(3, 5)
            board.isLastPlayWinning(4) shouldBe false

            board = board.dropToken(2, -6)
            board.isLastPlayWinning(4) shouldBe false
        }

        test("Check no win on these moves #4") {
            var board = Board()

            board = board.dropToken(4, 1)
            board = board.dropToken(0, -2)
            board = board.dropToken(0, 3)
            board = board.dropToken(3, -4)

            board = board.dropToken(0, 5)
            board = board.dropToken(5, -6)
            board = board.dropToken(0, 7)
            board.isLastPlayWinning(4) shouldBe false

            board = board.dropToken(6, -8)
            board.isLastPlayWinning(4) shouldBe false

            board = board.dropToken(0, 9)
            board.isLastPlayWinning(4) shouldBe true
        }

        test("Check no win on these moves #5") {
            var board = Board()

            repeat(6) {
               board = board.dropLockedToken(3)
            }
            board.isLastPlayWinning(4) shouldBe false
            board.isAtMaxSize() shouldBe false
            board.getLegalMoves() shouldContainExactly listOf(0,1,2,4,5,6)
        }
    })
