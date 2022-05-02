package lectures.section5

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // Variance is the problem of "inheritance" - type substitution, of generics

  // Should Cage[Cat] inherit from Cage[Animal] ?
  class Cage[T]

  // 1) Yes = covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // 2) No = invariance
//  class ICage[T]
//  val icage: ICage[Animal] = new ICage[Cat]

  // 3) hell no - opposite = contravariance

  // Replace specific cage of cats with a general cage of animals
  // Works if animal cage works on everything
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val Animal: T) // COVARIANT position
                                          // In this field the compiler accepts covariant types
                                          // Also accepts invariant types
                                          // But does not accept contravariant types

  // contravariant type T occurs in covariant position in type => T of value animal
//  class ContravariantCage[-T](val animal: T)

  // Why the above error, because then you could do the following...
//  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)

  // covariant type t occurs in contravariant position in type => T of variable animal
//  class CovariantVariableCage[+T](var animal: T)

  // Why the above error, because then you could do the following
//  val cCage: CCage[Animal] = new CCage[Cat](new Cat)
//  cCage.animal = new Crocodile

//  class ContravariantVariableCage[-T](var animal: T) // also in covariant position
//  val CatCage: XCage[Cat] = new XCage[Animal](new Crocodile)

  class InvariantVariableCage[T](var animal: T) // ok


//
//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T) // Contravariant Position
//  }
//
//  val ccage: CCage[Animal] = new CCage[Dog]
//  ccage.add(new Cat)

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
//  acc.addAnimal(new Animal)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)


  // Widening the type
  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B]
  }

  // Compiler won't be happy unless we allow the type to widen with [B >: A]
  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)

  // This would be a problem if we couldn't promote to super types
  val evenMoreAnimals = moreAnimals.add(new Dog)


  class PetShop[-T] {
//    def get(isItApuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION

//      val catShop = new PetShop[Animal] {
//        def get(isItAPuppy: Boolean): Animal = new Cat
//      }
//      val dogShop: PetShop[Dog] = catShop
//      dogShop.get(true) // returns a Cat even

    def get[B <: T](isItApuppy: Boolean, defaultAnimal: B): B = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
//  val cat = shop.get(true, new Cat) // Won't work
  class TerraNova extends Dog
  val biGurry = shop.get(true, new TerraNova)




  // val fields in classes must be invariant or covariant
  // var fields in classes must be invariant because they are in both covariant and contravariant position
  // Method arguments must be contravariant or invariant because they are in contravariant position ***
  // Method return values must be covariant or invariant because they are in covariant position

  /**
   * Rules of thumb
   * - use covariance = COLLECTION OF THINGS
   * - use contravariance = GROUP OF ACTIONS
   */

  /**
   * Parking[T](things: List[T])
   *  park(vehicle: T)
   *  impound(vehicle: List[T])
   *  checkVehicles(conditions: String): List[T]
   *
   * 1. Invariant, covariant, and contravariant
   *
   * 2. What if we used an invariant list (IList[T]) instead of covariant list?
   * 3. Parking = monad!
   *    Add flatMap method
   *
   */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class Convertible extends Car

// 1

//  abstract class Parking[T](things: List[T]) {
//    def park(vehicle: T): Parking[T]
//    def impound(vehicles: List[T]): Parking[T]
//    def checkVehicles(condition: String): List[T]
//  }

  // Covariant
//  abstract class Parking[+T](things: List[T]) {
//    def park[S >: T](vehicle: S): Parking[S]
//    def impound[S >: T](vehicles: List[S]): Parking[S]
//    def checkVehicles(condition: String): List[T]
//  }
//
//
//  // Contravariant
//  abstract class Parking[-T](things: List[T]) {
//    def park[S <: T](vehicle: S): Parking[T]
//    def impound(vehicles: List[T]): Parking[T]
//    def checkVehicles[S <: T](condition: String): List[S]
//  }

// 2

  // Covariant does not change
  class IList[T]
//  abstract class Parking[+T](things: IList[T]) {
//    def park[S >: T](vehicle: S): Parking[S]
//    def impound[S >: T](vehicles: IList[S]): Parking[S]
//    def checkVehicles[S >: T](condition: String): IList[S]
//  }

//  abstract class Parking[-T](things: IList[T]) {
//    def park[S <: T](vehicle: S): Parking[T]
//    def impound[S <: T](vehicles: IList[S]): Parking[T]
//    def checkVehicles[S <: T](condition: String): IList[S]
//  }

// 3

//  abstract class Parking[T](things: List[T]) {
//    def park(vehicle: T): Unit
//    def impound(vehicles: List[T]): Unit
//    def checkVehicles(condition: String): List[T]
//
//    def flatMap[S](f: T => Parking[S]): Parking[S]
//  }

  // Covariant
//  abstract class Parking[+T](things: List[T]) {
//    def park[S >: T](vehicle: S): Unit
//    def impound[S >: T](vehicles: List[S]): Unit
//    def checkVehicles(condition: String): List[T]
//
//    def flatMap[S](f: T => Parking[S]): Parking[S]
//  }


  // Contravariant
  abstract class Parking[-T](things: List[T]) {
    def park[S <: T](vehicle: S): Unit
    def impound(vehicles: List[T]): Unit
    def checkVehicles[S <: T](condition: String): List[S]

    def flatMap[R, S <: T](f: S => Parking[R]): Parking[R]
  }

}
