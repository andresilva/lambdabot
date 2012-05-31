package lambdabot

import scala.util.Random

object BotEventLoop {
  def process(input: String) = {
    val vm = VM(input)

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

  // TODO
  // b64 encoder to encode VMs as strings
  // start creating bot brains
}
