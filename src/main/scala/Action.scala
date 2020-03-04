import scala.scalanative.posix.unistd.{write, fsync}
import scala.scalanative.libc.string.strlen
import scala.scalanative.unsafe._

import scala.io.StdIn
import java.nio.charset.Charset

trait Action {
  def main(args: ujson.Obj)(env: Map[String, String]): Either[String, ujson.Value]

  def writeLine(line: String): Unit = Zone { implicit z =>
    val cstring = toCString(s"${line.replaceAll("\n", "")}\n", Charset.forName("UTF-8"))
    write(3, cstring, strlen(cstring)) // could be more performant
  }

  def main(args: Array[String]): Unit = {
    if(sys.env.get("__OW_WAIT_FOR_ACK").exists(_.nonEmpty))
      writeLine("""{"ok":true}""")
    while (true) {
      val resEither = try {
        val line = StdIn.readLine()
        System.err.println(line)
        System.err.flush()
        val json = ujson.read(line).obj
        val value = json("value").obj
        val env = (json -= "value").mapValues(_.str).toMap
        main(value)(Map())
      } catch {
        case e: Exception =>
          Left(e.getMessage())
      }
      val res = resEither.fold(
        err => ujson.Obj("error" -> err),
        res => res
      )
      val resString = ujson.write(res)
      writeLine(resString)
    }
  }
}
