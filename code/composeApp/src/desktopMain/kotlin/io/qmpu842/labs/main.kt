@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.qmpu842.labs.helpers.TournamentEngine
import io.qmpu842.labs.helpers.Zo
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.MiniMaxV1Profile
import io.qmpu842.labs.logic.profiles.minimaxSidesteps.MiniMaxV1NoHeuristicProfile
import io.qmpu842.labs.logic.profiles.minimaxSidesteps.MiniMaxV1OldProfile
import kotlinx.coroutines.delay

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
            LaunchedEffect(true) {
                delay(300_000)
                this@application.exitApplication()
            }
            App2()
        }
    }

/**
 * The main to run without ui 1v1
 */
fun main2() {
    GameHolder.runWithOutUiSplit(
        100,
        playerA =
            MiniMaxV1NoHeuristicProfile(
                depth = 6,
            ),
        //        playerB = MiniMaxV1OldProfile(depth = 2),
//        playerB = ProfileHolder.rand
        playerB =
            MiniMaxV1NoHeuristicProfile(
                depth = 2,
            ),
    )
}

/**
 * The main for using the tournament system without ui
 */
fun main4() {
    val competitors =
        listOf(
//            RandomProfile(),

//            MiniMaxV1NoHeuristicProfile(depth = 2),
//            MiniMaxV1NoHeuristicProfile(depth = 6),
//            MiniMaxV1NoHeuristicProfile(depth = 7),
//            MiniMaxV1NoHeuristicProfile(depth = 8),
//            MiniMaxV1NoHeuristicProfile(depth = 9),
//            MiniMaxV1NoHeuristicProfile(depth = 10),
            MiniMaxV1NoHeuristicProfile(depth = 11),
            MiniMaxV1NoHeuristicProfile(depth = 12),

//            MiniMaxV1OldProfile(depth = 2),
//            MiniMaxV1OldProfile(depth = 6),
//            MiniMaxV1OldProfile(depth = 7),
//            MiniMaxV1OldProfile(depth = 8),
//            MiniMaxV1OldProfile(depth = 9),
            MiniMaxV1OldProfile(depth = 10),
            MiniMaxV1OldProfile(depth = 11),
            MiniMaxV1OldProfile(depth = 12),

//            MiniMaxV1Profile(depth = 2),
//            MiniMaxV1Profile(depth = 6),
//            MiniMaxV1Profile(depth = 7),
//            MiniMaxV1Profile(depth = 8),
//            MiniMaxV1Profile(depth = 9),
//            MiniMaxV1Profile(depth = 10),
            MiniMaxV1Profile(depth = 11),
            MiniMaxV1Profile(depth = 12),

//            MiniMaxV1NoHeuristicProfile(depth = 42, timeLimit = 1000),
//            MiniMaxV1OldProfile(depth = 42, timeLimit = 1000),
//            MiniMaxV1Profile(depth = 42, timeLimit = 1000),
        )
    TournamentEngine.startTheTournament(
        competitors,
        amountOfGames = 5,
    )
}

/**
 * The main to list different types of possible lines in board
 */
fun main3() {
    counter3()
}

fun main(){
    val zoo = Zo(3)

    println("zoo: $zoo")
    println("the hash: ${zoo.hashCode()}")
}
