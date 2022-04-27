package lectures.section1

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    42
  }

  val aTry = Try {
    throw new RuntimeException
  }

  List(1,2,3).map { x => x + 1}

  // syntax sugar #2: single abstract method

  trait Action {
    def act(x: Int): Int
  }

  // you could do this
  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  // converts lambda to single abstract type
  val betterInstance: Action = (x: Int) => x + 1

  // example is with Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("stuff")
  })

  // better
  val aBetterThread = new Thread(() => println("stuff"))

  abstract class AbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  // Just syntactic sugars
  val anAbstractInstance: AbstractType = (a: Int) => println("sweet")


  // syntax sugar #3: the :: and #:: methods are special

  val prependedList = 2 :: List(3, 4)
  // 2.::(List(3,4))

  // scala spec: last char decides associativity of the method
  // if ends in a colon then right associative, so the operators are written in reverse order
  // else left associative
  1 :: 2 :: List(3) // is compiled to
  List(3).::(2).::(1)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation here
  }
  val myStream = 1 -->: 2 -->: 3 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi-word method naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }
  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is great!"

  // syntax sugar #5: infix types

  class Composite[A,B]
  val composite: Int Composite String = ???

  class -->[A,B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() method which is very special like apply

  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten anArray.update(2, 7)
  // used in mutable collections

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member = internalMember
    def member_=(value: Int): Unit = {
      internalMember = value
    }
  }

  val mutable = new Mutable
  mutable.member = 10
  //rewritten as mutable.member_=(10)
}
