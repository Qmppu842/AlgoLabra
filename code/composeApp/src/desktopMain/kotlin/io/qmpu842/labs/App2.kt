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
import io.qmpu842.labs.logic.HeuristicThing
import io.qmpu842.labs.logic.profiles.OpponentProfile
import io.qmpu842.labs.logic.profiles.SimpleHeuristicGuyProfile
import onlydesktop.composeapp.generated.resources.Res
import onlydesktop.composeapp.generated.resources.empty_cell
import onlydesktop.composeapp.generated.resources.red_cell
import onlydesktop.composeapp.generated.resources.yellow_cell
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun App2() {
    MaterialTheme {
        TheGame()
    }
}

@Composable
fun TheGame(modifier: Modifier = Modifier) {
    val playerA: OpponentProfile = SimpleHeuristicGuyProfile()
    val playerB: OpponentProfile = SimpleHeuristicGuyProfile()
    var playerOnTurn = playerA

    val forSide = remember { mutableIntStateOf(-1) }
    var isThereWinner by remember { mutableIntStateOf(0) }
    var boardState by remember { mutableStateOf(Board()) }

    val dropTokenAction: (Int) -> Unit = { column ->
        if (isThereWinner == 0) {
            boardState = boardState.dropToken(column, boardState.history.size * forSide.value)
            val voittaja = boardState.isLastPlayWinning(4)
            if (voittaja) {
                isThereWinner = forSide.value
            }
            forSide.value *= -1
            playerOnTurn = if (playerOnTurn.id == playerA.id) playerB else playerA
        }
    }

    val undoAction: () -> Unit = {
        boardState = boardState.undoLastMove()
    }

    val clearBoardAction: () -> Unit = {
        // This is work around to refresh the screen cuz compose magic...
        dropTokenAction(0)
        dropTokenAction(0)
        boardState = boardState.undoLastMove()
        boardState = boardState.undoLastMove()

        boardState = boardState.clear()
        playerOnTurn = playerA
        forSide.value = -1
        isThereWinner = 0
    }

    val heuristicWells = HeuristicThing.allTheWells(boardState, forSide = forSide.value, maxDepth = 5)

    Column(modifier = modifier) {
        DropButtons(
            dropTokenAction = dropTokenAction,
            boardWidth = boardState.getWells(),
        )
        DrawTheBoard(board = boardState)
        Row {
            for (well in heuristicWells) {
                Button(onClick = {}) {
                    Text("H:$well")
                }
            }
        }

        Row {
            Button(onClick = undoAction) {
                Text("Undo last move")
            }
            Button(onClick = clearBoardAction) {
                Text("Restart")
            }
            Button(onClick = { dropTokenAction(playerOnTurn.nextMove(board = boardState, forSide = forSide.value)) }) {
                Text("Play next move")
            }
            Button(onClick = {}) {
                Text(
                    text =
                        if (isThereWinner != 0) {
                            val voittaja =
                                if (isThereWinner == 1) "Player B, The Yellow One!" else "Player A, The Red One!"
                            "Winner is $voittaja"
                        } else {
                            "no winner, yet..."
                        },
                )
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
                    ChoosePic(move)
                }
            }
        }
    }
}

@Composable
fun ChoosePic(
    move: Int,
    modifier: Modifier = Modifier,
) {
    var contentDescription: String
    val resource: DrawableResource
    if (move > 0) {
        resource = Res.drawable.yellow_cell
        contentDescription = "Yellow Cell"
    } else if (move < 0) {
        resource = Res.drawable.red_cell
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
