package io.qmpu842.labs.bigger

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.TRILLION
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.MiniMaxV1Profile
import io.qmpu842.labs.logic.profiles.OpponentProfile

data class GameToPlay(
    val playerA: OpponentProfile =
        MiniMaxV1Profile(
            depth = 9,
            timeLimit = TRILLION,
        ),
    val playerB: OpponentProfile =
        MiniMaxV1Profile(
            depth = 9,
            timeLimit = TRILLION,
        ),
    val start: String,
    val end: String,
) {
    constructor(depth: Int = 9, start: String, end: String) : this(
        playerA =
            MiniMaxV1Profile(
                depth = depth,
                timeLimit = TRILLION,
            ),
        playerB =
            MiniMaxV1Profile(
                depth = depth,
                timeLimit = TRILLION,
            ),
        start,
        end,
    )
}

class BiggerMiniMaxV1ProfileTest :
    FunSpec({

        beforeTest {}

        /**
         * Does this prove anything?
         * I have no idea.
         * Like yeah in some sense yeah but in some sense not
         *
         */
        test("nextMove original") {
            val bc = BoardConfig()
            val minimax =
                MiniMaxV1Profile(
                    depth = 9,
                    timeLimit = TRILLION,
                )
            var gameHolder =
                GameHolder(
                    board = Board(bc, "44444222245355266776662611135533", offset = -1),
                    playerA = minimax,
                    playerB = minimax,
                    bc = bc,
                )

            repeat(11) {
                gameHolder = gameHolder.dropTokenLimited()
            }

            gameHolder.whoisWinner() shouldBe 1

            val history =
                gameHolder.board.history
                    .map { thing -> thing + 1 }
                    .joinToString("")
            history shouldBe "44444222245355266776662611135533353777711"
        }

        context("nextMove data tests") {
            withData(
                GameToPlay(
                    depth = 9,
                    start = "44444222245355266776662611135533",
                    end = "44444222245355266776662611135533353777711",
                ),
                GameToPlay(
                    start = "44444222273746666773171171556112",
                    end = "44444222273746666773171171556112555536233",
                ),
                GameToPlay(
                    start = "4441534236441655572",
                    end = "4441534236441655572535332",
                ),
                GameToPlay(
                    depth = 16,
                    start = "42656466455454627",
                    end = "426564664554546274556222211111133",
                ),
                GameToPlay(
                    depth = 16,
                    start = "4423432337225575224445",
                    end = "44234323372255752244455573377711111166",
                ),
//                GameToPlay(
//                    playerA =
//                        MiniMaxV1Profile(
//                            depth = 40,
//                            timeLimit = TRILLION,
//                        ),
//                    playerB =
//                        MiniMaxV1Profile(
//                            depth = 40,
//                            timeLimit = TRILLION,
//                        ),
//                    start = "4",
//                    end =   "44444666646555621237353353357112222711177",
//                ),
//                GameToPlay(
//                    depth = 20,
//                    start = "444441566664322362253",
//                    end = "44444666646555621237353353357112222711177",
//                ),

//                GameToPlay(
//                    depth = 18,
//                    start = "444441566664322362253151",
//                    end = "44444666646555621237353353357112222711177",
//                ),
                GameToPlay(
                    depth = 16,
                    start = "4444415666643223622531511",
                    end = "44444156666432236225315115525633327771177",
                ),
            ) { (playerA, playerB, start, end) ->
                val bc = BoardConfig()

                var gameHolder =
                    GameHolder(
                        board = Board(bc, start, offset = -1),
                        playerA = playerA,
                        playerB = playerB,
                        bc = bc,
                    )

                repeat(end.length - start.length) {
                    gameHolder = gameHolder.dropTokenLimited()
                }

                gameHolder.whoisWinner() shouldBe if (end.length % 2 == 0) -1 else 1

                val history =
                    gameHolder.board.history
                        .map { thing -> thing + 1 }
                        .joinToString("")
                history shouldBe end
            }
        }
    })
