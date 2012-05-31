package lambdabot

import scala.util.Random

object BotEventLoop {
  def process(input: String) = {
    val opcode = Opcode.deserialize(input)

    val vm = opcode match {
      case r: React => r.map.get("vm").map(VM.apply).map(_.push(r.time, r))
      case w: Welcome => Some(VM(opcode))
      case _ => None
    }

    stub(input)
  }

  def stub(input: String) = {
    val tokens = input.split('(')
    if (tokens(0) == "React") {
      var x = Random.nextInt(3) - 1
      var y = Random.nextInt(3) - 1

      "Move(direction=%s:%s)|Say(text=Hello)".format(x, y)
    } else {
      ""
    }
  }
}
