package io.qmpu842.labs.logic.profiles

import io.qmpu842.labs.helpers.MyRandom

class RandomProfile : OpponentProfile {
    val rand = MyRandom.random

    override fun nextMove(): Int {
        TODO("Not yet implemented")
    }
}
