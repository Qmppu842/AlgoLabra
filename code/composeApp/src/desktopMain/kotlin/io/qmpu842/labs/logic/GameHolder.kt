package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.ProfileHolder
import io.qmpu842.labs.helpers.lapTime
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.sign

data class GameHolder(
    val board: Board,
    val playerA: OpponentProfile,
    val playerB: OpponentProfile,
    val bc: BoardConfig,
) {
    constructor(
        playerA: OpponentProfile,
        playerB: OpponentProfile,
        bc: BoardConfig,
    ) : this(board = Board(bc), playerA = playerA, playerB = playerB, bc)

    companion object {
        fun runWithOutUi(amount: Int) {
            var gameHolder =
                GameHolder(
                    playerA = ProfileHolder.rand,
                    playerB = ProfileHolder.miniMaxV3Profile5,
                    bc = BoardConfig(),
                )
            var gameCounter = 0
            val alku = System.currentTimeMillis()
            while (gameCounter < amount) {
                if (gameHolder.hasGameStopped()) {
                    lapTime()
                    gameCounter++
                    gameHolder = gameHolder.clearBoardAndUpdateWinners()
                }
                gameHolder = gameHolder.dropTokenLimited()
            }
            val loppu = System.currentTimeMillis()
            println("Ended, took about ${loppu - alku} ms")
            println("Stats:")
            println("Red wins: ${gameHolder.playerA.firstPlayStats.wins}")
            println("Yellow wins: ${gameHolder.playerB.secondPlayStats.wins}")
        }
    }

    fun playerOnTurn() = if (board.getOnTurnToken().sign == 1) playerA else playerB

    fun undo() = this.copy(board = board.undoLastMove())

    fun clearBoard(): GameHolder = this.copy(board = board.clear())

    fun hasGameStopped() = (board.isLastPlayWinning() || board.isAtMaxSize())

    fun whoisWinner(): OpponentProfile? {
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return null
        return if (board.getOnTurnToken().sign == 1) playerA else playerB
    }

    fun whoisWinnerText(): String =
        if (board.getOnTurnToken().sign == -1) {
            "Player B, The Yellow One! The ${playerB.name}"
        } else {
            "Player A, The Red One! The ${playerA.name}"
        }

    fun dropTokenLimited(column: Int = -99): GameHolder {
        if (hasGameStopped()) return this
        var columnHolder = column
        if (column < 0) {
            columnHolder = playerOnTurn().nextMove(board = board.deepCopy(), forSide = board.getOnTurnToken().sign)
        }
        return this.copy(board.dropLockedToken(columnHolder))
    }

    fun updateWinners() {
        val winner = whoisWinner()
        if (winner != null) {
            if (board.getOnTurnToken().sign == 1) {
                playerA.firstPlayStats = playerA.firstPlayStats.win()
                playerB.secondPlayStats = playerB.secondPlayStats.lose()
            } else {
                playerA.firstPlayStats = playerA.firstPlayStats.lose()
                playerB.secondPlayStats = playerB.secondPlayStats.win()
            }
        } else {
            playerA.firstPlayStats = playerA.firstPlayStats.draw()
            playerB.secondPlayStats = playerB.secondPlayStats.draw()
        }
    }

    fun clearBoardAndUpdateWinners(): GameHolder {
        lapTime()
        updateWinners()
        return clearBoard()
    }
}
