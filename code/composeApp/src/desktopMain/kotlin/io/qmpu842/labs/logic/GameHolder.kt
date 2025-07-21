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
}
