package lambdabot

import scala.collection.immutable.Stack

import VM.Types._

case class VM(s: Stack[(Tick, Opcode)]) {
  def push(t: Tick, o: Opcode) = VM(s.push((t, o)))

  def serialize(t: Tick): String = s.filter(_._1 == t).map(_._2.serialize).mkString("|")

  def b64serialize: String = "" // TODO
}

object VM {
  object Types {
    type Tick = Int
  }

  def apply(o: Opcode) = new VM(Stack((0, o)))

  // deserialize vm from base64string
  def apply(s: String) = new VM(Stack[(Tick, Opcode)]())
}
