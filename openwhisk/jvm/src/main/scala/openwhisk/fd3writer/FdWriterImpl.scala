package openwhisk.fd3writer

import java.nio.charset.Charset
import java.nio.CharBuffer
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.FileDescriptor
import java.io.OutputStream

private [fd3writer] object Fd3WriterImpl {
  val writer: BufferedWriter = new BufferedWriter(
    new java.io.OutputStreamWriter(System.out)
  )
}
