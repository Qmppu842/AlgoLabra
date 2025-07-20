package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.profiles.*

object ProfileCreator {
    val human = HumanProfile()
    val rand = RandomProfile()
    val dfsProfileA = DFSProfile(-1)
    val dfsProfileB = DFSProfile(1)
    val simpleHeuristicGuyProfile = SimpleHeuristicGuyProfile()
    val simpleOpportunisticProfile = SimpleOpportunisticProfile()
}
