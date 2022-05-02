package lectures.section4

object Givens extends App {
  val aList = List(2, 4, 3, 1)

  object Implicits {
    implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  // Scala 2 style

  import Implicits._

  val anOrderedList = aList.sorted
  println(anOrderedList)

  // Scala 3 style
  //  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  // givens <=> equivalent to implicit vals
  //  meant to help simplify implicits, implicit defs still remain

  object Givens {
    given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  object GivenAnonymousClassNaive {
    given descendingOrdering_v2: Ordering[Int] = (x: Int, y: Int) => y - x
  }

  // Proper way for defining given instances for traits where you need to override a bunch of methods
  object GivenWith {
    given descendingOrdering_v3: Ordering[Int] with {
      override def compare(x: Int, y: Int) = y - x
    }
  }

  import GivenWith._ // In Scala 3, this import does NOT import givens as well
  // Why? To make sure implicits/given are explicitly denoted

  // Accepted way of importing givens
  import GivenWith.given

  println(anOrderedList)

  // What about givens in Scala 3?

  // Scala 2 version
  def extremes[A](list: List[A])(implicit ordering: Ordering[A]): (A, A) = {
    val sortedList = list.sorted
    (sortedList.head, sortedList.last)
  }

  // Exact same meaning as implicit argument
  def extremes_v2[A](list: List[A])(using ordering: Ordering[A]): (A, A) = {
    val sortedList = list.sorted
    (sortedList.head, sortedList.last)
  }

  // implicit def (synthesize new implicit values)
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  /**
   * Scala 2 Version
   *
   * Implicit def is the ability of the compiler to synthesize new list orderings based on an already existing ordering
   * for single elements in the list and a combinator you introduce. Basically, given a combinator, any type with
   * a pre-existing ordering will also have a list ordering.
   */
  implicit def listOrdering[A](implicit simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]]
    = new Ordering[List[A]] {
    override def compare(x: List[A], y: List[A]) = {
      val sumX = x.reduce(combinator.combine)
      val sumY = y.reduce(combinator.combine)

      simpleOrdering.compare(sumX, sumY)
    }
  }

  given listOrdering_v2[A](using simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]): Int = {
      val sumX = x.reduce(combinator.combine)
      val sumY = y.reduce(combinator.combine)

      simpleOrdering.compare(sumX, sumY)
    }
  }

  // Implicit defs were abused to use for conversions
  // In Scala 3 an implicit conversion is much harder


  // Compiler invokes string to person and then calls greet on the person
  // EVIL, EVIL, EVIL
  // Horrible to debug

  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }

  implicit def string2Person(string: String): Person = Person(string)
  val danielsGreet = "Daniel".greet()

  // Scala 3 blocks this
  import scala.language.implicitConversions // required
  given string2PersonConversion: Conversion[String, Person] with {
    override def apply(x: String): Person = Person(x)
  }
}


