package io.qmpu842.labs.logic.heuristics

import io.qmpu842.labs.logic.Way
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * This one pretends to be good one.
 * So this is last actually used with this name.
 */
@HeurName("lastMovesValueV5")
fun lastMovesValueV5(heuristicArgs: HeuristicArgs): Int {
    val (board, x, y, forSide, neededForWin) = heuristicArgs

    val w = board.boardConfig.width
    val h = board.boardConfig.height
    val size = sqrt(0.0 + w * w + h * h).toInt() + 2
    val omatLinjat = IntArray(size)
    val vihuLinjat = IntArray(size)
    val ilmaLinjat = IntArray(size)
    val ilmaLinjatOpp = IntArray(size)

    for (way in Way.half) {
        val opposite = Way.opp[way.ordinal]
//            omat linjat
        val resOma1: Int =
            board.checkLine(
                x = x,
                y = y,
                sign = forSide,
                way = way,
            )
        val resOma2: Int =
            board.checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = forSide,
                way = opposite,
            )
        val omaSum = resOma1 + resOma2
        omatLinjat[omaSum] += 1

        //        vihu linjat
        val resVih1: Int =
            board.checkLine(
                x = x,
                y = y,
                sign = -forSide,
                way = way,
            )
        val resVih2: Int =
            board.checkLine(
                x = x + opposite.x,
                y = y + opposite.y,
                sign = -forSide,
                way = opposite,
            )
        val vihuSum = resVih1 + resVih2
        vihuLinjat[vihuSum] += 1

        if (omaSum < 1) continue
//            ilma linjat
        val resAir1: Int =
            board.checkLine(
                x = x + (resOma1 * way.x),
                y = y + (resOma1 * way.y),
                sign = 0,
                way = way,
            )
        if (resAir1 > 0) {
            ilmaLinjat[omaSum] += 1
        }
        val resAir2: Int =
            board.checkLine(
                x = x + opposite.x + (resOma2 * opposite.x),
                y = y + opposite.y + (resOma2 * opposite.y),
                sign = 0,
                way = opposite,
            )
        if (resAir2 > 0) {
            ilmaLinjatOpp[omaSum] += 1
        }
    }
    var omaCounter = 0
    var vihuCounter = 0
    for (i in size - 1 downTo 0) {
        val ekaIlma = ilmaLinjat[i]
        val tokaIlma = ilmaLinjatOpp[i]
        val oma = omatLinjat[i]
        if (ekaIlma > 0 && tokaIlma > 0) {
            omaCounter += (oma * (10f.pow(i)).toInt()) * 2
        } else {
            omaCounter += oma * (10f.pow(i)).toInt()
        }
        val vihu = vihuLinjat[i]
        vihuCounter += -(vihu * (10f.pow(i)).toInt())
    }
    return if (abs(omaCounter) >= abs(vihuCounter)) {
        omaCounter
    } else {
        vihuCounter
    }
}
