package lambdabot

import scala.collection.immutable.Stack

import VM.Types._

case class VM(s: Stack[(Tick, Opcode)]) {
  def push(t: Tick, o: Opcode): VM = VM(s.push((t, o)))

  def serialize(t: Tick): String = s.filter(_._1 == t).map(_._2.serialize).mkString("|")

  def b64serialize: String = "" // TODO
}

object VM {
  object Types {
    type Tick = Int
  }

  def apply(o: Opcode): VM = new VM(Stack((0, o)))

  def apply(input: String): Option[VM] = {
    val opcode = Opcode.deserialize(input)

    opcode match {
      case r: React => r.map.get("vm").map(VM.b64deserialize).map(_.push(r.time, r))
      case w: Welcome => Some(VM.apply(opcode))
      case _ => None
    }
  }

  // deserialize vm from base64string
  private def b64deserialize(s: String): VM = new VM(Stack[(Tick, Opcode)]())
}
