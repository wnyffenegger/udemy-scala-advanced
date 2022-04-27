package lectures.section2

object LazyEvaluation extends App {

//  val x: Int = throw new RuntimeException()
  lazy val x: Int = throw new RuntimeException()

  lazy val y: Int = {
    println("hello")
    32
  }

  println(y)
  println(y)

  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

//  def byNameMethod(n: => Int): Int = {
//    3 * n + 1
//  }

  // Will only calculate t once even if function called multiple times
  def byNameMethod(n: => Int): Int = {
    // Call by name will trigger calculation of value three times
    // So switch to lazy with CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue: Int = {
    // side effect or a long computation
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }


  // By default whole filter must finish before
  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  println(lt30)

  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  // Use lazy values under the hood filtering never takes place
  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  println(gt20Lazy)

  gt20Lazy.foreach(println)

  // for comprehensions are lazy by default
  for {
    a <- List(1, 2, 3) if a % 2 == 0
  } yield a + 1

  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)

  /**
   * Exercise: implement a lazyily evaluated, singly linked STREAM of elements.
   *
   * naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite)
   * naturals.take(100).foreach(println) // still lazy evaluated
   * naturals.foreach(println) // will crash infinite
   * naturals.map(_ * 2) // potentially infinite but executed
   * naturals.map(_ * 2).take(100)
   */

  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    //prepend operator
    def #::[B >: A](element: B) : MyStream[B]
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B]

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A]
    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }

  class Stream[A] extends MyStream[A] {
    override def isEmpty: Boolean = head == Nil

    override def head: A = ???

    override def tail: MyStream[A] = ???

    override def #::[B >: A](element: B): MyStream[B] = ???

    override def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] = ???

    override def foreach(f: A => Unit): Unit = ???

    override def map[B](f: A => B): MyStream[B] = ???

    override def flatMap[B](f: A => MyStream[B]): MyStream[B] = ???

    override def filter(predicate: A => Boolean): MyStream[A] = ???

    override def take(n: Int): MyStream[A] = ???

    override def takeAsList(n: Int): List[A] = ???
  }
}
