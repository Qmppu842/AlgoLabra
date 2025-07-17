package io.qmpu842.labs.regressions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.logic.Board

class RegressionBoardTest :
    FunSpec({

        test("Check Win on these moves") {
            var board = Board()

            board = board.dropToken(5, 1)
            board = board.dropToken(5, -2)
            board = board.dropToken(3, 3)
            board = board.dropToken(3, -4)
            board = board.dropToken(2, 5)
            board = board.dropToken(2, -6)

            val asd = board.isLastPlayWinning(4)
            asd shouldBe false

            board = board.dropToken(4, 7)

            val asd2 = board.isLastPlayWinning(4)
            asd2 shouldBe true
        }
    })
