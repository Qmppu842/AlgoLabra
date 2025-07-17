package io.qmpu842.labs.logic

/**
 * Right +x
 * Left  -x
 * Up    -y
 * Down  +y
 */
enum class Way(
    val x: Int,
    val y: Int,
) {
    Up(0, -1),
    UpRight(1, -1),
    Right(1, 0),
    RightDown(1, 1),
    Down(0, 1),
    DownLeft(-1, 1),
    Left(-1, 0),
    LeftUp(-1, -1),
    ;

    fun getOpposite(): Way = entries[(this.ordinal + 4) % entries.size]
}
