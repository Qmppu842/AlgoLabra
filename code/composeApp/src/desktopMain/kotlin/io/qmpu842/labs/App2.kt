package io.qmpu842.labs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.ProfileHolder
import io.qmpu842.labs.helpers.Settings
import io.qmpu842.labs.logic.Board
import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.SecondHeuristicThing
import io.qmpu842.labs.logic.heuristics.lastMovesValueV5
import io.qmpu842.labs.logic.heuristics.zeroHeuristics
import io.qmpu842.labs.logic.profiles.MiniMaxV3Profile
import kotlinx.coroutines.delay
import onlydesktop.composeapp.generated.resources.Res
import onlydesktop.composeapp.generated.resources.empty_cell
import onlydesktop.composeapp.generated.resources.red_cell
import onlydesktop.composeapp.generated.resources.yellow_cell
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min
import kotlin.math.sign

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun App2() {
    println("Game on!")
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
//                ProfileHolder.rand,
                MiniMaxV3Profile(depth = 4, heuristic = ::zeroHeuristics),
//                ProfileHolder.minimaxDepth12TimeInf,
                MiniMaxV3Profile(depth = 4, heuristic = ::lastMovesValueV5),
//                MiniMaxV3Profile(depth = 4, timeLimit = 2000000),
                bc =
                    BoardConfig(
                        width = 7,
                        height = 6,
                        neededForWin = 4,
                    ),
            ),
        )
    }

    // All the methods wrapped into holders
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
                gameHolder = gameHolder.updateWinnersAndClearBoard()
            }
        }.SettingAutoAutoPlay(
            gameHolder = gameHolder,
            settings = settings,
        )

    // ... probably
    val thing =
        {
            if (gameHolder.playerOnTurn().id != ProfileHolder.human.id && !gameHolder.hasGameStopped()) {
                gameHolder = gameHolder.dropTokenLimited()
            }
        }.SettingNormalAutoPlay(
            gameHolder = gameHolder,
            settings = settings,
        )

    // The Actual ui drawing things
    Column(modifier = modifier.width(IntrinsicSize.Max)) {
        DropButtons(
            dropTokenAction = dropTokenAction,
            boardWidth = gameHolder.bc.width,
        )
        DrawTheBoard(
            board = gameHolder.board,
            dropTokenAction = dropTokenAction,
            settings = settings,
        )

        HeuristicWells(
            board = gameHolder.board,
            forSide = gameHolder.board.getOnTurnToken().sign,
            wellFunction = SecondHeuristicThing::combinedWells,
            dropTokenAction = dropTokenAction,
            settings = settings,
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
            Text("Red ${gameHolder.playerA.name} player wins: ${gameHolder.playerA.firstPlayStats.wins}")
        }
        Button(onClick = {}, modifier = Modifier.width(205.dp)) {
            Text("Draws: ${gameHolder.playerA.firstPlayStats.draws}")
        }
        Button(onClick = {}, modifier = Modifier.width(205.dp)) {
            Text("Yellow ${gameHolder.playerB.name} player wins: ${gameHolder.playerB.secondPlayStats.wins}")
        }
    }
}

@Composable
fun ControlPanel(
    undoAction: () -> Unit,
    clearBoardAction: () -> Unit,
    playNextFromProfile: () -> Unit,
    toggleAutoPlay: () -> Unit,
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
        Button(onClick = toggleAutoPlay) {
            Text("Activate autoplay from profiles")
        }
    }
}

/**
 * Displays the winner text.
 * Or text to indicate still going game
 */
@Composable
private fun WinnerDisplay(gameHolder: GameHolder) {
    val winner = gameHolder.whoisWinner()
    val result =
        if (winner == null) {
            "No winner, yet..."
        } else if (winner == 0) {
            "It is a draw, now draw your own tie"
        } else if (winner.sign == 1) {
            "Winner is Player A, The Red One! The ${gameHolder.playerA.name}"
        } else {
            "Winner is Player B, The Yellow One! The ${gameHolder.playerB.name}"
        }
    Button(onClick = {}) {
        Text(
            text = result,
        )
    }
}

/**
 * This allows us to draw the board.
 *
 *
 * Also the
 * @param dropTokenAction allows us to click columns in order to drop tokens
 *  Simple yet nice QoL feature.
 */
@Composable
fun DrawTheBoard(
    board: Board,
    settings: Settings,
    modifier: Modifier = Modifier,
    dropTokenAction: (Int) -> Unit,
) {
    Row(modifier = modifier) {
        repeat(board.board.size) { rowNum ->
            Column(
                modifier =
                    Modifier.clickable(
                        enabled = settings.isColumnClickingEnabled,
                        onClickLabel = "Drop it like it's hot",
                        onClick = { dropTokenAction(rowNum) },
                    ),
            ) {
                repeat(board.board[rowNum].size) { columnNum ->
                    val move = board.board[rowNum][columnNum]
                    ChoosePic(move)
                }
            }
        }
    }
}

/**
 * This chooses the pictures for each cell based on its value.
 */
@Composable
fun ChoosePic(
    move: Int,
    modifier: Modifier = Modifier,
) {
    var contentDescription: String
    val resource: DrawableResource
    if (move < 0) {
        resource = Res.drawable.yellow_cell
        contentDescription = "Yellow Cell"
    } else if (move > 0) {
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

/**
 * Draws the basic drop buttons.
 */
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

/**
 * This draws the displays for the heuristic wells.
 *
 * I should not but wanted so
 * @param dropTokenAction allows these to work as drop buttons too.
 *
 *
 * @param wellFunction is the heuristic thing to show here.
 * It should be method that takes board and for side to evaluate, and returns array of the heuristic values of each index
 */
@Composable
fun HeuristicWells(
    board: Board,
    forSide: Int,
    settings: Settings,
    wellFunction: (Board, Int) -> IntArray,
    dropTokenAction: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val heuristicWells = wellFunction(board, forSide)
    Row(modifier = modifier.fillMaxWidth()) {
        for ((index, well) in heuristicWells.withIndex()) {
            Button(
                onClick = { dropTokenAction(index) },
                enabled = settings.isWellClickingEnabled,
                modifier = Modifier.width(102.dp),
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

/**
 * This controls if games start automatically when game has ended
 */
@Composable
fun (() -> Unit).SettingAutoAutoPlay(
    gameHolder: GameHolder,
    settings: Settings,
) {
    LaunchedEffect(settings.isAutoAutoPlayActive && gameHolder.hasGameStopped()) {
        var delay = 10L
        if (gameHolder.playerB.id == ProfileHolder.human.id || gameHolder.playerA.id == ProfileHolder.human.id) {
            delay = 10000L
        }
        delay(delay)
        this@SettingAutoAutoPlay()
    }
}

/**
 * This controls if the profiles should automatically play their next move
 */
@Composable
fun (() -> Unit).SettingNormalAutoPlay(
    gameHolder: GameHolder,
    settings: Settings,
) {
    LaunchedEffect(settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileHolder.human.id && !gameHolder.hasGameStopped()) {
        while (settings.isAutoPlayActive && gameHolder.playerOnTurn().id != ProfileHolder.human.id && !gameHolder.hasGameStopped()) {
            val start = System.currentTimeMillis()
            this@SettingNormalAutoPlay()
            val end = System.currentTimeMillis()
            if (end - start < gameHolder.playerOnTurn().timeLimit) {
                val amount = min(gameHolder.playerOnTurn().timeLimit - (end - start), 10)
                delay(amount)
            }
        }
    }
}
