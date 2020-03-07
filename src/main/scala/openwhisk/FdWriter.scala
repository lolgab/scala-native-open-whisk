package openwhisk

import scala.scalanative.unsafe._
import scala.scalanative.runtime.ByteArray
import scala.scalanative.posix.unistd
import java.nio.charset.Charset
import java.nio.CharBuffer
import java.io.BufferedWriter

object Fd3Writer {
  private val javaIoWriter = new java.io.Writer {
    override def write(buf: Array[Char], off: Int, len: Int): Unit = {
      val buffer = Charset.defaultCharset().encode(CharBuffer.wrap(buf, off, len))
      val bytes = buffer.array()
      val byteArray = bytes.asInstanceOf[ByteArray]
      unistd.write(3, byteArray.at(0), bytes.length)
    }

    override def flush(): Unit = ()

    override def close(): Unit = ()
  }

  val writer = new BufferedWriter(javaIoWriter)
}
