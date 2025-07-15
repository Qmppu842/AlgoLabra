@file:Suppress("ktlint:standard:filename")

package io.qmpu842.labs

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Connect 4,  Bots",
        ) {
            App2()
        }
    }
