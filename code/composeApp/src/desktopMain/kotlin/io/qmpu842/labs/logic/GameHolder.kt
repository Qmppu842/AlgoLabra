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

    /**
     * This is annoying.
     * @return value is null only if board is full and no win on last ply.
     *  Otherwise (even when there is no winner) this will return on turn player
     *
     *  So this needs to be used with hasGameStopped all the time to actually know.
     *
     *  There is good argument to convert this to return Int?, and then -1 for A, +1 for B, 0 for draw and null for when game is still going.
     *
     *  If this style was really really wanted, one could look things like Arrowkt's Either.
     */
    fun whoisWinner1(): OpponentProfile? {
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return null
        return if (board.getOnTurnToken().sign == 1) playerA else playerB
    }

    fun whoisWinner(): Int? {
        if (hasGameStopped()) return null
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return 0
        return board.getOnTurnToken().sign
    }



    fun dropTokenLimited(column: Int = -99): GameHolder {
        if (hasGameStopped()) return this
        var columnHolder = column
        if (column < 0) {
            columnHolder = playerOnTurn().nextMove(board = board.deepCopy(), forSide = board.getOnTurnToken().sign)
        }
        return this.copy(board.dropLockedToken(columnHolder))
    }

    /**
     * Updates the profile stats only if the game has ended.
     */
    fun updateWinners() {
        val winner = whoisWinner()

        if (winner == null) {
            println("Game is still going.")
            return
        } else if (winner == 0) {
            playerA.firstPlayStats = playerA.firstPlayStats.draw()
            playerB.secondPlayStats = playerB.secondPlayStats.draw()
        } else if (winner.sign == -1) {
            playerA.firstPlayStats = playerA.firstPlayStats.win()
            playerB.secondPlayStats = playerB.secondPlayStats.lose()
        } else {
            playerA.firstPlayStats = playerA.firstPlayStats.lose()
            playerB.secondPlayStats = playerB.secondPlayStats.win()
        }
    }

    fun clearBoardAndUpdateWinners(): GameHolder {
        lapTime()
        updateWinners()
        return clearBoard()
    }
}
