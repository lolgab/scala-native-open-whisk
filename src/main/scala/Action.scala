import scala.scalanative.posix.unistd.{write, fsync}
import scala.scalanative.libc.string.strlen
import scala.scalanative.unsafe._

import ujson._
import scala.io.StdIn
import java.nio.charset.Charset

trait Action {
  def main(args: ujson.Value)(env: Map[String, String]): Either[String, ujson.Value]

  def writeLine(line: String): Unit = Zone { implicit z =>
    val cstring = toCString(s"$line\n", Charset.forName("UTF-8"))
    write(3, cstring, strlen(cstring)) // could be more performant
  }

  def main(args: Array[String]): Unit = {
    while (true) {
      val resEither = try {
        val line = StdIn.readLine()
        val json = ujson.read(line)
        val value = json("value")
        val env = (json.obj -= "value").mapValues(_.str).toMap
        main(value)(env)
      } catch {
        case e: Exception =>
          Left(e.getMessage())
      }
      val res = resEither.fold(
        err => Js.Obj("error" -> err),
        res => res
      )
      val resString = ujson.write(res)
      System.err.println(resString)
      writeLine(resString)
    }
  }
}
