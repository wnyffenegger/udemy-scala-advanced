package lectures.section2

object CurriesPartiallyAppliedFunctions extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3)
  println(add3(5))

  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4)

  // Why does currying work?

  // It works because of lifting
  // Have to convert method to expression due to limitation of the JVM (functions != methods)
  // Methods are not instances of Functionx

  // lifting = ETA-Expansion
  // Convert method to instance of object so it can be an expression


  // What happens to make map work?

  // 1. ETA-expansion of method into function
  // 2. Applying the function
  def inc(x: Int) = x + 1
  List(1, 2, 3).map(inc)

  // Partial function applications sometimes need ETA-expansion

  // Underscore indicates the following: do an ETA-expansion for me and convert to a FunctionX
  val add5 = curriedAdder(5) _


  /**
   * Exercises
   *
   * 1. Function value for adding
   * 2. Method definition of same function
   * 3. Curried add method
   *
   * Create add7 as many different ways as possible
   */

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  val add7Simple: Int => Int = (y: Int) => simpleAddFunction(7, y)
  val add7SimpleMethod = (y: Int) => simpleAddMethod(7, y)

  // Use curried method for functions
  val add7SimpleCurried = simpleAddFunction.curried(7)


  val add7Curried: Int => Int = curriedAddMethod(7)
  val add7CurriedUnder = curriedAddMethod(7) _
  val add7CurriedAlt = curriedAddMethod(7)(_)

  // Syntactic sugar for turning methods into function values
  val add7_5 = simpleAddMethod(7, _)
  val add7_6 = simpleAddFunction(7, _)
  // Compiler rewrites as y => simpleAddMethod(7, y)


  /**
   * Lets talk more about underscores
   */

  // Method converted to function value with a single parameter
  // Underscore is super powerful
  def concatenator(a: String, b: String, c: String): String =  a + b + c
  val insertName = concatenator("Hello, I'm ", _, ", how are you")
  println(insertName("Will"))

  // Each underscore will be a different parameter in ETA-expanded function
  val fillInTheBlanks = concatenator("Hello, ", _, _)
  // (x, y) => concatenator("Hello, ", x, y)

  println(fillInTheBlanks("Will ", "Scala is awesome"))

  /**
   * Take a curried function for a formatter and apply to a list of numbers
   */

  def formatter(s: String, x: Double): String = s.format(x)
  val format = formatter("%8.6f", _)
  List(1.11111, 1.2222222222, 234.122222222222).map(format).foreach(println)

  /**
   * 2. difference between
   *  - functions vs methods
   *  - parameters: by-name vs. 0-lambda
   *
   *  Define a method call by name
   */

  def byName(n: Int) = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /**
   * Call each by name and by function
   * - int
   * - method
   * - paren method
   * - lambda
   * - partially applied function
   *
   * Figure out which case applies and why
   */

}
