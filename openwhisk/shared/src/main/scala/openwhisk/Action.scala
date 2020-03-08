package openwhisk

import upickle.default._

trait Action[T, U] extends JsonAction {
  val reader: Reader[T]
  val writer: Writer[U]
  def main(args: T, env: Map[String, String]): Either[String, U]

  def main(args: ujson.Obj, env: Map[String,String]): Either[String,ujson.Obj] = {
    try {
      val argsJson = read[T](args)(reader)
      val res: Either[String, U] = main(argsJson, env)
      res.right.map(u => writeJs(u)(writer).obj)
    } catch {
      case e: Exception => Left(e.toString)
    }
  }
}
