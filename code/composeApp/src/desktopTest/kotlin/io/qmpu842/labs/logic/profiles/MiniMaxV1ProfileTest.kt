package io.qmpu842.labs.logic.profiles

import io.kotest.core.spec.style.FunSpec
import io.qmpu842.labs.logic.Board

class MiniMaxV1ProfileTest : FunSpec({

    beforeTest {
    }

    test("minimax") {

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

        val asd = MiniMaxV1Profile()
//        asd.minimax()
    }
})
