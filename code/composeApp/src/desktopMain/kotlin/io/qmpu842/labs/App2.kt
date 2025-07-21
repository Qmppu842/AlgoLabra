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
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.ProfileCreator
import io.qmpu842.labs.helpers.Settings
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.SecondHeuristicThing
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
    MaterialTheme {
        TheGame(Modifier.padding(top = 20.dp, bottom = 50.dp, start = 50.dp, end = 50.dp))
    }
}

@Composable
fun TheGame(modifier: Modifier = Modifier) {
//    val playerA: OpponentProfile = ProfileCreator.miniMaxV3Profile // 3000
//    val playerB: OpponentProfile = ProfileCreator.miniMaxV2Profile // 80
//    val playerA: OpponentProfile = ProfileCreator.miniMaxV3Profile4
//    val playerB: OpponentProfile = ProfileCreator.miniMaxV3Profile4

//    var boardState by remember { mutableStateOf(Board()) }

    var settings by remember { mutableStateOf(Settings()) }

    var gameHolder by remember {
        mutableStateOf(
            GameHolder(
                Board(),
                ProfileCreator.rand,
                ProfileCreator.rand,
                bc = BoardConfig(),
            ),
        )
    }

//    val playerA: OpponentProfile = ProfileCreator.rand
//    val playerB: OpponentProfile = ProfileCreator.rand

//    var playerOnTurn by remember { mutableStateOf(playerA) }

    val forSide = remember { mutableIntStateOf(-1) }
    var isThereWinner by remember { mutableIntStateOf(0) }

    val aStats = remember { mutableIntStateOf(1) }
    val bStats = remember { mutableIntStateOf(1) }

    val dropTokenAction: (Int) -> Unit = { column ->
        gameHolder = gameHolder.dropTokenLimited(column)
    }

    val undoAction: () -> Unit = {
        gameHolder = gameHolder.undo()
    }

    val clearBoardAction: () -> Unit = {
        gameHolder = gameHolder.clearBoard()
    }

    val playNextFromProfile = {
//        dropTokenAction(playerOnTurn.nextMove(board = boardState.deepCopy(), forSide = forSide.value))
        gameHolder = gameHolder.dropTokenLimited()
    }

//    LaunchedEffect(settings.isAutoPlayActive) {
//        while (settings.isAutoPlayActive && playerOnTurn.id != ProfileCreator.human.id) {
// //            delay(100)
// //            runBlocking {
//            val alku = System.currentTimeMillis()
//            playNextFromProfile()
//            val loppu = System.currentTimeMillis()
//            if (loppu - alku < playerOnTurn.timeLimit) {
//                val amount = playerOnTurn.timeLimit - (loppu - alku)
//                delay(amount)
//            }
// //            }
//
//            if (settings.isAutoAutoPlayActive && isThereWinner != 0) {
//                val thing = System.currentTimeMillis()
//                val timeTook = thing - lastTime
//                lastTime = thing
//                println("Round took ~$timeTook ms")
//                var delay = 10L
//                if (playerB.id == ProfileCreator.human.id || playerA.id == ProfileCreator.human.id) {
//                    delay = 5000L
//                }
// //                runBlocking {
//                delay(delay)
// //                }
//                clearBoardAction()
//            }
//        }
//    }
    val thing2 =
        { gameHolder = gameHolder.clearBoard() }
            .SettingAutoAutoPlay(
                gameHolder = gameHolder,
                settings = settings,
                modifier = modifier,
            )

    val thing =
        { gameHolder = gameHolder.dropTokenLimited() }
            .SettingNormalAutoPlay(
                gameHolder = gameHolder,
                settings = settings,
        )
    Column(modifier = modifier.width(IntrinsicSize.Max)) {
        DropButtons(
            dropTokenAction = dropTokenAction,
            boardWidth = gameHolder.bc.width,
        )
        DrawTheBoard(board = gameHolder.board)

        HeuristicWells(
            board = gameHolder.board,
            forSide = forSide.value,
            wellFunction = SecondHeuristicThing::combinedWells,
        )

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
            Button(onClick = { settings = settings.toggleAutoPlay() }) {
                Text("Activate autoplay from profiles")
            }
//            SettingsThings(
//                gameHolder = gameHolder,
//                settings = settings,
//                ghDropToken = { gameHolder = gameHolder.dropTokenLimited() },
// //                ghClear = { gameHolder = gameHolder.clearBoard() },
//                settingsToggleAuto = { settings = settings.toggleAutoPlay() },
//            )
        }
        Row {
            Button(onClick = {}, modifier = Modifier.width(205.dp)) {
                Text("Red player wins: ${aStats.value}")
            }
            Button(onClick = {}, modifier = Modifier.width(205.dp)) {
                Text("Draws: ${bStats.value}")
            }
            Button(onClick = {}, modifier = Modifier.width(205.dp)) {
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

@Composable
fun HeuristicWells(
    board: Board,
    forSide: Int,
    wellFunction: (Board, Int) -> IntArray,
    modifier: Modifier = Modifier,
) {
    val heuristicWells = wellFunction(board, forSide)
    Row(modifier = modifier.fillMaxWidth()) {
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
}


var lastTime = System.currentTimeMillis()

@Composable
fun (() -> Unit).SettingAutoAutoPlay(
    gameHolder: GameHolder,
    settings: Settings,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(settings.isAutoAutoPlayActive && gameHolder.hasGameStopped()) {
        val thing = System.currentTimeMillis()
        val timeTook = thing - lastTime
        lastTime = thing
        println("Round took ~$timeTook ms")
        var delay = 400L
        if (gameHolder.playerB.id == ProfileCreator.human.id || gameHolder.playerA.id == ProfileCreator.human.id) {
            delay = 5000L
        }
        delay(delay)
        this@SettingAutoAutoPlay()
    }
}

@Composable
fun (() -> Unit).SettingNormalAutoPlay(
    gameHolder: GameHolder,
    settings: Settings,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileCreator.human.id) {
        while (settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileCreator.human.id) {
            val alku = System.currentTimeMillis()
            this@SettingNormalAutoPlay()
            val loppu = System.currentTimeMillis()
            if (loppu - alku < gameHolder.playerOnTurn().timeLimit) {
                val amount = gameHolder.playerOnTurn().timeLimit - (loppu - alku)
                delay(amount)
            }
        }
    }
}