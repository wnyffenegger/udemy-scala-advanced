package lectures.section2

object FunctionalCollection extends App {


  // Functional sequences
  // Sequences are "callable" through an integer index

  // Sequences
  val numbers = List(1, 2, 3)
  numbers(1)
  numbers(3) // throws exception

  // Sequences are partial functions
  // Defined on domain [0 , len(sequence) - 1]
  // Undefined outside that

  val phoneMappings = Map(2 -> "ABC", 3 -> "DEF")
  phoneMappings(2)
  phoneMappings(1) // NoSuchElementException

  // Maps are also partial functions
  // Defined on domain of all keys
  // undefined outside of that
}
