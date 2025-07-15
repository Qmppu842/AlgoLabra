package io.qmpu842.labs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.qmpu842.labs.logic.Board
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
fun TheGame() {
    DrawTheBoard(board = Board(arrayOf(intArrayOf(0, 1, -1, 2, -3))))
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
