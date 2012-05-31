package qwertybot

case class Coordinate(x: Int, y: Int) {
  override def toString = "%s:%s".format(x, y)
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
}
