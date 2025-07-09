package io.qmpu842.labs

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "onlydesktop",
    ) {
        App()
    }
}