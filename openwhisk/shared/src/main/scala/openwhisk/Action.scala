package openwhisk

import upickle.default._

abstract class Action[T, U](implicit val ir: Reader[T], val ow: Writer[U]) extends JsonAction {

  def main(args: T, env: Map[String, String]): Either[String, U]

  def main(args: ujson.Obj, env: Map[String,String]): Either[String,ujson.Obj] = {
    try {
      val argsJson = read[T](args)
      val res: Either[String, U] = main(argsJson, env)
      res.right.map(u => writeJs(u).obj)
    } catch {
      case e: Exception => Left(e.toString)
    }
  }
}
