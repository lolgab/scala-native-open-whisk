package openwhisk

import upickle.default._

trait Action[T, U] extends JsonAction {
  implicit def ir: Reader[T] = implicitly
  implicit def ow: Writer[U] = implicitly

  def main(args: T, env: Map[String, String]): Either[String, U]

  def main(args: ujson.Obj, env: Map[String,String]): Either[String,ujson.Obj] = {
    try {
      scribe.info("before reading")
      val argsJson = read[T](args)
      val res: Either[String, U] = main(argsJson, env)
      res.right.map(u => writeJs(u).obj)
    } catch {
      case e: Exception => Left(e.toString)
    }
  }
}
