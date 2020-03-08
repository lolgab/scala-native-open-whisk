# Scala Native OpenWhisk

Scala Native Open Whisk is a library to write OpenWhisk actions with Scala Native. It uses the [openwhisk-go-runtime](https://github.com/apache/openwhisk-runtime-go) and the ActionLoop protocol to receive data and communicate output.
The goal is to write actions in pure Scala code and without having to mess with Json object directly.
However, if you want, you can receive and manage JSON objects directly using the great [ujson and upickle libraries](https://github.com/lihaoyi/upickle).

## Getting Started

### Prerequisites

This library is not yet released. You need to `publishLocal` it yourself cloning and from sbt:

```
> openwhiskNative/publishLocal
```

### Installing

You can add it to any Scala Native 0.4.0-M2 project:
From Sbt:
```scala
libraryDependencies := "com.github.lolgab" %%% "scala-native-openwhisk" % "0.0.1-SNAPSHOT"
```

### Writing your first action

You need to implement the `Action` trait:

```scala
package openwhisk
import upickle.default._
trait Action[T, U] extends JsonAction {
  val reader: Reader[T]
  val writer: Writer[U]
  def main(args: T, env: Map[String, String]): Either[String, U]
}
```

It has three abstract members:

- `main` is the action implementation.
  
  - arguments:
    - `args`: the Json object converted into a Scala object
    - `env`: a Map containing the enviroment variables OpenWhisk passes at every invocation
  - returns:
    - `Either[String, U]` where the left part is an error message and `U` is a Scala object convertible to JSON
- `reader` is a upickle `Reader[T]` to read a Scala object from Json
- `writer` is a upickle `Writer[T]` to write a Scala object to Json

### An Example Action

```scala
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

```

You need to have case classes for the imput and for the output.
You can use upickle macros `macroR` and `macroW` to write `Reader` and `Writer` for you:

```scala
implicit val reader = macroR[Input]
implicit val writer = macroW[Output]
```

And implement your action:

```scala
def main(arg: Input, env: Map[String, String]): Either[String, Output] = {
  if(arg.int > 0) Right(Output(0.to(arg.int)))
  else Left(s"$arg is a negative number!")
}
```

From sbt you can then create a binary with `nativeLink` and try it through the shell.
Since the ActionLoop protocol uses the file descriptor 3 to send output, you can redirect it to stdout using `3>&1`:

```bash
echo '{"value": {"int": 10}}' | target/scala-2.11/example-action 3>&1
```

And you will see as result:

```
{"list":[0,1,2,3,4,5,6,7,8,9,10]}
```

## Running the tests

Tests are based on [utest](https://github.com/lihaoyi/utest)

You can run them from Sbt:

```
> openwhiskNative/test
> openwhiskJVM/test
```

It will run the tests with JVM and Scala Native. This is convenient, since tests in JVM run much faster and you can have a much faster feedback loop.


## Deployment

To run the action on Openwhisk you need to pass it to a runtime image.
I have created a docker image based on Alpine Linux that has the Scala Native runtime dependencies (some dynamic libraries) and the OpenWhisk Action Loop proxy.
You need to run `nativeLink` on Alpine to create a valid executable.
You can find an example on [script.sh](script.sh) where the example action is update to IBM Cloud

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.


## Authors

* **Lorenzo Gabriele** - *Initial work*

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details
