import argonaut.Json

import scala.scalanative.posix.unistd.{write, STDOUT_FILENO, STDERR_FILENO}
import scala.scalanative.native.stdio.fflush
import scala.scalanative.native._
import scala.scalanative.native.string.strlen

import argonaut._, Argonaut._
import scala.io.StdIn
import java.nio.charset.Charset
import scala.util.control.NonFatal

trait Action {
  def main(args: Json)(env: Map[String, String]): Json

  def writeLine(line: String): Unit = Zone { implicit z =>
    val cstring = toCString(line + '\n', Charset.forName("UTF-8"))
    write(3, cstring, strlen(cstring)) // could be more performant
  }

  def main(args: Array[String]): Unit = {
    while (true) {
      val line = StdIn.readLine()
      val jsonEither = JsonParser.parse(line)
      val jsonObjectEither: Either[String, JsonObject] = jsonEither.right
        .flatMap(_.obj.toRight("The json value is not an object"))
      val resEither = jsonObjectEither.right.flatMap { obj =>
        val env = (obj.toMap - "value").mapValues(_.nospaces)
        obj("value").toRight("No \"value\" key found").right.flatMap { payload =>
          try Right(main(payload)(env))
          catch {
            case e: Throwable => 
              e.printStackTrace()
              Left(e.getMessage())
          }
        }
      }
      val res = resEither.fold(
        err => Json.obj("error" -> jString(err)),
        res => res
      )
      val resString = res.nospaces
      writeLine(resString)
    }
  }
}
