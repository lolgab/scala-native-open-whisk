// package openwhisk

// object Main extends JsonAction {
//   def main(args: ujson.Obj, env: Map[String, String]): Either[String, ujson.Obj] = {
//     try {
//       val name = args("name").str
//       Right(ujson.Obj("greeting" -> s"Hello $name"))
//     } catch {
//       case e: Exception => Left("No \"name\" field found.")
//     }
//   }
// }
