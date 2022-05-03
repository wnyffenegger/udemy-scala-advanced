package lectures.section5

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner): Unit = println(i)

    def printGeneral(i: Outer#Inner): Unit = println(i)
  }

    def aMethod: Int = {
      class HelperClass
      type HelperType = String

// Not allowed
//      type ExtraHelpfulType
      2
    }

    // In order to reference an inner type you will need an instance of the outer type
    val outer = new Outer
//    val inner = new Inner
//    val inner = new Outer.Inner
    val inner = new outer.Inner

  // These Inner classes are different types because they are parameterized by the instance of the outer class
  val oo = new Outer
//  val otherInner: oo.Inner = new o.Inner

  // Types are not the same so method won't work
  outer.print(inner)
//  oo.print(inner)

  // This kind of dependency is called a path-dependent types

  // Type of Inner is actually Outer#Inner
  // Now this will compile
  outer.printGeneral(inner)
  oo.printGeneral(inner)

  /**
   * Exercise
   *
   * DB keyed by Int or String
   */
}
