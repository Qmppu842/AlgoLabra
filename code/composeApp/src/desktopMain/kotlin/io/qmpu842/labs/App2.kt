package io.qmpu842.labs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.qmpu842.labs.helpers.ProfileCreator
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.SecondHeuristicThing
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlinx.coroutines.delay
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
//    val human = HumanProfile()
//    val playerA: OpponentProfile =  DFSProfile(-1)
//    val playerB: OpponentProfile = DFSProfile(1)
    val playerA: OpponentProfile = ProfileCreator.dfsProfileA
    val playerB: OpponentProfile = ProfileCreator.dfsProfileB
    var playerOnTurn by remember { mutableStateOf(playerA) }

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
        boardState = boardState.clear()
        playerOnTurn = playerA
        forSide.value = -1
        isThereWinner = 0
    }
    val playNextFromProfile = {
//        println("Next turn from profile?")
        dropTokenAction(playerOnTurn.nextMove(board = boardState.deepCopy(), forSide = forSide.value))
    }

    var isAutoPlayActive by remember { mutableStateOf(true) }

    println("player on turn: ${playerOnTurn::class.simpleName}")

    LaunchedEffect(isAutoPlayActive){
//        while (isAutoPlayActive && (playerOnTurn !is HumanProfile || playerOnTurn.id != human.id)){
//        println("aaaaaa")

//        if ( playerOnTurn.id != human.id){
//            println("Thing2?")
//            playNextFromProfile()
//        }
        while (isAutoPlayActive && playerOnTurn.id != ProfileCreator.human.id) {
//            if (playerOnTurn.id != ProfileCreator.human.id) {
                delay(80)
//                println("Thing?")
                playNextFromProfile()
//                isAutoPlayActive = false
//            }
        }
    }

    val heuristicWells =
        SecondHeuristicThing.combinedWells(
            board = boardState,
            forSide = forSide.value,
        )


    Column(modifier = modifier.width(IntrinsicSize.Max)) {
        DropButtons(
            dropTokenAction = dropTokenAction,
            boardWidth = boardState.getWells(),
        )
        DrawTheBoard(board = boardState)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (well in heuristicWells) {
                Button(
                    onClick = {},
                    Modifier.width(102.dp),
                ) {
                    var texti = "H:$well"
                    if (well == Int.MAX_VALUE) {
                        texti = "H:WIN!!"
                    } else if (well == Int.MIN_VALUE) {
                        texti = "H:Must block"
                        if (heuristicWells.count { it == Int.MIN_VALUE } >= 2) {
                            texti = "H:☹\uFE0F"
                        }
                    }
                    Text(text = texti)
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
            Button(onClick = playNextFromProfile) {
                Text("Play next move")
            }
            Button(onClick = {isAutoPlayActive = !isAutoPlayActive}){
                Text("Activate autoplay from profiles")
            }
        }

        Button(onClick = {}) {
            Text(
                text =
                    if (isThereWinner != 0) {
                        "Winner is " +  if (isThereWinner == 1) "Player B, The Yellow One!" else "Player A, The Red One!"
                    } else {
                        "No winner, yet..."
                    },
            )
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
            }, modifier = Modifier.width(102.dp)) {
                Text("Drop@${num + 1}")
            }
        }
    }
}
