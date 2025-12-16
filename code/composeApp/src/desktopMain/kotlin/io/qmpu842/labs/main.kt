@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.qmpu842.labs.helpers.ProfileHolder
import io.qmpu842.labs.helpers.TournamentEngine
import io.qmpu842.labs.helpers.Zo
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.heuristics.fullBoardEvaluation
import io.qmpu842.labs.logic.heuristics.lastMovesValueV5
import io.qmpu842.labs.logic.heuristics.zeroHeuristics
import io.qmpu842.labs.logic.profiles.MiniMaxV3Profile

/**
 * The normal main when you want ot see and/or play against something.
 */
fun main1() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Connect 4,  Bots",
            state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
//            alwaysOnTop = true,
        ) {
//            Comment this launchedEffect away if you want to play more than 5 minutes
//            LaunchedEffect(true) {
//                delay(300_000)
//                this@application.exitApplication()
//            }
            App2()
        }
    }

/**
 * The main2 to run without ui 1v1
 */
fun main2() {
    GameHolder.runWithOutUiSplit(
        1,
        playerA =
            MiniMaxV3Profile(
                depth = -1,
                heuristic = ::fullBoardEvaluation,
                timeLimit = 50_000,
            ),
        playerB = ProfileHolder.rand,
//        playerB =
//            MiniMaxV3Profile(
//                depth = 2,
//                heuristic = ::fullBoardEvaluation,
//            ),
    )
}

/**
 * The main4 for using the tournament system without ui
 * Every profile will play against every other profile
 */
fun main4() {
    val competitors =
        MiniMaxV3Profile(
            depths = listOf(2),
            heuristicFunList = listOf(::zeroHeuristics, ::lastMovesValueV5, ::fullBoardEvaluation),
            timeLimits = listOf(100, 500, 1000),
        )
    TournamentEngine.startTheTournament(
        competitors,
        amountOfGames = 5,
    )
}

/**
 * The main6 for using the tournament system without ui
 * Every profile will play against every other profile
 */
fun main() {
//    val depths = listOf(10)
//    val heurs = listOf(::zeroHeuristics, ::lastMovesValueV5, ::fullBoardEvaluation)
    val heurs = listOf(::fullBoardEvaluation, ::zeroHeuristics)
//    val times: List<Long> = listOf(500, 1000)

    val competitors =
        MiniMaxV3Profile(
            depths = listOf(-1),
            heuristicFunList = heurs,
            timeLimits = listOf(1000),
//            timeLimits = listOf(500, 1000,10_000),
        )

    val oldGuard =
        MiniMaxV3Profile(
//            depths = listOf(2),
            depths = listOf(2, 4, 10),
            heuristicFunList = heurs,
        )
    TournamentEngine.startTheTournament(
        competitors + oldGuard + ProfileHolder.rand,
        amountOfGames = 5,
    )
}

/**
 * The main3 to list different types of possible lines in board
 */
fun main3() {
//    counter3()
    counterSensible()
}

fun main5() {
    val zoo = Zo(3)

    println("zoo: $zoo")
    println("the hash: ${zoo.hashCode()}")
}
