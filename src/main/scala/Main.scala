import argonaut.Json
object Main extends Action {
  def main(args: Json)(env: Map[String, String]): Json = {
    args.field("name")
        .flatMap(_.string)
        .map(s => Json.obj("greeting" -> Json.jString(s"Hello $s")))
        .getOrElse(throw new Exception("No \"name\" field found."))
  }
}
