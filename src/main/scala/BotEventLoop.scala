package lambdabot

import scala.util.Random
import scalaz.syntax.id._

import Coordinate.Implicits._

object BotEventLoop {
  val random = new Random()
  var counter = 0

  def process(input: String) = {
    val opcode = Opcode(input)

    val outputOpcodes = Opcode(input) match {
      case react: React => {
        react.view.nearest[Zugar].map(Move.apply).getOrElse(Move(Coordinate.random))
        val nearest = react.view.nearest[Zugar]
        nearest.map { s => println("some"); s }.getOrElse { println("none"); Coordinate(0, 0) }
        println("Nearest zugar in direction: " + nearest.get.x + " " + nearest.get.y)
        List(react.view.nearest[Zugar].map(Move.apply).getOrElse {
          var move = Coordinate.random
          while (react.view(move).isInstanceOf[Bad]) {
            move = Coordinate.random
          }
          Move(move)
        })
      }
      case _ => List(NoOp())
    }

    val output = Opcode.serialize(outputOpcodes)

    if (counter < 3) {
      println("Input: " + "\n" + input + "\n")
      println("Input Opcode: " + opcode.getClass.getSimpleName)
      println("Output Opcode: " + outputOpcodes.head.getClass.getSimpleName)
      println("Output: " + output)
      counter += 1
    }

    output
  }
}
