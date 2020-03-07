package openwhisk

import utest._
import upickle.default._
import scala.collection.mutable
import upickle.default

case class JsonInt(int: Int)
object JsonInt {
  implicit val rw: ReadWriter[JsonInt] = macroRW
}

case class JsonList(list: List[Int])
object JsonList {
  implicit val rw: ReadWriter[JsonList] = macroRW
}

object ExampleAction extends Action[JsonInt, JsonInt] {
  implicit val reader = JsonInt.rw
  implicit val writer = JsonInt.rw
  override def main(args: JsonInt, env: Map[String,String]): Either[String,JsonInt] = {
    Right(JsonInt(args.int + 1))
  }

}

object ActionTest extends TestSuite {
  def testAction[T](action: Action[_, T], lines: Seq[String], expected: Seq[T])(implicit w: Writer[T]) = {
    val mutableLines = mutable.ListBuffer(lines:_*)
    def readLine() = try {
      mutableLines.remove(0)
    } catch {
      case _: IndexOutOfBoundsException => null
    }
    val outputJson = mutable.ListBuffer.empty[ujson.Obj]
    def writeJson(value: ujson.Obj) = outputJson += value

    action.actionImpl(readLine, writeJson)
    val expectedJson = expected.map(writeJs[T](_)(w))
    assert(outputJson == expectedJson)
  }

  val tests = Tests {
    test("ExampleAction") {
      test("should return correctly") {
        val result = ExampleAction.main(JsonInt(1), Map.empty[String, String])
        assert(result == Right(JsonInt(2)))
      }
      test("should read and write correctly") {
        val lines = List(
          """{"value": {"int": 1}}""",
          """{"value": {"int": 2}}"""
        )
        val expected = Seq(JsonInt(2), JsonInt(3))
        testAction(ExampleAction, lines, expected)
      }
      test("should manage lists") {
        val lines = List(
          """{"value": {"int": 1}}""",
          """{"value": {"int": 2}}"""
        )
        val action = new Action[JsonInt, JsonList] {
          def main(args: JsonInt, env: Map[String,String]): Either[String,JsonList] = {
            if(args.int > 0) Right(JsonList(0.to(args.int).toList))
            else Left("error")
          }
          implicit val reader: upickle.default.Reader[JsonInt] = JsonInt.rw
          implicit val writer: upickle.default.Writer[JsonList] = JsonList.rw
        }
        val expected = Seq(JsonList(List(0,1)), JsonList(List(0,1,2)))
        testAction[JsonList](action, lines, expected)
      }
    }
  }
}
