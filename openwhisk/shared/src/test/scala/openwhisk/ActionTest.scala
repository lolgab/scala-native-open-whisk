package openwhisk

import utest._
import upickle.default._
import scala.collection.mutable
import upickle.default

case class JsonInt(int: Int)
object JsonInt {
  implicit val rw: ReadWriter[JsonInt] = macroRW
}

object ExampleAction extends Action[JsonInt, JsonInt]() {
  
  override def main(args: JsonInt, env: Map[String,String]): Either[String,JsonInt] = {
    Right(JsonInt(args.int + 1))
  }

}

object ActionTest extends TestSuite {
  def myTest[T](action: Action[_, T], lines: Seq[String], expected: Seq[T])(implicit w: Writer[T]) = {
    val mutableLines = mutable.ListBuffer(lines:_*)
    def readLine() = try {
      mutableLines.remove(0)
    } catch {
      case _: IndexOutOfBoundsException => null
    }
    val outputJson = mutable.ListBuffer.empty[ujson.Obj]
    def writeJson(value: ujson.Obj) = outputJson += value

    ExampleAction.actionImpl(readLine, writeJson)
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
        myTest(ExampleAction, lines, expected)
      }
    }
  }
}
