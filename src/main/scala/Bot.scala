import scala.util.Random

import lambdabot._

class ControlFunctionFactory {
  def create = new ControlFunction().respond _
}

class ControlFunction {
  def respond(input: String) = {
    // val tokens = input.split('(')
    // if(tokens(0)=="React") {
    //     var x = Random.nextInt(3) - 1
    //     var y = Random.nextInt(3) - 1

    //     "Move(direction=%s:%s)|Say(text=Hello)".format(x, y)
    // } else {
    //   ""
    // }

    val opcode = Opcode.deserialize(input)

    val vm = opcode match {
      case r: React => r.map.getOrElse("vm", "")
      case _ => ""
    }
    // deserialize(vm).push(opcode)

       val tokens = input.split('(')
    if(tokens(0)=="React") {
        var x = Random.nextInt(3) - 1
        var y = Random.nextInt(3) - 1

        "Move(direction=%s:%s)|Say(text=Hello)".format(x, y)
    } else {
      ""
    }
  }
}
