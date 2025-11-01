package io.qmpu842.labs.helpers

fun a(x: Any) {
//
}


data class Zo(
    val thetable: IntArray,
    val onturn: Int,
) {


    companion object {
        private val first = 1
        private val second = 2

        operator fun invoke(boardSize: Int): Zo =
            Zo(
                thetable = IntArray(size = boardSize * 2) { MyRandom.random.nextBits(64) },
                onturn = MyRandom.random.nextBits(64) xor first,
            )

        operator fun invoke(
            width: Int,
            height: Int,
        ): Zo = invoke(width * height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Zo
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = 0


        return result
    }
}

class Zo1{
    val zoTable = Zo
}
