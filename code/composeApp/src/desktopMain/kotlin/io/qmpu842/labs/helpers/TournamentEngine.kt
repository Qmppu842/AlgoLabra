package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.pow
import kotlin.math.round

object TournamentEngine {
    private const val K = 32
    private const val MAGNITUDE = 400

    fun startTheTournament(
        competitors: List<OpponentProfile>,
        amountOfGames: Int = 10,
    ) {
//        val statHolder = HashMap<Int, Pair<Stats, Stats>>(competitors.size)
        val eloHolder = HashMap<Int, Int>(competitors.size)
        val idToName = HashMap<Int, String>(competitors.size)

        competitors.forEach { opp ->
//            statHolder[opp.id] = Pair(Stats(), Stats())
            eloHolder[opp.id] = 1500
            idToName[opp.id] = opp.name + if (opp.depth != -1) ", depth ${opp.depth}" else ""
        }
        var totalGames = 0
        val startTime = System.currentTimeMillis()
        for (playerA in competitors) {
            for (playerB in competitors) {
                if (playerA.id == playerB.id) continue
                var gameHolder =
                    GameHolder(
                        playerA = playerA,
                        playerB = playerB,
                        bc = BoardConfig(),
                    )
                val playerAElo = eloHolder[playerA.id] ?: 1500
                val playerBElo = eloHolder[playerB.id] ?: 1500
                var expectedForA = eloExpected(playerAElo, playerBElo)
                var expectedForB = eloExpected(playerBElo, playerAElo)

                var gameCounter = 0
                while (gameCounter < amountOfGames) {
                    if (gameHolder.hasGameStopped()) {
                        gameCounter++
                        val winner = gameHolder.whoisWinner() ?: 0
                        gameHolder = gameHolder.updateWinnersAndClearBoard()
                        //                        println("Currently ended game: $gameCounter")

                        val endEloA: Int = (playerAElo + K * (0.5 + (0.5 * winner) - expectedForA)).toInt()
                        val endEloB: Int = (playerBElo + K * (0.5 + (0.5 * -winner) - expectedForB)).toInt()

//                        var endEloA = 0
//                        var endEloB = 0
//                        if (winner == 0){
//                            endEloA = (playerAElo + K  * (0.5 - expectedForA)).toInt()
//                            endEloB = (playerBElo + K  * (0.5 - expectedForB)).toInt()
//                        }else if(winner == 1){
//                            endEloA = (playerAElo + K  * (1 - expectedForA)).toInt()
//                            endEloB = (playerBElo + K  * (0 - expectedForB)).toInt()
//                        } else if (winner == -1){
//                            endEloA = (playerAElo + K  * (0 - expectedForA)).toInt()
//                            endEloB = (playerBElo + K  * (1 - expectedForB)).toInt()
//                        }

                        eloHolder[playerA.id] = endEloA
                        eloHolder[playerB.id] = endEloB

                        expectedForA = eloExpected(endEloA, endEloB)
                        expectedForB = eloExpected(endEloB, endEloA)
                    }
                    gameHolder = gameHolder.dropTokenLimited()
                }
                totalGames += gameCounter
            }
        }
        val endTime = System.currentTimeMillis()

        println("And the games are done! It took ${endTime - startTime} ms, so ~${round((endTime - startTime) / 1000f)}s")
        println("In total of ${totalGames} games, games per one way paring: ${amountOfGames}")
        println("Now for the Elo rankings:")

        val thing = eloHolder.entries.sortedBy { (_, value) -> -value }
        thing.forEachIndexed { index, (key, value) ->
            println("$index: at $value, ${idToName[key]}")
        }
    }

    private fun eloExpected(
        ratingA: Int,
        ratingB: Int,
    ): Float = 1 / (1 + 10f.pow((ratingB - ratingA) / MAGNITUDE))

//    fun createProfilesHelper(asd: OpponentProfile, listOfDepths: List<Int> = listOf(2,6,8,10)): List<OpponentProfile> {
//        val result = mutableListOf<OpponentProfile>()
//        listOfDepths.forEach { i ->
//            result.add(asd::class)
//        }
//        return result.toList()
//    }
}
