package openwhisk

import scala.scalanative.posix.unistd
import scala.scalanative.posix.stdlib.setenv
import scala.scalanative.libc.stdlib.malloc
import scala.scalanative.libc.stdio._
import scala.scalanative.libc.string._
import scala.scalanative.unsafe._

object Platform {
  def setHomeEnv(): Unit = setenv(c"HOME", c"", 0)
}
