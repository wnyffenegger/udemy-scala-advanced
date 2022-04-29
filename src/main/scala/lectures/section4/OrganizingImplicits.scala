package lectures.section4

object OrganizingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  //  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  println(List(1,4,5,3,2).sorted)

  // Overrides the implicit

  // Note that sorted takes an implicit ordinal value
  // Where do these values come from?
  // scala.Predef

  /**
   * What things can be implicits?
   *
   * Implicits:
   *  - val/var
   *  - object
   *  - accessor methods = defs with no parentheses
   */

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )


//  implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, String]((p1: Person) => p1.name)
//  println(persons.sorted)

  // Will not work because it is not in scope for sorted, Fun does not participate in sorted method
//  object Fun {
//    implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, String]((p1: Person) => p1.name)
//  }

  // #1

  // Will work because Person is involved and we look through companion objects
//  object Person {
//    implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, String]((p1: Person) => p1.name)
//  }

  // # 2

  // Will work because Person is involved and age sort will override name sort
  //  object Person {
  //    implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, String]((p1: Person) => p1.name)
  //  }
  // implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, Int]((p1: Person) => p1.age)


  /**
   * Implicit scope ordered by priority
   *  - normal scope = LOCAL SCOPE (where we write our code)
   *  - imported scope
   *  - companion objects of all types involved in the method signature
   *    sorted example
   *      - List
   *      - Ordering
   *      - all the types involved = A or any supertype
   *
   */

  /**
   * Best practices
   *
   * 1) If there is a single possible value for it and you can edit the code for the type ->
   *      Define the implicit in a companion object
   * 2) If there are many possible values for it but a single good one and you can edit the code for the type
   *      Define the good value as an implicit in the companion object
   * 3) If there are many possible values and no single good value
   *      Define the values as implicits in custom companion objects
   */


   // # 3

//    object AlphabeticNameOrdering {
//      implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, String]((p1: Person) => p1.name)
//    }
//
//    object AgeOrdering {
//      implicit val alphaOrdering: Ordering[Person] = Ordering.by[Person, Int]((p1: Person) => p1.age)
//    }
//
//    import AgeOrdering._
//    println(persons.sorted)


  /**
   * Exercise.
   *
   * Online store and you want to make purchases.
   *
   * Create three different orderings
   *   - total price
   *   - unit count
   *   - unit price
   */

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.by[Purchase, Double]((p: Purchase) => p.nUnits * p.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.by[Purchase, Int]((p: Purchase) => p.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.by[Purchase, Double]((p: Purchase) => p.unitPrice)
  }

  val purchases = List(
    Purchase(2, 20),
    Purchase(10, 1),
    Purchase(1, 21)
  )

  // Change sort order by importing object

//  import UnitCountOrdering._
//  import UnitPriceOrdering._
  println(purchases.sorted)
}
