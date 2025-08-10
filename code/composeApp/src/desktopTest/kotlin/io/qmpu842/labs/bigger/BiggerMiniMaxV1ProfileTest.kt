package io.qmpu842.labs.bigger

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.HumanProfile
import io.qmpu842.labs.logic.profiles.MiniMaxV1Profile

class BiggerMiniMaxV1ProfileTest :
    FunSpec({

        beforeTest {
        }

        test("nextMove") {
//        Alku:  44444222245355266776662611135533
//        Loppu: 44444222245355266776662611135533375777311
//        Ensimmäinen pelaaja pystyy voittamaan viidellä omalla siirollaan
//            3757_7731_1
//            x6x6_x6x0_x

            val bc = BoardConfig()
            val minimax =
                MiniMaxV1Profile(
                    depth = 5,
                    timeLimit = TRILLION,
                )
            val human = HumanProfile()
            var gameHolder =
                GameHolder(
                    board = Board(bc, "44444222245355266776662611135533", offset = -1),
                    playerA = minimax,
                    playerB = human,
                    bc = bc,
                )
            var asd = gameHolder.board.getLegalMoves()

            gameHolder = gameHolder.dropTokenLimited(6)
            gameHolder = gameHolder.dropTokenLimited()
            asd = gameHolder.board.getLegalMoves()

            gameHolder = gameHolder.dropTokenLimited(6)
            gameHolder = gameHolder.dropTokenLimited()
            asd = gameHolder.board.getLegalMoves()

            gameHolder = gameHolder.dropTokenLimited(6)
            gameHolder = gameHolder.dropTokenLimited()
            asd =  gameHolder.board.getLegalMoves()

            gameHolder = gameHolder.dropTokenLimited(0)
            gameHolder = gameHolder.dropTokenLimited()
            asd =  gameHolder.board.getLegalMoves()

            gameHolder.hasGameStopped() shouldBe true
            gameHolder.whoisWinner() shouldBe 1

            val history =
                gameHolder.board.history
                    .map { thing -> thing + 1 }
                    .joinToString("")
            history shouldBe "44444222245355266776662611135533375777311"
        }
    })
