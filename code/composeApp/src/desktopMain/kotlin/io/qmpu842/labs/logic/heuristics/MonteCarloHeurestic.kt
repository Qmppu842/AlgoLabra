package io.qmpu842.labs.logic.heuristics


import io.qmpu842.labs.helpers.MyRandom
import io.qmpu842.labs.logic.Board


/**
 * Nooooooooo.....
 * I so wanted this to work...
 * Now making it work would be pain since the things I need here are in the gameholder.
 *
 * How should I deal with this iteratively....
 * Or better:
 * How would I have predicted this bit earlier while staying in the iterative mindset?
 *
 * I mean sure, I could go and rework things, but I don't have energy nor time to do it now.
 * 
 */
fun MonteCarloHeurestic(
    heuristicArgs: HeuristicArgs =
        HeuristicArgs(
            board = Board(),
            x = 0,
            y = 0,
            forSide = 1,
            neededForWin = 4,
        ),
): Int{
    val board = heuristicArgs.board.deepCopy()
    val themoves = heuristicArgs.board.getLegalsMiddleOutSeq()
    val buckets = IntArray(heuristicArgs.board.getWells()) { 0 }

    var theMove = 0
    repeat(10){
        var randd: Int
        do {
             randd = MyRandom.random.nextInt(buckets.size)
        } while (board.getWellSpace(randd) != 0)
        theMove = randd
        while (board.isLastPlayWinning() || board.isAtMaxSize()){

        }
    }

    return 0
}
