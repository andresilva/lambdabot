package qwertybot

case class View(v: IndexedSeq[Cell])

object View {
  def apply(s: String) = {
    val v = s.toList.map(Cell.apply).toIndexedSeq
    new View(v)
  }
}
