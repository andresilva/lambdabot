package lambdabot

import scalaz.std.option._
import scalaz.std.anyVal._
import scalaz.syntax.applicative._

sealed trait Opcode {
  final def serialize: String = {
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

case class Welcome(name: String, apocalyse: Int, round: Int) extends ServerOpcode

case class React(generation: Int, name: String, time: Int, view: View,
                 energy: Int, master: Option[Coordinate], collision: Option[Coordinate],
                 map: Map[String, String]) extends ServerOpcode

case class Goodbye(energy: Int) extends ServerOpcode

case class Move(direction: Coordinate) extends BotOpcode {
  override protected def serializeParamsAsMap = Map("direction" -> direction.toString)
}

case class Spawn(direction: Coordinate, name: String, energy: Int) extends BotOpcode {
  override protected def serializeParamsAsMap = Map("direction" -> direction.toString,
                                         "name" -> name,
                                         "energy" -> energy.toString)
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
  def serialize(o: BotOpcode): String = {
    o.serialize
  }

  def deserialize(s: String): ServerOpcode = {
    val OpcodeRegex = "^(\\w+)\\(([a-zA-Z0-9=,:]+)\\)$".r

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
          val generation = params.get("generation").map(_.toInt)
          val name = params.get("name")
          val time = params.get("time").map(_.toInt)
          val view = params.get("view").map(View.apply)
          val energy = params.get("energy").map(_.toInt)
          val master = params.get("master").map(Coordinate.apply)
          val collision = params.get("collision").map(Coordinate.apply)

          val map =
            Some(params - ("generation", "name", "time",
                           "view", "energy", "master", "collision"))

          (generation |@| name |@| time |@| view |@|
           energy |@| master |@| collision |@| map) apply React.apply
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
