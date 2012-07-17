package lambdabot

import scala.util.Random

case class Coordinate(x: Int, y: Int) {
  override def toString = "%s:%s".format(x, y)

  def +(that: Coordinate) = Coordinate(x + that.x, y + that.y)
  def -(that: Coordinate) = Coordinate(x - that.x, y - that.y)
  def *(factor: Double) = Coordinate((x * factor).toInt, (y * factor).toInt)

  def distance(that: Coordinate) = (this-that).length
  def length = math.sqrt(x * x + y * y)
}

object Coordinate {
  def apply(s: String): Option[Coordinate] = {
    val CoordinateRegex = "^(-?\\d+):(-?\\d+)$".r

    try {
      val CoordinateRegex(x, y) = s
      Some(Coordinate(x.toInt, y.toInt))
    } catch {
      case _: MatchError => None
    }
  }

  def random = Coordinate(Random.nextInt(3) - 1, Random.nextInt(3) - 1)

  object Implicits {
    implicit def tupleToCoordinate(t: (Int, Int)) = Coordinate(t._1, t._2)
  }
}
