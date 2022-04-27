package lectures.section2

object Monads extends App {

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /**
   * left-identity
   *
   * unit.flatMap(f) = f(x)
   * Attempt(x).flatMap(f) = f(x)
   * Success(x).flatMap(f) = f(x)
   *
   * right-identity
   *
   * attempt.flatMap(unit) = attempt
   * Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
   *
   * associativity
   *
   * attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g)
   * Fail(e).flatMap(f).flatMap(g) = Fail(e)
   * Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)
   *
   * Success(v).flatMap(f).flatMap(g) =
   *  f(v).flatMap(g) OR Fail(e)
   * Success(v).flatMap(x => f(x).flatMap(g)) =
   *  f(v).flatMap(g) or Fail(e)
   */

  val attempt = Attempt {
    throw new RuntimeException("whoo")
  }

  println(attempt)

  /**
   * 1) Implement own Monad that is lazy. Lazy[T] monad = computation which will only be executed when it's needed
   *
   * unit/apply
   * flatMap Lazy[T] => Lazy[S]
   *
   * 2) Monads = unit + flatMap
   *   Monads = unit + map + flatten
   *
   * Given an already implemented Monad how would you implement
   *
   * def map[B](f: T => B): Monad[B] = ???
   * def flatten(m: Monad[Monad[T]]): Monad[T] = ???
   */

  class Lazy[+A](value: => A) {

    // Prevent value from being evaluated multiple times
    private lazy val internalValue = value

    // Trigger evaluation of value
    def use: A = value

    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = {
// You must delay when the function evaluates the parameter to also be call by name otherwise
// first flatMap will eagerly evaluate function parameter
//    def flatMap[B](f: A => Lazy[B]): Lazy[B] = {
      f(internalValue)

      // if called this way then every time use is called this value will be re-evaluated
      // f(value)
    }


    // #2 flatMap makes map easy, given a function, apply the Monad to the function
    def map[B](f: (=> A) => B): Lazy[B] = flatMap(x => Lazy(f(x)))
//    // Flatten is trickier
//    def flatten(m: Lazy[Lazy[A]]): Lazy[A] = m.flatMap((x: Lazy[A]) => x)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = {
      new Lazy(value)
    }
  }

  val a = Lazy({
    println("This is hard")
    10
  })

  println("Already declared")
//  println(a.use)

  val b = a.flatMap(x => Lazy {
    10 * x
  })

  val c = a.flatMap(x => Lazy {
    20 * x
  })

  println("Did it work?")

  // Even when this works there is a problem. a is evaluated every time someone runs b.use or c.use
  // How to get it to a single evaluation? Use the lazy keyword
  println(b.use)
  println(b.use)
  println(c.use)
//  println(b.value)


  /**
   * Is this Lazy thing a Monad?
   *
   * left-identity
   * unit.flatMap(f) = f(x)
   * Lazy(v).flatMap(f) = f(v) f(v) must return a Lazy value
   *
   * right-identity
   * lazy.flatMap(Lazy) = lazy
   * Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)
   *
   * associativity
   * m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
   * Lazy(x).flatMap(f).flatMap(g) = Lazy(x).flatMap(x => f(x).flatMap(g))
   * Lazy(x).flatMap(f).flatMap(g) = f(v).flatMap(g)
   * Lazy(x).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g)
   *
   */
}
