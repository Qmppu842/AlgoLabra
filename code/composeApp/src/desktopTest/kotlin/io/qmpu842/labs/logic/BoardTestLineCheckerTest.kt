package io.qmpu842.labs.logic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Point

class BoardTestLineCheckerTest : FunSpec({

    test("doubleLine") { }

    test("checkLine") { }

 test("checkLine on straight line down") {
  val dropThisMany = 5
  val height = 6
  val width = 7
  var board =
   Board(
    boardWidth = width,
    boardHeight = height,
   )

  repeat(dropThisMany) {
   board = board.dropToken(1, it + 1)
  }
  val lineLength =
   board.checkLine(
    current = Point(1, 1),
    sign = 1,
    way = Way.Down,
    length = 1
   )

  lineLength shouldBe dropThisMany
 }
})
