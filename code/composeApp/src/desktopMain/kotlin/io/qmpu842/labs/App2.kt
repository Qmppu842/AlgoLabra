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

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun App2() {
//    var isAutoPlayActive by remember { mutableStateOf(true) }
//    LaunchedEffect(isAutoPlayActive){
//        delay(3000L)
//        isAutoPlayActive = false
//    }
    MaterialTheme {
        TheGame(Modifier.padding(top = 20.dp, bottom = 50.dp, start =  50.dp, end = 50.dp))
    }
}

var lastTime = System.currentTimeMillis()

@Composable
fun TheGame(modifier: Modifier = Modifier) {
//    val playerA: OpponentProfile = ProfileCreator.miniMaxV3Profile // 3000
//    val playerB: OpponentProfile = ProfileCreator.miniMaxV2Profile // 80
//    val playerA: OpponentProfile = ProfileCreator.miniMaxV3Profile4
//    val playerB: OpponentProfile = ProfileCreator.miniMaxV3Profile4

    val playerA: OpponentProfile = ProfileCreator.rand
    val playerB: OpponentProfile = ProfileCreator.rand

    var playerOnTurn by remember { mutableStateOf(playerA) }

    val forSide = remember { mutableIntStateOf(-1) }
    var isThereWinner by remember { mutableIntStateOf(0) }
    var boardState by remember { mutableStateOf(Board()) }

    val aStats = remember { mutableIntStateOf(playerA.wins) }
    val bStats = remember { mutableIntStateOf(playerB.wins) }

    var isAutoPlayActive by remember { mutableStateOf(false) }

    var isAutoAutoPlayActive by remember { mutableStateOf(true) }

    val dropTokenAction: (Int) -> Unit = { column ->
        if (isThereWinner == 0) {
            boardState = boardState.dropToken(column, boardState.history.size * forSide.value)
//            boardState = boardState.dropLockedToken(column)
            val voittaja = boardState.isLastPlayWinning(4)
            if (voittaja) {
                isThereWinner = forSide.value
                if (forSide.value == -1) {
                    aStats.value += 1
                } else {
                    bStats.value += 1
                }
            }
            forSide.value *= -1
            playerOnTurn = if (playerOnTurn.id == playerA.id) playerB else playerA

            if (playerB.id == ProfileCreator.human.id || playerA.id == ProfileCreator.human.id) {
                isAutoPlayActive = !isAutoPlayActive
                isAutoPlayActive = !isAutoPlayActive
            }
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
        dropTokenAction(playerOnTurn.nextMove(board = boardState.deepCopy(), forSide = forSide.value))
    }

//    println("player on turn: ${playerOnTurn::class.simpleName}")

    LaunchedEffect(isAutoPlayActive) {
        while (isAutoPlayActive && playerOnTurn.id != ProfileCreator.human.id) {
//            delay(100)
//            runBlocking {
            val alku = System.currentTimeMillis()
            playNextFromProfile()
            val loppu = System.currentTimeMillis()
            if (loppu - alku < playerOnTurn.timeLimit) {
                val amount = playerOnTurn.timeLimit - (loppu - alku)
                delay(amount)
            }
//            }

            if (isAutoAutoPlayActive && isThereWinner != 0) {
                val thing = System.currentTimeMillis()
                val timeTook = thing - lastTime
                lastTime = thing
                println("Round took ~$timeTook ms")
                var delay = 500L
                if (playerB.id == ProfileCreator.human.id || playerA.id == ProfileCreator.human.id) {
                    delay = 5000L
                }
//                runBlocking {
                delay(delay)
//                }
                clearBoardAction()
            }
        }
    }

    val heuristicWells =
        SecondHeuristicThing.combinedWells(
            board = boardState,
            forSide = forSide.value,
        )
//    val heuristicWells2  =   heuristicWells
//    val heuristicWells2 =
//        ProfileCreator.miniMaxV1Profile.minimaxAsHearisticWells(
//            board = boardState.deepCopy(),
//            forSide = forSide.value,
//        )

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

//        Row(modifier = Modifier.fillMaxWidth()) {
//            for (well in heuristicWells2) {
//                Button(
//                    onClick = {},
//                    Modifier.width(102.dp),
//                ) {
//                    var texti = "H:$well"
//                    if (well == Int.MAX_VALUE) {
//                        texti = "H:WIN!!"
//                    } else if (well == Int.MIN_VALUE) {
//                        texti = "H:Must block"
//                        if (heuristicWells2.count { it == Int.MIN_VALUE } >= 2) {
//                            texti = "H:☹\uFE0F"
//                        }
//                    }
//                    Text(text = texti)
//                }
//            }
//        }

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
            Button(onClick = { isAutoPlayActive = !isAutoPlayActive }) {
                Text("Activate autoplay from profiles")
            }
        }
        Row {
            Button(onClick = {}, modifier = Modifier.width(308.dp)) {
                Text("Red player wins: ${aStats.value}")
            }
            Button(onClick = {}, modifier = Modifier.width(308.dp)) {
                Text("Yellow player wins: ${bStats.value}")
            }
        }

        Button(onClick = {}) {
            Text(
                text =
                    if (isThereWinner != 0) {
                        "Winner is " + if (isThereWinner == 1) "Player B, The Yellow One!" else "Player A, The Red One!"
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
