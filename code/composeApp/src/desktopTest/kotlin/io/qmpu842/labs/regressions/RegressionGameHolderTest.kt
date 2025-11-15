package io.qmpu842.labs.regressions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.MiniMaxV3Profile

class RegressionGameHolderTest :
    FunSpec({

        test("hasGameStopped") {
            var gameHolder =
                GameHolder(
                    playerA = MiniMaxV3Profile(depth = 4),
                    playerB = MiniMaxV3Profile(depth = 4),
                    bc = BoardConfig(),
                )

            repeat(6) {
                gameHolder = gameHolder.dropTokenLimited(3)
            }

            gameHolder.hasGameStopped() shouldBe false
//            gameHolder.whoisWinner1() shouldBe gameHolder.playerA

            gameHolder.board.isLastPlayWinning() shouldBe false

            gameHolder.board.doesPlaceHaveWinning(
                x = 3,
                y = 0,
                neededForWin = 4,
            ) shouldBe false

            gameHolder.board.doesPlaceHaveWinning(
                x = 2,
                y = 0,
                neededForWin = 4,
            ) shouldBe false

            gameHolder.board.getLegalMoves() shouldContainExactly listOf(0,1,2,4,5,6)

            (!gameHolder.board.isLastPlayWinning() && gameHolder.board.isAtMaxSize()) shouldBe false


        }
    })

// fun whoisWinner(): OpponentProfile? {
//    if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return null
//    return if (board.getOnTurnToken().sign == 1) playerA else playerB
// }
