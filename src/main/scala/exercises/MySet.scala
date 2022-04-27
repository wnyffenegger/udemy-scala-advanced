package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  // #2
  // Removing of an element
  // Intersection with another set
  // Difference with another set

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]

  // #3 Implement the negation of a set
  def unary_! : MySet[A]
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  override def contains(elem: A): Boolean = {
    if (head == elem) true
    else tail.contains(elem)
  }

  override def +(elem: A): MySet[A] =
    if(this.contains(elem)) this
    else new NonEmptySet(elem, this)

  /**
   * [1, 2, 3] ++ [4, 5] =
   * [2, 3] ++ [4, 5] + 1 =
   * [3] ++ [4, 5] + 1 + 2 =
   * [] ++ [4, 5] + 1 + 2 + 3
   *
   * [4, 5] + 1 + 2 + 3
   * [4, 5, 1]
   * [4, 5, 1, 2]
   * [4, 5, 1, 2, 3]
   *
   * @param anotherSet
   * @return
   */
  override def ++(anotherSet: MySet[A]): MySet[A] = {
    tail ++ anotherSet + head
  }

  /**
   * Same thing as previous one
   */
  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = {
    (tail flatMap f) ++ f(head)
  }

  override def filter(predicate: A => Boolean): MySet[A] =
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def -(elem: A): MySet[A] = {
    if (head == elem) tail
    else tail - elem + head
  }

  override def --(anotherSet: MySet[A]): MySet[A] = {
    filter(x => !anotherSet(x))
  }

  override def &(anotherSet: MySet[A]): MySet[A] = {
    filter(x => anotherSet(x))
  }

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

class EmptySet[A] extends MySet[A] {

  override def contains(elem: A): Boolean = false

  override def +(elem: A) = new NonEmptySet(elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = {}

  override def -(elem: A): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => true)
}


/**
 * Better option is a property based set that allows filtering etc.
 *
 * All elements satisfying a property {x in A | property(x) }.
 *
 * We're going to mathematically define a set using Scala, all changes to the set will manifest
 * as changes to the property of the set.
 *
 * @param property
 * @tparam A
 */
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)


  // {x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || elem == x)

  // {x in A | property(x) || x in anotherSet}
  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet.contains(x) )

  override def map[B](f: A => B): MySet[B] = politelyFail
  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valuesSequence: Seq[A], accumulator: MySet[A]): MySet[A] =
      if (valuesSequence.isEmpty) accumulator
      else buildSet(valuesSequence.tail, accumulator + valuesSequence.head)

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object TestingGrounds extends App {

  val set = MySet(1, 2, 3, 4)

  set.foreach(println)

  set.map(_ * 2).foreach(println)

  set.flatMap(x => MySet(x, x * 10)).foreach(println)

  var added = set + 3
  added.foreach(println)
  added = set + 6
  added.foreach(println)

  added ++ MySet(8, 9) foreach(println)

  added filter(_ % 2 == 0) foreach(println)


  // Testing property based set
  val negative = !set
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(7))
  println(negativeEven(8))

  val negativeEven7 = negativeEven + 7
  println(negativeEven7(7))

  val intersection = negativeEven & MySet(2, 4, 6)
  println(intersection(2))
  println(intersection(6))
}