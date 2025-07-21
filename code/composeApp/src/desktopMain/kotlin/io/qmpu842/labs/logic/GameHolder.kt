package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
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
//        boardState = boardState.clear()
//        playerOnTurn = playerA
//        forSide.value = -1
//        isThereWinner= 0

    fun hasGameStopped() = (board.isLastPlayWinning() || board.isAtMaxSize())

    fun whoisWinner(): OpponentProfile? {
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return null
        return if (board.getOnTurnToken().sign == 1) playerA else playerB
    }

    fun dropTokenLimited(column: Int = -99): GameHolder {
        if (hasGameStopped()) return this
        var columnHolder = column
        if (column < 0) {
            columnHolder = playerOnTurn().nextMove(board = board.deepCopy(), forSide = board.getOnTurnToken().sign)
        }
        return this.copy(board.dropLockedToken(columnHolder))
    }

//    fun dropTokenLimited(column: Int) = if (hasGameStopped()) this else this.copy(board.dropLockedToken(column))

    fun updateWinners() {
    }
}

data class Stats(
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
)

// val playNextFromProfile = {
//    dropTokenAction(playerOnTurn.nextMove(board = boardState.deepCopy(), forSide = forSide.value))
// }
