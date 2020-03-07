package openwhisk

import utest._
import ujson.Obj
import scala.collection.mutable

case class JsonInt(value: Int)

object ExampleAction extends Action[JsonInt, JsonInt] {

  override def main(args: JsonInt, env: Map[String,String]): Either[String,JsonInt] = {
    Right(JsonInt(args.value + 1))
  }

}

object ActionTest extends TestSuite {
  val tests = Tests {
    test("ExampleAction") {
      test("should return correctly") {
        val result = ExampleAction.main(JsonInt(1), Map.empty[String, String])
        assert(result == Right(JsonInt(2)))
      }
      test("should read and write correctly") {
        val lines = mutable.ListBuffer(
          """{"value": {"int": 1}}""",
          """{"value": {"int": 2}}"""
        )
        def readLine() = try {
          lines.remove(0)
        } catch {
          case _: IndexOutOfBoundsException => null
        }
        val outputJson = mutable.ListBuffer.empty[ujson.Obj]
        def writeJson(value: ujson.Obj) = outputJson += value

        ExampleAction.actionImpl(readLine, writeJson)
        val expected = List(2.0, 3.0).map(ujson.Num)
        assert(outputJson == expected)
      }
    }
  }
}