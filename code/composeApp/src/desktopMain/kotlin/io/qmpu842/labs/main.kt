@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Connect 4,  Bots",
            state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
            alwaysOnTop = true,
        ) {
            App2()
        }
    }
