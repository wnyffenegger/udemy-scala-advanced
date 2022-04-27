package lectures.section2

object PartialFunctions extends App {
  // Function1[Int, Int] === Int => Int
  val aFunction = (x: Int) => x + 1

  // Partial function, not defined on whole domain of input
  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  // {1, 2, 5} => Int
  // This is a proper function according to scala
  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // Scala gives us PartialFunction to handle this
  // Value inside braces is called "partial function value".
  // The value inside the braces can only be attributed
  // to a partial function and nothing else in Scala
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // Partial functions are actually based on pattern matching
  println(aPartialFunction(2))
//  println(aPartialFunction(23))

  // Partial function utilities
  //

  // Test whether a partial function can be run
  // using the arguments
  println(aPartialFunction.isDefinedAt(67))

  // Partial function can be lifted to total function
  // returning optionals
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(-1))

  // Partial functions can be chained
  val chained = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(lifted(45))
  println(chained(2))
  println(chained(45))

  // Partial Functions extend normal functions.
  // This is valid
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // Higher order functions accept partial functions
  // because they are a subtype of total functions
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)


  /**
   * Unlike functions, partial functions can only have ONE parameter type
   *
   * 1 Construct a partial function instance yourself (anonymous class)
   * 2 Implement a dumb chat bot
   * 3 Use scala io to read and print things to console
   */

  val aManualPartialFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int) = {
      x == 1 || x == 2 || x == 5
    }
  }

  val chatbot: PartialFunction[String, String] = {
    case "Hello" => "How are you"
    case "Goodbye" => "Have a nice day"
  }

  scala.io.Source.stdin.getLines().map(chatbot)
    .foreach(println)
}
