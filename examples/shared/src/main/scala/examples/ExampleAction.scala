package examples

import upickle.default._
import openwhisk.Action

// model
case class Input(int: Int)
case class Output(list: Seq[Int])

object RangeAction extends Action[Input, Output] {
  implicit val reader = macroR[Input]
  implicit val writer = macroW[Output]

  def main(arg: Input, env: Map[String, String]): Either[String, Output] = {
    if(arg.int > 0) Right(Output(0.to(arg.int)))
    else Left(s"$arg is a negative number!")
  }
}
