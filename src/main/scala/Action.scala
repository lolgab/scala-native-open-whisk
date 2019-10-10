import argonaut.Json

import scala.scalanative.posix.unistd.write
import scala.scalanative.native._
import scala.scalanative.native.string.strlen

import argonaut._, Argonaut._
import scala.io.StdIn

trait Action {
  def main(args: Json): Json

  def writeLine(line: String): Unit = Zone { implicit z =>
    val cstring = toCString(line + '\n')
    write(3, cstring, strlen(cstring)) // could be more performant
  }

  def main(args: Array[String]): Unit = {
    while (true) {
      val line = StdIn.readLine()
      val json = JsonParser.parse(line)
      val res: Json = json match {
        case Left(err) => Json("error" -> jString(err))
        case Right(value) => main(value)
      }
      writeLine(res.nospaces)
    }
  }
}