object Main extends Action {
  def main(args: ujson.Value)(env: Map[String, String]): Either[String, ujson.Value] = {
    try {
      val name = args("name").str
      Right(ujson.Obj("greeting" -> s"Hello $name"))
    } catch {
      case e: Exception => Left("No \"name\" field found.")
    }
  }
}
