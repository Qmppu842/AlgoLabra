@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.qmpu842.labs.helpers.ProfileHolder
import io.qmpu842.labs.logic.GameHolder
import kotlinx.coroutines.delay

fun main1() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Connect 4,  Bots",
            state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
            alwaysOnTop = true,
        ) {

//            Comment this launchedEffect away if you want to play more than 5 minutes
            LaunchedEffect(true) {
                delay(300_000)
                this@application.exitApplication()
            }
            App2()
        }
    }

fun main() {
    GameHolder.runWithOutUi(
        10,
        playerA = ProfileHolder.rand,
        playerB = ProfileHolder.minimaxDepth11TimeInf
    )
}
