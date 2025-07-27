package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.logic.profiles.RandomProfile

class GameHolderTest :
    FunSpec({

        beforeTest {
        }

        test("playerOnTurn on empty game") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc = BoardConfig(),
                )
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerA
        }

        test("playerOnTurn after first turn") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc = BoardConfig(),
                )
            gameHolder = gameHolder.dropTokenLimited()
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerB
        }

        test("playerOnTurn after 32 ply") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc =
                        BoardConfig(
                            width = 1,
                            height = 100,
                            neededForWin = 4,
                        ),
                )
            repeat(32) {
                gameHolder = gameHolder.dropTokenLimited()
            }
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerA
        }

        test("undo on empty gameHolder") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc = BoardConfig(),
                )
            gameHolder = gameHolder.undo()
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerA
        }

        test("undo 9 times after 32 ply") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc =
                        BoardConfig(
                            width = 1,
                            height = 40,
                            neededForWin = 100,
                        ),
                )
            repeat(32) {
                gameHolder = gameHolder.dropTokenLimited()
            }
            repeat(9) {
                gameHolder = gameHolder.undo()
            }
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerB
            gameHolder.board.history.size shouldBe 23
            gameHolder.board.getLastMove() shouldBe 0
            gameHolder.board.getHighestSpaceIndex(0) shouldBe 16
        }

        test("clearBoard after 32 ply") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc =
                        BoardConfig(
                            width = 1,
                            height = 40,
                            neededForWin = 100,
                        ),
                )
            repeat(33) {
                gameHolder = gameHolder.dropTokenLimited()
            }
            gameHolder = gameHolder.clearBoard()
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerA
            gameHolder.board.history.size shouldBe 0
            gameHolder.board.getLastMove() shouldBe null
            gameHolder.board.getHighestSpaceIndex(0) shouldBe 39
        }

        test("hasGameStopped should be false on 33 first turns on board with 40 spaces") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc =
                        BoardConfig(
                            width = 1,
                            height = 40,
                            neededForWin = 100,
                        ),
                )
            repeat(33) {
                gameHolder = gameHolder.dropTokenLimited()
                gameHolder.hasGameStopped() shouldBe false
            }
        }

        test("hasGameStopped should be true after 40 turns on board with 40 spaces") {
            val playerA = RandomProfile()
            val playerB = RandomProfile()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc =
                        BoardConfig(
                            width = 1,
                            height = 40,
                            neededForWin = 100,
                        ),
                )
            repeat(40) {
                gameHolder = gameHolder.dropTokenLimited()
            }
            gameHolder.hasGameStopped() shouldBe true
            gameHolder.playerOnTurn() shouldBeSameInstanceAs playerA
        }

        test("whoisWinner") { }

        test("whoisWinnerText") { }

        test("dropTokenLimited") { }

        test("updateWinners") { }

        test("clearBoardAndUpdateWinners") { }

        test("board") { }

        test("playerA") { }

        test("playerB") { }

        test("bc") { }

        test("GameHolder") { }
    })
