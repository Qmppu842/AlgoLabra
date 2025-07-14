package io.qmpu842.labs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.qmpu842.labs.logic.MoveHistory
import onlydesktop.composeapp.generated.resources.Res
import onlydesktop.composeapp.generated.resources.board
import onlydesktop.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@SuppressWarnings("ktlint:compose:modifier-missing-check", "ktlint:standard:function-naming", "unused")
@Composable
fun App2() {
    MaterialTheme {
//        Original()
//        NiceTesting()
        BoardAndButtons()
    }
}

@Composable
private fun Original() {
    var showContent by remember { mutableStateOf(false) }
    Image(
        painter = painterResource(Res.drawable.board),
        contentDescription = "Board",
        modifier = Modifier.fillMaxSize().safeContentPadding(),
        contentScale = ContentScale.Fit,
    )

    Column(
        modifier = Modifier.safeContentPadding().fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { Greeting().greet() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
                val purpleColor = Color(0xFFBA68C8)
                Canvas(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    onDraw = {
                        drawCircle(purpleColor)
                    },
                )
            }
        }
    }
}

@Composable
fun NiceTesting(modifier: Modifier = Modifier) {
    var showContent by remember { mutableStateOf(false) }
    val eka: @Composable () -> Unit = {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!1")
        }
    }

    val toka: @Composable () -> Unit = {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!2")
        }
    }

    val kolmas: @Composable () -> Unit = {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!3")
        }
    }
    val listaa2: List<@Composable () -> Unit> = listOf(eka, toka, kolmas)
    val listaa = listaa2.shuffled()
    Row {
        for (item in listaa) {
            item()
        }
    }
}

@Composable
fun BoardAndButtons() {
    var state by remember { mutableStateOf(MoveHistory()) }
    Column {
        Row {
            repeat(7) { num ->
                val real = num + 1
                Button(onClick = {
                    state = state.limitedAdd(real)
                }) {
                    Text("Drop@#$real")
                }
            }
        }
        Text("Hello, #${state.size()}" + if (state.size() > 0) "and ${state.list.last()}" else "")
        Text("${state.list}")

        Button(onClick = {
            if (state.size() > 0) {
                state = state.undoLast()
            }
        }) {
            Text("Undo last move")
        }
    }
}

@Composable
fun drawTheBoardState(state: State<List<Int>>) {
}
