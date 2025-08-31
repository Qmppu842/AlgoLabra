package io.qmpu842.labs.helpers

data class Stats(
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val cumulativeScore: Int = 0
) {
    fun win(): Stats = this.copy(wins = wins + 1)

    fun draw(): Stats = this.copy(draws = draws + 1)

    fun lose(): Stats = this.copy(losses = losses + 1)

    fun total() = wins+draws+losses

    fun cumulate(score: Int): Stats = this.copy(cumulativeScore = cumulativeScore + score)
}