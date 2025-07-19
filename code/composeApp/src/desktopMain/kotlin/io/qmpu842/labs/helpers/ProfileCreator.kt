package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.profiles.DFSProfile
import io.qmpu842.labs.logic.profiles.HumanProfile
import io.qmpu842.labs.logic.profiles.RandomProfile

object ProfileCreator {
    val human = HumanProfile()
    val rand = RandomProfile()
    val dfsProfileA = DFSProfile(-1)
    val dfsProfileB = DFSProfile(1)
}