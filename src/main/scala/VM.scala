package qwertybot

import scala.collection.immutable.Stack

import VM.Types._

case class VM(s: Stack[(Tick, Opcode)]) {
  def push(p: Opcode, t: Tick) = VM(s.push((t, p)))

  def serialize(t: Tick): String = s.filter(_._1 == t).map(_._2.serialize).mkString("|")
}

object VM {
  object Types {
    type Tick = Int
  }

  // deserialize vm from base64string
  def apply(s: String) = ""
}
