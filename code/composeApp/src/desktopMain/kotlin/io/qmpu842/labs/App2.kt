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
import onlydesktop.composeapp.generated.resources.*
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
fun BoardAndButtons(
    boardHeight: Int = 6,
    boardWidth: Int = 7,
) {
    val state =
        remember { mutableStateOf(MoveHistory(list = listOf(), boardHeight = boardHeight, boardWidth = boardWidth)) }

    val dropTokenAction: (Int) -> Unit = {
        state.value = state.value.limitedAdd(it, state.value.boardHeight)
    }

    val undoAction = {
        if (state.value.size() > 0) {
            state.value = state.value.undoLast()
        }
    }

    Column {
        DropButtons(dropTokenAction)

        DrawTheBoardState(state.value)

        Text("Hello, #${state.value.size()}" + if (state.value.size() > 0) " and ${state.value.list.last()}" else "")
        Text("${state.value.list}")

        Button(onClick = undoAction) {
            Text("Undo last move")
        }
    }
}

@Composable
fun DrawTheBoardState(state: MoveHistory) {
    val board4 = state.getBoardPaddedWithZeros()

    Row {
        repeat(state.boardWidth) { y ->
            Column {
                repeat(state.boardHeight) { x ->
                    val target = board4[y][x]

                    val resource =
                        if (target > 0) {
                            Res.drawable.yellow_cell
                        } else if (target < 0) {
                            Res.drawable.red_cell
                        } else {
                            Res.drawable.empty_cell
                        }

                    Image(
                        painter = painterResource(resource),
                        contentDescription = "Cell $resource",
                    )
                }
            }
        }
    }
}

@Composable
fun DropButtons(
    dropTokenAction: (Int) -> Unit,
    boardWidth: Int = 7,
) {
    Row {
        repeat(boardWidth) { num ->
            val real = num + 1
            Button(onClick = {
                dropTokenAction(real)
            }) {
                Text("Drop@$real")
            }
        }
    }
}
