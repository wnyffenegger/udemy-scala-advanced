package exercises

import scala.annotation.tailrec

object StreamsPlayground extends App {

  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    //prepend operator
    def #::[B >: A](element: B) : MyStream[B]
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A]
    def takeAsList(n: Int): List[A] = take(n).toList()

    @tailrec
    final def toList[B >: A](acc: List[B] = Nil): List[B] = {
      if (isEmpty) acc
      else tail.toList(acc :+ head)
    }
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] =
      new Stream(start, MyStream.from(generator(start))(generator))
  }

  object EmptyStream extends MyStream[Nothing] {
    override def isEmpty: Boolean = true

    override def head: Nothing = throw new NoSuchElementException()

    override def tail: MyStream[Nothing] = throw new NoSuchElementException()

    override def #::[B >: Nothing](element: B): MyStream[B] = new Stream[B](element, this)

    override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

    override def foreach(f: Nothing => Unit): Unit = ()

    override def map[B](f: Nothing => B): MyStream[B] = this

    override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

    override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

    override def take(n: Int): MyStream[Nothing] = this
  }

  class Stream[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
    override def isEmpty: Boolean = false

    override val head: A = hd

    override lazy val tail: MyStream[A] = tl // Use CALL BY NEED

    override def #::[B >: A](element: B): MyStream[B] = new Stream(element, this)

    override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Stream(head, tail ++ anotherStream)

    override def foreach(f: A => Unit): Unit =
      f(head)
      tail.foreach(f)

    override def map[B](f: A => B): MyStream[B] = new Stream(f(head), tail.map(f))

    override def flatMap[B](f: A => MyStream[B]): MyStream[B] =
      f(head) ++ tail.flatMap(f)

    override def filter(predicate: A => Boolean): MyStream[A] =
      if (predicate(head)) new Stream(head, tail.filter(predicate))
      else tail.filter(predicate)

    override def take(n: Int): MyStream[A] =
      if (n <= 0) EmptyStream
      else if (n == 1) new Stream(head, EmptyStream)
      else new Stream(head, tail.take(n - 1))
  }


  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val intermediate = naturals.map(_ * 2).filter(_ % 4 == 0).take(10)

  println(intermediate.toList())

  // printing out list in previous steps should not alter contents in stream
  intermediate.take(1).foreach(println)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)

//  startFrom0.take(10000).foreach(println)

  println(startFrom0.flatMap(x => new Stream(x, new Stream(x + 1, EmptyStream))).take(100).toList())
//  println(startFrom0.flatMap(x => new Stream(x, new Stream(x + 1, EmptyStream))).take(50000).toList())

  // Filter doesn't guarantee that stream is finite
//  println(startFrom0.filter(_ < 10).toList())

  // Still crashes if you take more elements than pass condition
//  println(startFrom0.filter(_ < 10).take(11).toList())

  println(startFrom0.filter(_ < 10).take(10).toList())

  // This is fine because taking 20 from 10 will result in 10 elements
  println(startFrom0.filter(_ < 10).take(10).take(20).toList())

  /**
   * 1 - stream of fibonacci numbers
   * 2 - stream of prime numbers Eratosthenes sieve
   *
   * [2, 3, 4 ...]
   * filter out all numbers divisible by 2
   * [ 2 3 5 7 9 11 ... ]
   * filter out all numbers divisible by 3
   * [ 2 3 5 7 11 13 17]
   * filter out all numbers divisible by 5
   */

  def fibonacci(first: Int, second: Int): MyStream[Int] = {
    new Stream(first, fibonacci(second, first + second))
  }

  println(fibonacci(1, 1).take(100).toList())

  def sieve(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Stream(numbers.head, sieve(numbers.tail.filter(_ % numbers.head != 0)))
  }

  println(sieve(MyStream.from(2)(_ + 1)).take(100).toList())
}
