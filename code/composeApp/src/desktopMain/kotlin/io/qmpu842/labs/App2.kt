package io.qmpu842.labs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.profiles.OpponentProfile
import io.qmpu842.labs.logic.profiles.RandomProfile
import onlydesktop.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

@Composable
fun App2() {
    MaterialTheme {
        TheGame()
    }
}

@Composable
fun TheGame(modifier: Modifier = Modifier) {
    val playerA: OpponentProfile = RandomProfile()
    val playerB: OpponentProfile = RandomProfile()
    var playerOnTurn = playerA

    val doThing = remember { mutableIntStateOf(-1) }
    var boardState by remember { mutableStateOf(Board()) }

    val dropTokenAction: (Int) -> Unit = { column ->
        boardState = boardState.dropToken(column, boardState.history.size * doThing.value)
        doThing.value *= -1
        playerOnTurn = if (playerOnTurn.id == playerA.id) playerB else playerA
    }

    val undoAction: () -> Unit = {
        boardState = boardState.undoLastMove()
    }
    var doThing2 by remember { mutableStateOf(false) }
    val clearBoardAction: () -> Unit = {
        doThing2 = true
    }
    LaunchedEffect(doThing2) {
        if (doThing2) {
            boardState = boardState.clear()
            doThing2 = !doThing2
            dropTokenAction(0)
            dropTokenAction(0)
            boardState.undoLastMove()
            boardState.undoLastMove()
        }
    }

    Column(modifier = modifier) {
        DropButtons(
            dropTokenAction = dropTokenAction,
            boardWidth = boardState.getWells(),
        )
        DrawTheBoard(board = boardState)

        Row {
            Button(onClick = undoAction) {
                Text("Undo last move")
            }
            Button(onClick = clearBoardAction) {
                Text("Restart")
            }
            Button(onClick = { dropTokenAction(playerOnTurn.nextMove(board = boardState)) }) {
                Text("Play next move")
            }
        }
    }
}

@Composable
fun DrawTheBoard(
    board: Board,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        repeat(board.board.size) { rowNum ->
            Column(
                modifier = Modifier,
            ) {
                repeat(board.board[rowNum].size) { columnNum ->
                    val move = board.board[rowNum][columnNum]
                    ChoosePic(move, highest = board.getLastMove())
                }
            }
        }
    }
}

@Composable
fun ChoosePic(
    move: Int,
    modifier: Modifier = Modifier,
    highest: Int = 0,
) {
    var contentDescription: String
    val resource: DrawableResource
    if (move > 0) {
        resource =
            if (abs(move) == highest) {
                Res.drawable.yellow_cell_latest
            } else {
                Res.drawable.yellow_cell
            }
        contentDescription = "Yellow Cell"
    } else if (move < 0) {
        resource =
            if (abs(move) == highest) {
                Res.drawable.red_cell_latest
            } else {
                Res.drawable.red_cell
            }
        contentDescription = "Red Cell"
    } else {
        resource = Res.drawable.empty_cell
        contentDescription = "Empty Cell"
    }

    Image(
        painter = painterResource(resource),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.None,
    )
}

@Composable
fun DropButtons(
    dropTokenAction: (Int) -> Unit,
    modifier: Modifier = Modifier,
    boardWidth: Int = 7,
) {
    Row {
        repeat(boardWidth) { num ->
            Button(onClick = {
                dropTokenAction(num)
            }) {
                Text("Drop@${num + 1}")
            }
        }
    }
}
