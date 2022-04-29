package lectures.section4

object ImplicitsIntro extends App {

  // There is no arrow method on a string so how does this work?

  // If you run CMD-B on the arrow it goes to ArrowAssoc, a class that adds implicits
  val pair = "Daniel" -> "555"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }
  implicit def fromStringToPerson(str: String): Person = Person(str)

  // Compiler looks for anything that can turn a string into something that has a greet method
  println("Peter".greet)
  // Compiled code looks like
  println(fromStringToPerson("Peter").greet)

  class A {
    def greet: Int = 2
  }

  // Compiler will be confused because there are now two methods that result in greet methods
//  implicit def fromStringToA(str: String): A = new A

  // implicit parameters

  // Compiler looks for a parameter that satisfies the implicit
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount: Int = 10

  println(increment(2))
}
