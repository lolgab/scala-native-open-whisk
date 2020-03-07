package openwhisk.fd3writer

import scala.scalanative.unsafe._
import scala.scalanative.runtime.ByteArray
import scala.scalanative.posix.unistd
import java.nio.charset.Charset
import java.nio.CharBuffer
import java.io.BufferedWriter

private [fd3writer] object Fd3WriterImpl {
  private object JavaIoWriter extends java.io.Writer {
    override def write(buf: Array[Char], off: Int, len: Int): Unit = {
      val buffer = Charset.defaultCharset().encode(CharBuffer.wrap(buf, off, len))
      val bytes = buffer.array()
      val byteArray = bytes.asInstanceOf[ByteArray]
      unistd.write(3, byteArray.at(0), bytes.length)
    }

    override def flush(): Unit = ()

    override def close(): Unit = ()
  }

  private [fd3writer] val writer: BufferedWriter = new BufferedWriter(JavaIoWriter)
}
