package io.qmpu842.labs.helpers

import io.qmpu842.labs.logic.profiles.*

object ProfileHolder {
    val human = HumanProfile()
    val rand = RandomProfile()
    val dfsProfileA = DFSProfile(-1)
    val dfsProfileB = DFSProfile(1)
    val simpleHeuristicGuyProfile = SimpleHeuristicGuyProfile()
    val simpleOpportunisticProfile = SimpleOpportunisticProfile() // the good guy

    val minimaxDepth0TimeInf =
        MiniMaxV1Profile(
            depth = 0,
            timeLimit = TRILLION,
        )

    val minimaxDepth1TimeInf =
        MiniMaxV1Profile(
            depth = 1,
            timeLimit = TRILLION,
        )

    val minimaxDepth2TimeInf =
        MiniMaxV1Profile(
            depth = 2,
            timeLimit = TRILLION,
        )

    val minimaxDepth3TimeInf =
        MiniMaxV1Profile(
            depth = 3,
            timeLimit = TRILLION,
        )
    val minimaxDepth4TimeInf =
        MiniMaxV1Profile(
            depth = 4,
            timeLimit = TRILLION,
        )

    val minimaxDepth5TimeInf =
        MiniMaxV1Profile(
            depth = 5,
            timeLimit = TRILLION,
        )

    val minimaxDepth7TimeInf =
        MiniMaxV1Profile(
            depth = 7,
            timeLimit = TRILLION,
        )

    val minimaxDepth8TimeInf =
        MiniMaxV1Profile(
            depth = 8,
            timeLimit = TRILLION,
        )

    val minimaxDepth9TimeInf =
        MiniMaxV1Profile(
            depth = 9,
            timeLimit = TRILLION,
        )

    val minimaxDepth10TimeInf =
        MiniMaxV1Profile(
            depth = 10,
            timeLimit = TRILLION,
        )

    val minimaxDepth15TimeInf =
        MiniMaxV1Profile(
            depth = 15,
            timeLimit = TRILLION,
        )
}
