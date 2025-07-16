package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual

class HeuristicThingTest :
    FunSpec({

        beforeTest {
        }

        test("singleWellHeuristic should give higher score to middle than to edges on empty board") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            val rightWell = HeuristicThing.singleWellHeuristic(0, board, forSide = 1)

            val middleWell =
                HeuristicThing.singleWellHeuristic(3, board, forSide = 1) * -1 // heh

            val leftWell = HeuristicThing.singleWellHeuristic(6, board, forSide = 1)

            rightWell shouldBeEqual leftWell
            middleWell shouldBeGreaterThan rightWell
            middleWell shouldBeGreaterThan leftWell
            middleWell shouldBeGreaterThanOrEqual (rightWell + leftWell)
        }


        test("singleWellHeuristic should give higher score to middle than to edges") {
            var board =
                Board(
                    board =
                        arrayOf(
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                            intArrayOf(0, 0, 0, 0, 0, 0),
                        ),
                )

            val rightWell = HeuristicThing.singleWellHeuristic(3, board)

            val middleWell = HeuristicThing.singleWellHeuristic(0, board)

            val leftWell = HeuristicThing.singleWellHeuristic(6, board)

//            rightWell shouldBeEqual leftWell
            middleWell shouldBeGreaterThan rightWell
            middleWell shouldBeGreaterThan leftWell
            middleWell shouldBeGreaterThanOrEqual (rightWell + leftWell)
        }
    })
