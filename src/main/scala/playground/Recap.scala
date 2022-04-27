package playground

import scala.annotation.tailrec

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65

  // instructions vs. expressions

  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit
  // Expressions that have side effects but do not return anything

  // functions
  def aFunction(x: Int): Int = x + 1

  // Preserve stack frames
  @tailrec
  def factorial(n: Int, accumulator: Int): Int = {
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)
  }

  // OOP
  class Animal
  class Dog extends Animal

  // Subtype polymorphism
  val aDog: Animal = new Dog

  // Interface
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // infix notation
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // operators are permissive
  val xxxi = 1 + 2
  // 1.+2 is how the compiler interprets it

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("nom")
  }

  // generics (variance addressed in this course)
  abstract class MyList[+A]
  // singletons and companions
  object MyList

  // case classes
  // companions and utilities built in
  // serializable, apply, etc.
  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally expressions

  // Nothing type
  // replaces everything as the return type for an expression
  val throwsException = throw new RuntimeException

  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  }

  // packaging and imports

  // functional programming
  // functions are instances of classes with apply methods
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  incrementer(1)

  val anonInc = (x: Int) => x + 1
  List(1,2,3).map(anonInc) // higher order function

  // map, flatMap, filter
  // which are basis of for comprehensions
  val pairs = for {
    num <- List(1,2,3)
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: Seqs, Arrays, Lists, Maps, Vectors, Tuples
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "collections": Options, Try
  // Some, None
  val anOption = Some(2)

  // pattern matching
  // switch on steroids
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }
}
