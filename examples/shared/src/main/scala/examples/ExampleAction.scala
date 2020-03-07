package examples

import upickle.default._
import openwhisk.Action

// model
case class Input(int: Int)
object Input {
  implicit val inputReader: Reader[Input] = macroR
}
case class Output(list: Seq[Int])
object Output {
  implicit val outputWriter: Writer[Output] = macroW
}

object IncrementAction extends Action[Input, Output] {
  implicit val reader: Reader[Input] = implicitly
  implicit val writer: Writer[Output] = implicitly

  def main(arg: Input, env: Map[String, String]): Either[String, Output] = {
    if(arg.int > 0) Right(Output(0.to(arg.int).toList))
    else Left(s"$arg is a negative number!")
  }
}
