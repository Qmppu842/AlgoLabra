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
import kotlin.math.sign

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun App2() {
    MaterialTheme {
        TheGame(Modifier.padding(top = 20.dp, bottom = 50.dp, start = 50.dp, end = 50.dp))
    }
}

@Composable
fun TheGame(modifier: Modifier = Modifier) {
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
        gameHolder = gameHolder.dropTokenLimited()
    }

    // These two are really evil
    val thing2 =
        {
            if (gameHolder.hasGameStopped()) {
                gameHolder = gameHolder.clearBoardAndUpdateWinners()
            }
        }.SettingAutoAutoPlay(
            gameHolder = gameHolder,
            settings = settings,
        )

    // ... probably
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
            forSide = gameHolder.board.getOnTurnToken().sign,
            wellFunction = SecondHeuristicThing::combinedWells,
        )

        ControlPanel(
            undoAction,
            clearBoardAction,
            playNextFromProfile,
            { settings = settings.toggleAutoPlay().toggleAutoAutoPlay() },
        )
        PlayStatsDisplay(gameHolder)

        WinnerDisplay(gameHolder)
    }
}

@Composable
fun PlayStatsDisplay(
    gameHolder: GameHolder,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Button(onClick = {}, modifier = Modifier.width(205.dp)) {
            Text("Red player wins: ${gameHolder.playerA.firstPlayStats.wins}")
        }
        Button(onClick = {}, modifier = Modifier.width(205.dp)) {
            Text("Draws: ${gameHolder.playerA.firstPlayStats.draws}")
        }
        Button(onClick = {}, modifier = Modifier.width(205.dp)) {
            Text("Yellow player wins: ${gameHolder.playerB.secondPlayStats.wins}")
        }
    }
}

@Composable
fun ControlPanel(
    undoAction: () -> Unit,
    clearBoardAction: () -> Unit,
    playNextFromProfile: () -> Unit,
    settings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Button(onClick = undoAction) {
            Text("Undo last move")
        }
        Button(onClick = clearBoardAction) {
            Text("Restart")
        }
        Button(onClick = playNextFromProfile) {
            Text("Play next move")
        }
        Button(onClick = settings) {
            Text("Activate autoplay from profiles")
        }
    }
}

@Composable
private fun WinnerDisplay(gameHolder: GameHolder) {
    val winner = gameHolder.whoisWinner()
    val result =
        if (winner == null && gameHolder.hasGameStopped()) {
            "It is a draw, now draw your own tie"
        } else if (gameHolder.hasGameStopped()) {
            "Winner is " + gameHolder.whoisWinnerText()
        } else {
            "No winner, yet..."
        }
    Button(onClick = {}) {
        Text(
            text = result,
        )
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
    Row(modifier) {
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


@Composable
fun (() -> Unit).SettingAutoAutoPlay(
    gameHolder: GameHolder,
    settings: Settings,
) {
    LaunchedEffect(settings.isAutoAutoPlayActive && gameHolder.hasGameStopped()) {
        var delay = 10L
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
) {
    LaunchedEffect(settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileCreator.human.id) {
        while (settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileCreator.human.id) {
            val start = System.currentTimeMillis()
            this@SettingNormalAutoPlay()
            val end = System.currentTimeMillis()
            if (end - start < gameHolder.playerOnTurn().timeLimit) {
                val amount = gameHolder.playerOnTurn().timeLimit - (end - start)
                delay(amount)
            }
        }
    }
}