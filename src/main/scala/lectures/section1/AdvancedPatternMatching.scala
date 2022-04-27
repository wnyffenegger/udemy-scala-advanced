package lectures.section1

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /**
   * constants
   * wildcards
   * case classes
   * tuples
   * some special magic (the aboe
   */

  // Make this compatible with pattern matching without a case class
  class Person(val name: String, val age: Int)

  // define a companion object
  // define a special method of unapply
  object PersonPattern {
    // deconstruct object
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  // Change bob's age to 21 to see matching errors
  val bob = new Person("Bob", 25)


  // So it unpacks the class using unapply

  // Steps
  // 1 Pattern called Person with a name and an age
  // 2 Look for a method called unapply on a companion object and look for a tuple
  // 3 Call unapply if value is Some and not None evaluate case
  // 4 Errors out if unapply returns None or no match found
  val greeting = bob match {
    case PersonPattern(name, age) => s"Hi my name is $name and I am $age years old"
  }
  println(greeting)

  val legalStatus = bob.age match {
    case PersonPattern(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /**
   * Exercise.
   *
   * Create pattern matching for integers for a list of properties
   */

  val n: Int = 45
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 ==0 => "an even number"
    case _ => "no property"
  }
  println(mathProperty)

  // Interpreted as Boolean tests
  object even {

    // Also valid
//    def unapply(arg: Int): Option[Boolean] = {
//      if (arg % 2 == 0) Some(true)
//      else None
//    }

    // Interpreted as Boolean tests
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singledigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val mathProperty2 = n match {
    case singledigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }
  println(mathProperty2)






  // infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number is written as $string"
//    case Or(number, string) => s"$number is written as $string"
  }
  println(humanDescription)

  // decomposing sequences so that you can pattern match on the sequence
  // or how to use unapply sequence
  // Capability, allows for variable length patterns

  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyListPattern {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyListPattern(1, 2, _*)  => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  // custom return types for unapply only require two methods
  // isEmpty: Boolean, get: something.

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = {
      new Wrapper[String] {
        def isEmpty = person.name == null
        def get = person.name
      }
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person is $name"
    case _ => s"Unknown name"
  })
}
