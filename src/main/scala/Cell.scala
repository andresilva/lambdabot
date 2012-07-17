package lambdabot

sealed trait Cell

sealed trait Good
sealed trait Bad

sealed trait Mine
sealed trait Enemy

sealed trait Plant
sealed trait Beast

case class Invisible() extends Cell
case class Empty() extends Cell
case class Wall() extends Cell with Bad
case class MyBot() extends Cell with Mine
case class EnemyBot() extends Cell with Enemy
case class MyMiniBot() extends Cell with Mine
case class EnemyMiniBot() extends Cell with Enemy
case class Zugar() extends Cell with Good with Plant
case class Toxifera() extends Cell with Bad with Plant
case class Fluppet() extends Cell with Good with Beast
case class Snorg() extends Cell with Bad with Beast

object Cell {
  def apply(c: Char) = {
    c match {
      case '?' => Invisible()
      case '_' => Empty()
      case 'W' => Wall()
      case 'M' => MyBot()
      case 'm' => EnemyBot()
      case 'S' => MyMiniBot()
      case 's' => EnemyMiniBot()
      case 'P' => Zugar()
      case 'p' => Toxifera()
      case 'B' => Fluppet()
      case 'b' => Snorg()
    }
  }
}
