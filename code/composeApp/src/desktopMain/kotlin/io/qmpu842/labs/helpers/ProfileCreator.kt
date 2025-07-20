package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.profiles.*

object ProfileCreator {
    val human = HumanProfile()
    val rand = RandomProfile()
    val dfsProfileA = DFSProfile(-1)
    val dfsProfileB = DFSProfile(1)
    val simpleHeuristicGuyProfile = SimpleHeuristicGuyProfile()
    val simpleOpportunisticProfile = SimpleOpportunisticProfile() // the good guy
    val miniMaxV1Profile = MiniMaxV1Profile()
    val miniMaxV2Profile =
        MiniMaxV101Profile(
            depth = 20,
            timeLimit = 300,
        )
    val miniMaxV3Profile = // the current best?
        MiniMaxV1Profile(
            depth = 20,
        timeLimit = 300
    )

    val miniMaxV3Profile2 =
        MiniMaxV1Profile(
            depth = 5,
            timeLimit = 500
        )
    val miniMaxV3Profile3 =
        MiniMaxV1Profile(
            depth = 20,
            timeLimit = 150
        )

    val miniMaxV2Profile2 =
        MiniMaxV101Profile(
            depth = 6,
            timeLimit = 600,
        )
}
