package io.qmpu842.labs.logic

import io.qmpu842.labs.helpers.BoardConfig
import io.qmpu842.labs.helpers.Stats
import io.qmpu842.labs.helpers.lapTime
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.abs
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
        /**
         * Same as runWithOutUi but runs both players as first and second player
         */
        fun runWithOutUiSplit(
            amount: Int,
            playerA: OpponentProfile,
            playerB: OpponentProfile,
        ) {
            val (ekaFirstStats, ekaSecondStats) = runWithOutUi(amount, playerA, playerB)
            println("----------------------")
            println("And now the other way:")
            val (tokaFirstStats, tokaSecondStats) = runWithOutUi(amount, playerB, playerA)

            println("----------------------")
            println("End stats:")
            printer(playerA, playerB, ekaFirstStats, "first")
            println("----------------------")
            printer(playerB, playerA, tokaFirstStats, "second")

            println("----------------------")
            println("End stats combined:")
            println(
                "Player A, ${playerA.name}, wins: ${ekaFirstStats.wins + tokaSecondStats.wins}," +
                    " score: ${ekaFirstStats.cumulativeScore + tokaSecondStats.cumulativeScore}," +
                    " avg score: ${0f +(ekaFirstStats.cumulativeScore + tokaSecondStats.cumulativeScore) / (ekaFirstStats.total() + tokaSecondStats.total())}",
            )
            println(
                "Player B, ${playerB.name}, wins: ${ekaSecondStats.wins + tokaFirstStats.wins}," +
                    " score: ${ekaSecondStats.cumulativeScore + tokaFirstStats.cumulativeScore}," +
                    " avg score: ${0f +(ekaSecondStats.cumulativeScore + tokaFirstStats.cumulativeScore) / (ekaSecondStats.total() + tokaFirstStats.total())}",
            )
            println("Draws: ${ekaFirstStats.draws + tokaFirstStats.draws}")
            println("All games total: ${ekaFirstStats.total() + tokaFirstStats.total()}")
        }

        private fun printer(
            firstPlayer: OpponentProfile,
            secondPlayer: OpponentProfile,
            stats: Stats,
            text: String,
        ) {
            println("Stats from $text game:")
            println("First player, ${firstPlayer.name}, wins: ${stats.wins}")
            println("Second player, ${secondPlayer.name}, wins: ${stats.losses}")
            println("Draws: ${stats.draws}")
            println("Total games: ${stats.total()}")
        }

        /**
         * Runs
         * @param amount of games and prints the wins and time it took to do so.
         * @param playerA and
         * @param playerB are the profiles used for this tournament.
         */
        fun runWithOutUi(
            amount: Int,
            playerA: OpponentProfile,
            playerB: OpponentProfile,
        ): Pair<Stats, Stats> {
            lapTime()
            var gameHolder =
                GameHolder(
                    playerA = playerA,
                    playerB = playerB,
                    bc = BoardConfig(),
                )
            var gameCounter = 0
            val startTime = System.currentTimeMillis()
            while (gameCounter < amount) {
                if (gameHolder.hasGameStopped()) {
                    gameCounter++
                    gameHolder = gameHolder.updateWinnersAndClearBoard()
                    println("Currently ended game: $gameCounter")
                }
                gameHolder = gameHolder.dropTokenLimited()
            }
            val endTime = System.currentTimeMillis()
            println("Ended, took about ${endTime - startTime} ms")
            println("Stats:")
            println("Red, ${gameHolder.playerA.name}, wins: ${gameHolder.playerA.firstPlayStats.wins}")
            println("Yellow, ${gameHolder.playerB.name}, wins: ${gameHolder.playerB.secondPlayStats.wins}")
            println("Draws: ${gameHolder.playerA.firstPlayStats.draws}")
            println("Total games: $gameCounter")
            return Pair(gameHolder.playerA.firstPlayStats, gameHolder.playerB.secondPlayStats)
        }
    }

    fun playerOnTurn() = if (board.getOnTurnToken().sign == 1) playerA else playerB

    fun undo() = this.copy(board = board.undoLastMove())

    fun clearBoard(): GameHolder = this.copy(board = board.clear())

    /**
     * @return true if game is won or board full, otherwise false
     */
    fun hasGameStopped() = (board.isLastPlayWinning() || board.isAtMaxSize())

    /**
     * @return Tells you if game is still going with null, or if game ended in draw, 0, or +1/-1 for wins
     */
    fun whoisWinner(): Int? {
        if (!hasGameStopped()) return null
        if ((!board.isLastPlayWinning() && board.isAtMaxSize())) return 0
        return -board.getOnTurnToken().sign
    }

    /**
     * This drops token to well if
     * @param column is equal or greater than 0, that well is used,
     * Otherwise it will ask current profiles next move and use that well.
     */
    fun dropTokenLimited(column: Int = -99): GameHolder {
        if (hasGameStopped()) return this
        var columnHolder = column
        if (column < 0) {
            columnHolder = playerOnTurn().nextMove(board = board.deepCopy(), forSide = board.getOnTurnToken().sign)
        }
//        else{
//            println("human playing")
//        }
        return this.copy(board = board.dropLockedToken(columnHolder))
    }

    /**
     * Updates the profile stats only if the game has ended.
     */
    fun updateWinners() {
        val winner = whoisWinner()

//        println("turnToken:  ${board.getOnTurnToken()}")
//        println("About to add this: ${1 + board.maxSize() - abs(board.getOnTurnToken())}")
//        println("thing thing: ${1f / abs(board.getOnTurnToken())}")
        val winnerGain = if(abs(board.getOnTurnToken()) > board.maxSize()) 0f else (board.maxSize() * 10f) / abs(board.getOnTurnToken())

        if (winner == null) {
            println("Game is still going.")
            return
        } else if (winner == 0) {
            playerA.firstPlayStats = playerA.firstPlayStats.draw()
            playerB.secondPlayStats = playerB.secondPlayStats.draw()

//            playerB.secondPlayStats.cumulate(1 + board.maxSize() - abs(board.getOnTurnToken()))
        } else if (winner.sign == 1) {
            playerA.firstPlayStats = playerA.firstPlayStats.win()
            playerB.secondPlayStats = playerB.secondPlayStats.lose()

//            playerA.firstPlayStats = playerA.firstPlayStats.cumulate(1F + board.maxSize() - abs(board.getOnTurnToken()))
//            playerA.firstPlayStats = playerA.firstPlayStats.cumulate(400f / abs(board.getOnTurnToken()))
//            playerB.secondPlayStats = playerB.secondPlayStats.cumulate(-abs(board.getOnTurnToken()))
            playerA.firstPlayStats = playerA.firstPlayStats.cumulate(winnerGain)
        } else {
            playerA.firstPlayStats = playerA.firstPlayStats.lose()
            playerB.secondPlayStats = playerB.secondPlayStats.win()

//            playerA.firstPlayStats = playerA.firstPlayStats.cumulate(-abs(board.getOnTurnToken()))
//            playerB.secondPlayStats =
//                playerB.secondPlayStats.cumulate(1 + board.maxSize() - abs(board.getOnTurnToken()))
//            playerB.secondPlayStats = playerB.secondPlayStats.cumulate(400f / abs(board.getOnTurnToken()))
//            playerB.secondPlayStats = playerB.secondPlayStats.cumulate( abs(board.getOnTurnToken()))
            playerB.secondPlayStats = playerB.secondPlayStats.cumulate(winnerGain)
        }
        println("PlayerA: ${playerA.firstPlayStats.cumulativeScore}")
        println("PlayerB: ${playerB.secondPlayStats.cumulativeScore}")
    }

    fun updateWinnersAndClearBoard(): GameHolder {
        lapTime()
        updateWinners()
        return clearBoard()
    }
}
