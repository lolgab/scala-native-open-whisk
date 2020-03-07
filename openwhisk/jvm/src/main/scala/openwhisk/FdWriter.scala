package openwhisk

import java.nio.charset.Charset
import java.nio.CharBuffer
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.FileDescriptor

object Fd3Writer {
  val writer = new BufferedWriter(
    new java.io.OutputStreamWriter(new FileOutputStream({
      val fd = new FileDescriptor()
      sun.misc.SharedSecrets.getJavaIOFileDescriptorAccess().set(fd, 3)
      fd
    }))
  )
}
