package lambdabot

sealed trait Brain

trait BotBrain extends Brain {
  // def think(r: React) = {
  //   r.view.bad.max { case (bad, coordinate) =>
  //     r.view.distance(coordinate)
  //   }
  // }
}

trait MiniBotBrain extends Brain {

}
