package lambdabot

import scalaz.std.option._
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.syntax.applicative._
import scalaz.syntax.traverse._

sealed trait Opcode {
  def serialize: String = {
    val opcode = this.getClass.getSimpleName
    val params = serializeParams

    "%s(%s)".format(opcode, params)
  }

  final private def serializeParams: String = {
    val m = serializeParamsAsMap
    m.foldLeft("") { case (acc, (key, value)) =>
      acc + (if (acc.isEmpty) "" else ",") + "%s=%s".format(key, value)
    }
  }

  protected def serializeParamsAsMap: Map[String, String] = Map[String, String]()
}

sealed trait BotOpcode extends Opcode
sealed trait ServerOpcode extends Opcode

sealed trait NeutralOpcode extends BotOpcode

case class Exception() extends ServerOpcode with BotOpcode
case class NoOp() extends ServerOpcode with BotOpcode {
  override def serialize = ""
}

case class Welcome(name: String, apocalyse: Int, round: Int) extends ServerOpcode

case class React(generation: Int, name: String, time: Int, view: View,
                 energy: Int, master: Option[Coordinate] = None,
                 collision: Option[Coordinate] = None,
                 map: Map[String, String]) extends ServerOpcode

case class Goodbye(energy: Int) extends ServerOpcode

case class Move(direction: Coordinate) extends BotOpcode {
  override protected def serializeParamsAsMap = Map("direction" -> direction.toString)
}

case class Spawn(direction: Coordinate, name: String, energy: Int,
                 map: Map[String, String] = Map[String, String]()) extends BotOpcode {
  override protected def serializeParamsAsMap = Map("direction" -> direction.toString,
                                                    "name" -> name,
                                                    "energy" -> energy.toString) ++ map
}

case class Set(map: Map[String, String]) extends BotOpcode {
  override protected def serializeParamsAsMap = map
}

case class Explode(size: Int) extends BotOpcode {
  override protected def serializeParamsAsMap = Map("size" -> size.toString)
}

case class Say(text: String) extends NeutralOpcode {
  override protected def serializeParamsAsMap = Map("text" -> text)
}

case class Status(text: String) extends NeutralOpcode {
  override protected def serializeParamsAsMap = Map("text" -> text)
}

case class MarkCell(position: Coordinate, color: String) extends NeutralOpcode {
  override protected def serializeParamsAsMap = Map("position" -> position.toString,
                                                    "color" -> color)
}

case class DrawLine(from: Coordinate, to: Coordinate, color: String) extends NeutralOpcode {
  override protected def serializeParamsAsMap = Map("from" -> from.toString,
                                                    "to" -> to.toString,
                                                    "color" -> color)
}

case class Log(text: String) extends NeutralOpcode {
  override protected def serializeParamsAsMap = Map("text" -> text)
}

object Opcode {
  def serialize(ops: List[Opcode]) = ops.map(_.serialize).mkString("|")

  def apply(s: String): ServerOpcode = {
    val OpcodeRegex = """^(\w+)\((.+)\)$""".r

    try {
      val OpcodeRegex(opcode, p) = s

      val params = p.split(",").map(_.split("=")).foldLeft(Map[String, String]()) {
        case (acc, Array(key, value)) =>
          acc + (key -> value)
      }

      val res = opcode match {
        case "Welcome" => {
          val name = params.get("name")
          val apocalypse = params.get("apocalypse").map(_.toInt)
          val round = params.get("round").map(_.toInt)

          (name |@| apocalypse |@| round) apply Welcome.apply
        }
        case "React" => {
          val generation = params.get("generation")
          val name = params.get("name")
          val time = params.get("time")
          val view = params.get("view")
          val energy = params.get("energy")
          val master = params.get("master").flatMap(Coordinate.apply)
          val collision = params.get("collision").flatMap(Coordinate.apply)

          val map = params - ("generation", "name", "time",
                              "view", "energy", "master", "collision")

          // (generation |@| name |@| time |@| view |@|
          //  energy |@| master |@| collision |@| map) apply
          //  React.apply

          List(generation, name, time, view, energy).sequence.map {
            case (generation :: name :: time :: view :: energy :: Nil) =>
              React(generation.toInt, name, time.toInt, View(view), energy.toInt,
                    master, collision, map)
          }
        }
        case "Goodbye" => {
          val energy = params.get("energy").map(_.toInt)
          energy.map(Goodbye.apply)
        }
      }

      res match {
        case Some(op) => op
        case None => Exception()
      }

    } catch {
      case _: MatchError => Exception()
    }
  }
}
