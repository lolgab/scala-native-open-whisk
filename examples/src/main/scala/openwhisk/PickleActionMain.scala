package openwhisk

object PickleActionMain extends Action[Int, List[Int]] {
  def main(arg: Int, env: Map[String, String]): Either[String, List[Int]] = {
    if(arg > 0) Right(List(arg + 1))
    else Left(s"$arg is a negative number!")
  }
}
