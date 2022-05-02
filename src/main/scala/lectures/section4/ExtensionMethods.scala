package lectures.section4

object ExtensionMethods extends App {

  case class Person(name: String) {
    def greet(): String = s"Hi, I'm $name, how can I help?"
  }

  extension (string: String) {
    def greetAsPerson(): String = Person(string).greet()
  }

  val danielsGreeting = "Daniel".greetAsPerson()

  // extension methods <=> implicit classes

  // Instead of this
  object Scala2ExtensionMethods {
    implicit class RichInt(val value: Int) extends AnyVal {
      def isEven: Boolean = value % 2 == 0

      def sqrt: Double = Math.sqrt(value)

      def times(f: () => Unit): Unit = {

        def timeAux(n: Int): Unit = {
          if (n <= 0) {}
          else {
            f()
            timeAux(n - 1)
          }
        }

        timeAux(value)
      }
    }
  }


  // Scala 3 style
  // Much clearer on what is going on
  // No autoboxing necessary
  extension(value: Int) {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)

    def times(f: () => Unit): Unit = {

      def timeAux(n: Int): Unit = {
        if (n <= 0) {}
        else {
          f()
          timeAux(n - 1)
        }
      }

      timeAux(value)
    }
  }

  // Allows you to use generic expressions
  // And using clauses
  extension [A](list: List[A]) {
    def ends: (A, A) = (list.head, list.last)
    def extremes(using ordering: Ordering[A]): (A, A) = list.sorted.ends // and even use extension method just declared
  }
}
