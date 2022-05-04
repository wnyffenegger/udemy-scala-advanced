package lectures.section5

object FBoundedPolymorphism extends App {

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    // Want list of cats not animals
//    override def breed: List[Animal] = ???
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Dog] = ???
//  }

// Solution #1: Pray

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  // Manually specific it
//  class Cat extends Animal {
//    // Want list of cats not animals
//    override def breed: List[Cat] = ???
//  }
//
//  class Dog extends Animal {
//    // Compiler won't complain
//    override def breed: List[Cat] = ???
//  }


// Solution #2 F-Bounded Polymorphism

  // Used by ORMs
//  trait Animal[A <: Animal[A]] {    // recursive type: F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  // Manually specific it
//  class Cat extends Animal[Cat] {
//    // Want list of cats not animals
//    override def breed: List[Cat] = ???
//  }
//
//  // But still limited because it doesn't protect people from themselves
//  class Crocodile extends Animal[Cat] {
//    // Compiler won't complain
//    override def breed: List[Cat] = ???
//  }


// Solution #3 F-Bounded Polymorphism with self-types

//  trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism
//    self: A =>
//    def breed: List[Animal[A]]
//  }
//
//  // Manually specific it
//  class Cat extends Animal[Cat] {
//    // Want list of cats not animals
//    override def breed: List[Cat] = ???
//  }

  // Using a self type blocks illegal inheritance
//  class Crocodile extends Animal[Cat] {
//    // Compiler won't complain
//    override def breed: List[Cat] = ???
//  }

  // So sharks can breed with fish still
//  trait Fish extends Animal[Fish]
//  class Shark extends Fish {
//    override def breed: List[Animal[Fish]] = ???
//  }


// Solution #4 Implicits with type classes (foolproof)

//  // Use type classes
//  trait CanBreed[A] {
//    def breed(a: A): List[A]
//  }
//
//  trait Animal
//  class Dog extends Animal
//  object Dog {
//    implicit object DogsCanBreed extends CanBreed[Dog] {
//      def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] =
//      canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed
//
//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  // Compiler is pissed off because it doesn't match
//  val cat = new Cat
//  cat.breed


// Solution #5 pure type classes same as 4 perhaps cleaner (foolproof)

  trait Animal[A] {
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed
}
