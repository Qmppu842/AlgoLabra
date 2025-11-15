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
        MiniMaxV3Profile(
            depth = 0,
            timeLimit = TRILLION,
        )

    val minimaxDepth1TimeInf =
        MiniMaxV3Profile(
            depth = 1,
            timeLimit = TRILLION,
        )

    val minimaxDepth2TimeInf =
        MiniMaxV3Profile(
            depth = 2,
            timeLimit = TRILLION,
        )

    val minimaxDepth3TimeInf =
        MiniMaxV3Profile(
            depth = 3,
            timeLimit = TRILLION,
        )
    val minimaxDepth4TimeInf =
        MiniMaxV3Profile(
            depth = 4,
            timeLimit = TRILLION,
        )

    val minimaxDepth5TimeInf =
        MiniMaxV3Profile(
            depth = 5,
            timeLimit = TRILLION,
        )

    val minimaxDepth6TimeInf =
        MiniMaxV3Profile(
            depth = 6,
            timeLimit = TRILLION,
        )

    val minimaxDepth7TimeInf =
        MiniMaxV3Profile(
            depth = 7,
            timeLimit = TRILLION,
        )

    val minimaxDepth8TimeInf =
        MiniMaxV3Profile(
            depth = 8,
            timeLimit = TRILLION,
        )

    val minimaxDepth9TimeInf =
        MiniMaxV3Profile(
            depth = 9,
            timeLimit = TRILLION,
        )

    val minimaxDepth10TimeInf =
        MiniMaxV3Profile(
            depth = 10,
            timeLimit = TRILLION,
        )
    val minimaxDepth11TimeInf =
        MiniMaxV3Profile(
            depth = 11,
            timeLimit = TRILLION,
        )

    val minimaxDepth12TimeInf =
        MiniMaxV3Profile(
            depth = 12,
            timeLimit = TRILLION,
        )

    val minimaxDepth13TimeInf =
        MiniMaxV3Profile(
            depth = 13,
            timeLimit = TRILLION,
        )
    val minimaxDepth14TimeInf =
        MiniMaxV3Profile(
            depth = 14,
            timeLimit = TRILLION,
        )

    val minimaxDepth15TimeInf =
        MiniMaxV3Profile(
            depth = 15,
            timeLimit = TRILLION,
        )
}
