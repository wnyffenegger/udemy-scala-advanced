package lectures.section4

object PimpMyLibrary extends App {


  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
  }

  // Annoying way to do this
  new RichInt(42).sqrt

  // Cool way to do this
  42.isEven

  /**
   * This technique is called Type Enrichment (also called pimping)
   */

  // Some examples
  1 to 10

  import scala.concurrent.duration._
  3.seconds

  /**
   * 1. Compiler searches for any implicits that can satisfy things
   * 2. Rewrite using implicit
   */

  /**
   * Conventions
   *
   * 1. On these implicit classes use AnyVal
   */

  // Limitations
  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  // Will not work it only goes 1 level deep with search
//  42.isOdd

  /**
   * Enrich the String class
   * - asInt
   * - encrypt
   *    "John" -> Lnjp
   *
   * Keep enriching the Int class
   *  - times(function)
   *    3.times()
   *  - * (multiply)
   *   3 * List(1, 2) = List(1, 2, 1, 2, 1, 2)
   */

  implicit class RichString(s: String) {
    private val possibleChars = "abcdefghijklmnopqrstuvwxyz"

    def asInt: Int = s.toInt
    def encrypt(offset: Int): String = {
      s.map((c: Char) => {
        val distance = possibleChars.indexOf(c.toLower)
        possibleChars((distance + offset) % possibleChars.length)
      })
    }
  }

  implicit class RichIntAgain(i: Int) {
    def times(f: () => Unit): Unit = {

      def timeAux(n: Int): Unit = {
        if (n <= 0) {}
        else {
          f()
          timeAux(n - 1)
        }
      }

      timeAux(i)
    }
    def *[T](t: List[T]): List[T] = {

      def timeAux(n: Int, acc: List[T]): List[T] = {
        if (n <= 0) acc
        else timeAux(n - 1, t ++ acc)
      }

      timeAux(i, List())
    }
  }

  println(3.times(() => println("Hello over and over")))
  println(3 * List(1, 2, 3))

  // "3" / 4
  // implicits support conversions (whoa)

  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2)

  // Auto convert
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  /**
   * Why implicit conversions are super dangerous
   */

  // This is incredibly hard to trace
  implicit def intToBoolean(i: Int): Boolean = i == 1

  if (10) println("It's 1")
  else println("It's not")

  /**
   * Keep type enrichment to implicit classes and type classes
   * Avoid implicit defs as much as possible
   * package implicits clearly, bring into scope only what you need
   * IF you need conversions, make them specific
   */
}
