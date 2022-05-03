package lectures.section5

object TypeMembers extends App {
  
  class Animal
  class Dog extends Animal
  class Cat extends Animal


  // Abstract type members mostly are around to help compiler do type inference
  class AnimalCollection {
    type AnimalType // abstract type member

    // Upper and lower bounds work
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal

  }

  val ac = new AnimalCollection

  // Doesn't really allow constructing or building anything
//  val dog: ac.AnimalType = ???
//  val cat: ac.BoundedAnimal = new Cat

  // Does allow us to use super bounded types
  val pup: ac.SuperBoundedAnimal = new Dog

  // Type aliases used for collisions when many packages are in use
  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat


  // Alternative to generics
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int

    override def add(element: Int): MyList = ???
  }


  // .type
  val cat = new Cat
  type CatsType = cat.type

  // Cannot do instantiation only association
//  new CatsType

  /**
   * Exercise - enforce a type to be applicable to SOME TYPES only
   */

  
}
