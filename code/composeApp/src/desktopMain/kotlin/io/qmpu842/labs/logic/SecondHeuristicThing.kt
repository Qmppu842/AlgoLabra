package io.qmpu842.labs.logic

object SecondHeuristicThing {
    /**
     * This values basically guaranteed win places open in the map
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3StraightAnd2AirSpace(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values places with 3 tokens
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith3Straight(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values places with already 2 tokens and that would have 2 airspaces around after this one
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith2TokensAndAirSpaceAround(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values places with 2 tokens and possibility for 4
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getMovesWith2TokensAndAirSpace(board: Board, forSide: Int): IntArray {
        TODO("Yet to be implemented")
    }

    /**
     * This values the open spaces around itself.
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun getOpenness(board: Board, forSide: Int,): IntArray {
        TODO("Yet to be implemented")
    }


    /**
     * This is the actual checker
     *
     * @param patternToLook
     *  x for this position
     *  o for air
     *  e for enemy
     *  a for allied
     *
     *  @param way is to signal which way to look the pattern
     *
     * @return array of ints such that:
     * index is the well to use,
     * sign of number is what side would benefit from it
     * And magnitude how many or how dangerous the position is
     */
    fun theChecker(board: Board, patternToLook: CharArray, way: Way): IntArray {
        TODO("Yet to be implemented")
    }
}
