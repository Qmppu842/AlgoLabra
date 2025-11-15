package io.qmpu842.labs.logic.heuristics

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.logic.Board

data class BoardToEval(
    val boardAsString: String,
    val evalFunction: HeuristicFun,
//    val heuristicArgs: HeuristicArgs,
    val forSide: Int,
    val expectedPotential: IntRange,
)
// {
//    companion object{
//        operator fun invoke(boardAsString: String){
//
//        }
//    }
// }

class FullBoardEvaluationTest :
    FunSpec({

        test("fullBoardEvaluation empty board potential is 0") {
            val bc = BoardConfig()
            val board =
                Board(
                    boardConfig = bc,
                    historyAsText = "",
                    offset = -1,
                )
            val (lastX, lastY) = board.getLastTokenLocation()
            val potential =
                fullBoardEvaluation(
                    HeuristicArgs(
                        board = board,
                        x = lastX,
                        y = lastY,
                        forSide = 1,
                        neededForWin = 4,
                    ),
                )

            potential shouldBe 0
        }

        context("Full board eval testing with data") {
            withData(
                BoardToEval(
                    boardAsString = "",
                    evalFunction = ::fullBoardEvaluation,
                    forSide = 1,
                    expectedPotential = -1..1,
                ),
                BoardToEval(
                    boardAsString = "",
                    evalFunction = ::fullBoardEvaluation,
                    forSide = 1,
                    expectedPotential = 0..0,
                ),
            ) { (boardAsString, evalFunction, forSide,expectedPotential) ->

                val bc = BoardConfig()
                val board =
                    Board(
                        boardConfig = bc,
                        historyAsText = boardAsString,
                        offset = -1,
                    )
                val (lastX, lastY) = board.getLastTokenLocation()
                val potential =
                    evalFunction(
                        HeuristicArgs(
                            board = board,
                            x = lastX,
                            y = lastY,
                            forSide = forSide,
                            neededForWin = 4,
                        ),
                    )

                potential shouldBeInRange expectedPotential
            }
        }
    })
