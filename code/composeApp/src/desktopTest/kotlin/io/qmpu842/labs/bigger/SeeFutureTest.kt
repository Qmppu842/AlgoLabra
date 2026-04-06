package io.qmpu842.labs.bigger

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.HUNDRED_K
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.MiniMaxV3Profile
import kotlin.math.abs

class SeeFutureTest : FunSpec({


    context("Can the minimax without iterative deepening see the win and stay on it") {
        withData(
            // @formatter:off
            GameToPlay(
                depth = 6,
                start = "4441534236441655572",
                end =   "4441534236441655572535332",
            ),
            GameToPlay(
                depth = 9,
                start = "44444222245355266776662611135533",
                end =   "44444222245355266776662611135533353777711",
            ),
            GameToPlay(
                depth = 9,
                start = "44444222273746666773171171556112",
                end =   "44444222273746666773171171556112555536233",
            ),
            GameToPlay(
                depth = 16,
                start = "42656466455454627",
                end =   "426564664554546274556222211111133",
            ),
            GameToPlay(
                depth = 16,
                start = "4423432337225575224445",
                end =   "44234323372255752244455573377711111166",
            ),
            GameToPlay(
                depth = 16,
                start = "4444415666643223622531511",
                end =   "44444156666432236225315115525633327771177",
            ),
            GameToPlay(
                depth = 16,
                start = "4444415666643223622531555",
                end =   "44444156666432236225315552563332777111177",
            ),
            GameToPlay(
                depth = 17,
                start = "444441566664322362253155",
                end =   "44444156666432236225315552563332777111177",
            ),
            // @formatter:on
        ) { (playerA, playerB, start, end) ->
            val bc = BoardConfig()

            var gameHolder = GameHolder(
                board = Board(bc, start, offset = -1),
                playerA = playerA,
                playerB = playerB,
                bc = bc,
            )

            repeat(end.length - start.length) {
                var profile = (gameHolder.playerOnTurn() as MiniMaxV3Profile)
                gameHolder = gameHolder.dropTokenLimited()
                var value = profile.lastMoveEval
                abs(value) shouldBe (HUNDRED_K + it)
            }

            gameHolder.whoisWinner() shouldBe if (end.length % 2 == 0) -1 else 1

            val history = gameHolder.board.history.map { thing -> thing + 1 }.joinToString("")
            history shouldBe end
        }
    }



    context("Can the minimax with iterative deepening see the win and stay on it") {
        withData(
            // @formatter:off
            GameToPlay(
                start = "4441534236441655572",
                end =   "4441534236441655572535332",
                timelimit = 1000,
            ),
            GameToPlay(
                start = "44444222245355266776662611135533",
                end =   "44444222245355266776662611135533353777711",
                timelimit = 1000,
            ),
            GameToPlay(
                start = "44444222273746666773171171556112",
                end =   "44444222273746666773171171556112555536233",
                timelimit = 10000,
            ),
            GameToPlay(
                timelimit = 10000,
                start = "42656466455454627",
                end =   "426564664554546274556222211111133",
            ),
            GameToPlay(
                timelimit = 10000,
                start = "4423432337225575224445",
                end =   "44234323372255752244455573377711111166",
            ),
            GameToPlay(
                timelimit = 10000,
                start = "4444415666643223622531511",
                end =   "44444156666432236225315115525633327771177",
            ),
            GameToPlay(
                timelimit = 10000,
                start = "4444415666643223622531555",
                end =   "44444156666432236225315552563332777111177",
            ),
            GameToPlay(
                timelimit = 10000,
                start = "444441566664322362253155",
                end =   "44444156666432236225315552563332777111177",
            ),
            // @formatter:on
        ) { (playerA, playerB, start, end) ->
            val bc = BoardConfig()

            var gameHolder = GameHolder(
                board = Board(bc, start, offset = -1),
                playerA = playerA,
                playerB = playerB,
                bc = bc,
            )


            repeat(end.length - start.length) {
                var profile = (gameHolder.playerOnTurn() as MiniMaxV3Profile)
                gameHolder = gameHolder.dropTokenLimited()
                abs(profile.lastMoveEval) shouldBeGreaterThanOrEqual HUNDRED_K
            }

            gameHolder.whoisWinner() shouldBe if (end.length % 2 == 0) -1 else 1

            val history = gameHolder.board.history.map { thing -> thing + 1 }.joinToString("")
            history shouldBe end
        }
    }
})