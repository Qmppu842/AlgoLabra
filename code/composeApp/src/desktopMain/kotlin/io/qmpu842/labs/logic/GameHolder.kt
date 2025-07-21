package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
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

    fun playerOnTurn() = if (board.getOnTurnToken().sign == -1) playerA else playerB

    fun undo() = this.copy(board = board.undoLastMove())

    fun clearBoard(): GameHolder = this.copy(board = board.clear())

    fun hasGameStopped() = (board.isLastPlayWinning() || board.isAtMaxSize())

    fun whoisWinner(): OpponentProfile? {
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return null
        return if (board.getOnTurnToken().sign == 1) playerA else playerB
    }

    fun whoisWinnerText(): String =
        if (board.getOnTurnToken().sign == -1) {
            "Player B, The Yellow One! The ${playerB::class.simpleName}"
        } else {
            "Player A, The Red One! The ${playerA::class.simpleName}"
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
