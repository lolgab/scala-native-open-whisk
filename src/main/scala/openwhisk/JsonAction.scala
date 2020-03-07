package openwhisk

import scala.scalanative.posix.unistd
import scala.scalanative.posix.stdlib.setenv
import scala.scalanative.libc.stdlib.malloc
import scala.scalanative.libc.stdio._
import scala.scalanative.libc.string._
import scala.scalanative.unsafe._

import java.nio.charset.Charset
import scala.io.StdIn

trait JsonAction {
  def main(args: Array[String]): Unit = {
    // Scala Native crashes if HOME is not set
    // https://github.com/scala-native/scala-native/pull/1738
    setenv(c"HOME", c"", 0)
    if(System.getenv("__OW_WAIT_FOR_ACK") != null)
      Fd3Writer.writer.write("""{"ok":true}""")
    actionImpl(StdIn.readLine, writeToFd3)
  }

  private def writeToFd3(obj: ujson.Obj): Unit = {
    ujson.writeTo(obj, Fd3Writer.writer)
    Fd3Writer.writer.write('\n')
    Fd3Writer.writer.flush()
  }

  private [openwhisk] def actionImpl(readLine: () => String, write: ujson.Obj => Unit): Unit = {
    var line = readLine()
    while (line != null) {
      val resEither: Either[String, ujson.Obj] = try {
        val json = ujson.read(line).obj
        val value = json("value").obj
        val env = (json -= "value").mapValues(_.str).toMap
        main(value, env)
      } catch {
        case e: Exception => Left(e.toString())
      }
      val res = resEither.fold(
        err => ujson.Obj("error" -> err),
        res => res
      )
      write(res)
      line = readLine()
    }
  }

  def main(args: ujson.Obj, env: Map[String, String]): Either[String, ujson.Obj]

}
