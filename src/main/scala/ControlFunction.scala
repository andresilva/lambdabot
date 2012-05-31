import lambdabot.BotEventLoop

class ControlFunctionFactory {
  def create = new ControlFunction().respond _
}

class ControlFunction {
  def respond(input: String) = {
    BotEventLoop.process(input)
  }
}
