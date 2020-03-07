package openwhisk

import scala.scalanative.posix.stdlib.setenv
import scala.scalanative.unsafe._

private [openwhisk] object Platform {
  def setHomeEnv(): Unit = setenv(c"HOME", c"", 0)
}
