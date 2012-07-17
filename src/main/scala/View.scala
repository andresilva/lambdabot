package lambdabot

import scala.math._

case class View(v: IndexedSeq[Cell]) {
  lazy val relativeView = v.zipWithIndex.map { case (cell, index) => (cell, relativeFromIndex(index)) }

  val size = sqrt(v.length).toInt
  val center = Coordinate(size / 2, size / 2)

  def apply(c: Coordinate) = v(indexFromRelative(c))

  def myself: Coordinate = center

  def distance(to: Coordinate): Int = {
    // TODO: take walls into account
    abs(to.x - myself.x) + abs(to.y - myself.y)
  }

  def nearest[T]: Option[Coordinate] = {
    val filtered = relativeView.filter(c => c._1.isInstanceOf[T] && c._2 != myself).map(_._2)

    if (filtered.isEmpty) {
      None
    } else {
      Some(filtered.minBy(distance))
    }
  }

  def indexFromRelative(relative: Coordinate) = indexFromAbsolute(absoluteFromRelative(relative))
  def indexFromAbsolute(absolute: Coordinate) = absolute.x + absolute.y * size
  def relativeFromIndex(index: Int) = relativeFromAbsolute(absoluteFromIndex(index))
  def relativeFromAbsolute(abs: Coordinate) = abs - center
  def absoluteFromIndex(index: Int) = Coordinate(index % size, index / size)
  def absoluteFromRelative(relative: Coordinate) = relative + center
}

object View {
  def apply(s: String) = {
    val v = s.toList.map(Cell.apply).toIndexedSeq
    new View(v)
  }
}
