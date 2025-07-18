package io.qmpu842.labs.regressions

import io.kotest.core.spec.style.FunSpec
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
    })
