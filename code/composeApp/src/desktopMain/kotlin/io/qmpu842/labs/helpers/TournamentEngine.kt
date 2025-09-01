package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.GameHolder
import io.qmpu842.labs.logic.profiles.OpponentProfile
import kotlin.math.pow
import kotlin.math.round

object TournamentEngine {
    private const val K = 32
    private const val MAGNITUDE = 400
    private const val STARTING_ELO = 1500

    fun startTheTournament1(
        competitors: List<OpponentProfile>,
        amountOfGames: Int = 10,
    ) {
//        val statHolder = HashMap<Int, Pair<Stats, Stats>>(competitors.size)
        val eloRatings = HashMap<Int, Int>(competitors.size)
        val idToName = HashMap<Int, String>(competitors.size)

        competitors.forEach { opp ->
//            statHolder[opp.id] = Pair(Stats(), Stats())
            eloRatings[opp.id] = 1500
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
                val playerAElo = eloRatings[playerA.id] ?: 1500
                val playerBElo = eloRatings[playerB.id] ?: 1500
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

                        eloRatings[playerA.id] = endEloA
                        eloRatings[playerB.id] = endEloB

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
        println("Or ~${round((endTime - startTime) / 1000f / 60f)}m")
        println("Or ~${round((endTime - startTime) / 3600000f)}h")
        println("In total of $totalGames games, games per one way paring: $amountOfGames")
        println("Now for the Elo rankings:")

        val thing = eloRatings.entries.sortedBy { (_, value) -> -value }
        thing.forEachIndexed { index, (key, value) ->
            println("$index: at $value, ${idToName[key]}")
        }
    }

    fun startTheTournament(
        competitors: List<OpponentProfile>,
        amountOfGames: Int = 10,
    ) {
        val eloRatings = HashMap<Int, Int>(competitors.size)
        var eloHolder = HashMap<Int, Int>(competitors.size)
        val idToName = HashMap<Int, String>(competitors.size)

        competitors.forEach { opp ->
            eloRatings[opp.id] = STARTING_ELO
//            idToName[opp.id] = opp.name + if (opp.depth != -1) ", depth ${opp.depth}" else ""
            idToName[opp.id] = opp.name + ", depth ${opp.depth}, timelimit ${opp.timeLimit}"
        }
        val competitors = competitors.reversed()
        var totalGames = 0
        val startTime = System.currentTimeMillis()
        val endGames = amountOfGames * (competitors.size   * (competitors.size-1))
        lambo@while (totalGames < endGames) {
            val competitors = competitors.shuffled(MyRandom.random)
            for (playerA in competitors) {
                for (playerB in competitors) {
                    if (playerA.id == playerB.id) continue
                    playAGame(playerA, playerB, eloRatings, eloHolder)

                    totalGames += 1
                    println("Game $totalGames/${endGames}")
                    if (totalGames > endGames) break@lambo
                }
            }
            eloHolder.forEach { (key, value) ->
                eloRatings[key] = eloRatings[key]!! + value
            }
            eloHolder = HashMap(competitors.size)
        }
        val endTime = System.currentTimeMillis()

        println("And the games are done! It took ${endTime - startTime} ms, so ~${round((endTime - startTime) / 1000f)}s")
        println("Or ~${round((endTime - startTime) / 1000f / 60f)}m")
        println("Or ~${round((endTime - startTime) / 3600000f)}h")
        println("In total of $totalGames games, games per one way paring: $amountOfGames")
        println("Now for the Elo rankings:")

        val thing = eloRatings.entries.sortedBy { (_, value) -> -value }
        thing.forEachIndexed { index, (key, value) ->
            println("$index: at $value, ${idToName[key]}")
        }
    }

    private fun playAGame(
        playerA: OpponentProfile,
        playerB: OpponentProfile,
        eloRatings: HashMap<Int, Int>,
        eloHolder: HashMap<Int, Int>
    ) {
        var gameHolder =
            GameHolder(
                playerA = playerA,
                playerB = playerB,
                bc = BoardConfig(),
            )
        val playerAElo = eloRatings[playerA.id] ?: STARTING_ELO
        val playerBElo = eloRatings[playerB.id] ?: STARTING_ELO
        val expectedForA = eloExpected(playerAElo, playerBElo)
        val expectedForB = eloExpected(playerBElo, playerAElo)

        while (!gameHolder.hasGameStopped()) {
            gameHolder = gameHolder.dropTokenLimited()
        }
        val winner = gameHolder.whoisWinner() ?: 0

        val eloChangeA: Int = (K * (0.5 + (0.5 * winner) - expectedForA)).toInt()
        val eloChangeB: Int = (K * (0.5 + (0.5 * -winner) - expectedForB)).toInt()

        eloHolder[playerA.id] = (eloHolder[playerA.id] ?: 0) + eloChangeA
        eloHolder[playerB.id] = (eloHolder[playerB.id] ?: 0) + eloChangeB
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
