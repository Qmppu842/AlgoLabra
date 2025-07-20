package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class SecondHeuristicThingTest : FunSpec({

//    beforeTest {
//    }

//    test("getMovesWith3StraightAnd2AirSpace") { }

    test("getMovesWith3Straight flat") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, -2, 1),
                        intArrayOf(0, 0, 0, 0, -4, 3),
                        intArrayOf(0, 0, 0, 0, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, 1)
        heur[1] shouldBe 1
        heur[5] shouldBe 1

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, -1)
        heur2[1] shouldBe -1
        heur2[5] shouldBe -1
    }

    test("getMovesWith3Straight up") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 5, 3, 1),
                        intArrayOf(0, 0, 0, -6, -4, -2),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, 1, -1)
        heur[3] shouldBe 1
        heur[4] shouldBe -1

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, 1, -1)
        heur2[3] shouldBe -1
        heur2[4] shouldBe 1
    }

    test("getMovesWith3Straight diagonal down") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 3, -2),
                        intArrayOf(0, 0, 0, 7, -4, 1),
                        intArrayOf(0, 0, 9, -8, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, -10),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[1] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[1] shouldBe Int.MIN_VALUE
    }

    test("getMovesWith3Straight diagonal up") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 9),
                        intArrayOf(0, 0, 0, 0, 3, -2),
                        intArrayOf(0, 0, 0, 7, -4, 1),
                        intArrayOf(0, 0, 0, -8, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, -10),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[4] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[4] shouldBe Int.MIN_VALUE
    }
// -------------------------------------
    test("getMovesWith3TokensWithAirGap flat") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, -2, 1),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, -4, 3),
                        intArrayOf(0, 0, 0, 0, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[2] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[2] shouldBe Int.MIN_VALUE
    }

    test("getMovesWith3TokensWithAirGap up") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 5, -4, 3, 1),
                        intArrayOf(0, 0, 0, 0, -6, -2),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0)
        heur[3] shouldBe 0
        heur[4] shouldBe 0

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0)
        heur2[3] shouldBe 0
        heur2[4] shouldBe 0
    }

    test("getMovesWith3TokensWithAirGap diagonal down") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 3),
                        intArrayOf(0, 0, 0, 0, 0, -2),
                        intArrayOf(0, 0, 0, 7, -4, 1),
                        intArrayOf(0, 0, 9, -8, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, -10),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[2] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[2] shouldBe Int.MIN_VALUE
    }

    test("getMovesWith3TokensWithAirGap diagonal up") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 3),
                        intArrayOf(0, 0, 0, 0, 7, -2),
                        intArrayOf(0, 0, 0, 0, -4, 1),
                        intArrayOf(0, 0, 9, -8, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, -10),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[3] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[3] shouldBe Int.MIN_VALUE
    }

// -------------------------------------
    test("getMovesWith2TokensAndAirSpaceAround") { }

    test("getMovesWith2TokensAndAirSpace") {
        var board =
            Board(
                board =
                    arrayOf(
                        intArrayOf(0, 0, 0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0, 0, 3),
                        intArrayOf(0, 0, 0, 0, 7, -2),
                        intArrayOf(0, 0, 0, 0, -4, 1),
                        intArrayOf(0, 0, 9, -8, -6, 5),
                        intArrayOf(0, 0, 0, 0, 0, -10),
                        intArrayOf(0, 0, 0, 0, 0, 0),
                    ),
            )
        val heur = SecondHeuristicThing.getMovesWith3Straight(board, 1)
        heur.toList() shouldContainAll listOf(0, Int.MAX_VALUE)
        heur[3] shouldBe Int.MAX_VALUE

        val heur2 = SecondHeuristicThing.getMovesWith3Straight(board, -1)
        heur2.toList() shouldContainAll listOf(0, Int.MIN_VALUE)
        heur2[3] shouldBe Int.MIN_VALUE
    }

    test("getOpenness") {
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
    }

    test("theChecker") { }
})
