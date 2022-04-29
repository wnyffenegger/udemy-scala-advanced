package exercises

import lectures.section4.TypeClasses.HTMLSerializer


object EqualityPlayground extends App {

  case class User(name: String, age: Int, email: String)

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalityChecker: Equal[T]): Boolean =
      equalityChecker.apply(a, b)
  }

  implicit class EqualityEnrichment[T](value: T) {
    def ===(value2: T)(implicit equalityChecker: Equal[T]): Boolean = equalityChecker(value, value2)
    def !==(value2: T)(implicit equalityChecker: Equal[T]): Boolean = !equalityChecker(value, value2)
  }

  val john = User("John", 20, "john@rockthejvm.com")
  val anotherJohn = User("John", 45, "anotherJohn@rockthejvm.com")
  println(Equal(john, anotherJohn))
  println(john === anotherJohn)
  println(john !== anotherJohn)

  // So we're type safe
//  println(john === 43)


}
