


## Section 2

### Syntax Capabilities

The moral of all of this is that Scala provides many ways to write more concise and readable code.

There are both pros and cons to this:

* The flexibility means that you can write things more simply
* The flexibility means you have to be able to recognize code written more ways
* Shortening code with syntax hacks does not necessarily make it more readable

1. Methods with a single parameter
```scala
import scala.util.Try
def singleArgMethod(arg: Int): String = s"$arg little ducks"

// curly braces syntax for the first parameter for an expression
val description = singleArgMethod {
  42
}

// Like Java's try but still an expression
// apply method from Try
val aTryInstance = Try {
  throw new RuntimeException
}
```
2. Single abstract method can be written extremely concisely as an anonymous function.
```scala
trait Action {
  def act(x: Int): Int
}

// you could do this
val anInstance: Action = new Action {
  override def act(x: Int): Int = x + 1
}

// converts lambda to single abstract type
val betterInstance: Action = (x: Int) => x + 1

// example is with Runnables
val aThread = new Thread(new Runnable {
  override def run(): Unit = println("stuff")
})

// better
val aBetterThread = new Thread(() => println("stuff"))

abstract class AbstractType {
  def implemented: Int = 23
  def f(a: Int): Unit
}

// Just syntactic sugars
val anAbstractInstance: AbstractType = (a: Int) => println("sweet")
```
3. `::` and `#::` are special methods that control right vs. left associative
```scala
  val prependedList = 2 :: List(3, 4)
  // 2.::(List(3,4))

  // scala spec: last char decides associativity of the method
  // if ends in a colon then right associative, so the operators are written in reverse order
  // else left associative
  1 :: 2 :: List(3) // is compiled to
  List(3).::(2).::(1)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation here
  }
  val myStream = 1 -->: 2 -->: 3 -->: 3 -->: new MyStream[Int]
```
4. Multi-word method naming
```scala
  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }
  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is great!"
```
5. Infix types
```scala
  class Composite[A,B]
  val composite: Int Composite String = ???

  class -->[A,B]
  val towards: Int --> String = ???
```
6. update() method is special like apply and is the standard for mutable collections
```scala
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten anArray.update(2, 7)
  // used in mutable collections
```
7. Implementation of OO Encapsulation via setters for mutable members 
```scala
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member = internalMember
    def member_=(value: Int): Unit = {
      internalMember = value
    }
  }

  val mutable = new Mutable
  mutable.member = 10
  //rewritten as mutable.member_=(10)

```

### Advanced Pattern Matching

You can define functions that do pattern matching as singletons and add those for more complex matches.

Use cases:

1. Unpacking complex classes so you can apply conditions to them
2. Encapsulating cases that are used often for pattern matching

How it works:
1. Pattern called Person with a name and an age
2. Look for a method called unapply on a companion object and look for a tuple
3. Call unapply if value is Some and not None evaluate case
4. Errors out if unapply returns None or no match found

Features:

1. Standardized on the unapply method
2. What formats work? Both Option and regular return types work here.
3. Can take a variable number of arguments
4. Can lead to match errors if cases do not match any of the patterns encapsulated by functions
```scala

    // Make this compatible with pattern matching without a case class
    class Person(val name: String, val age: Int)
    
    // define a companion object
    // define a special method of unapply
    object PersonPattern {
      // deconstruct object
      def unapply(person: Person): Option[(String, Int)] =
        if (person.age < 21) None
        else Some((person.name, person.age))
    
      def unapply(age: Int): Option[String] =
        Some(if (age < 21) "minor" else "major")
    }

    val bob = new Person("Bob", 25)

    // So it unpacks the class using unapply

    // Steps
    // 1 Pattern called Person with a name and an age
    // 2 Look for a method called unapply on a companion object and look for a tuple
    // 3 Call unapply if value is Some and not None evaluate case
    // 4 Errors out if unapply returns None or no match found
    val greeting = bob match {
      case PersonPattern(name, age) => s"Hi my name is $name and I am $age years old"
    }
    println(greeting)

    val legalStatus = bob.age match {
      case PersonPattern(status) => s"My legal status is $status"
    }
    println(legalStatus)

  object even {

    // This option will return a value that can be used for further processing
//    def unapply(arg: Int): Option[Boolean] = {
//      if (arg % 2 == 0) Some(true)
//      else None
//    }

    // Interpreted as Boolean tests
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }
```

### Advanced Pattern Matching Part II


1. Infix patterns: specific return types can be pattern matched using infix notation. The notation allows you to read
the pattern match as a sentence.
```scala
case class Or[A, B](a: A, b: B)
val either = Or(2, "two")
val humanDescription = either match {
case number Or string => s"$number is written as $string"
//    case Or(number, string) => s"$number is written as $string"
}
println(humanDescription)
```
2. Decomposing sequences. To match sequences with multiple entries we often need to convert subclasses of sequences
into `Seq`. To do that there is also an `unapplySeq` method that requires us to unwrap a `List` or some other type
into a `Seq`
```scala
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyListPattern {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyListPattern(1, 2, _*)  => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)
```
3. Trait for pattern matching return types. All pattern matching expressions essentially implement
a standard trait with two methods: isEmpty and get (like an optional).
```scala
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = {
      new Wrapper[String] {
        def isEmpty = person.name == null
        def get = person.name
      }
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person is $name"
    case _ => s"Unknown name"
  })
```

## Advanced Functional Programming

### Partial Functions

```scala
trait PartialFunction[A,B] {
  def apply(x: A): B
  def isDefinedAt(x: A): Boolean
}
```

Partial functions are functions defined only over part of a domain (recap).

Scala gives us special traits and syntactic sugar to define partial functions effectively.


1. Scala defines the PartialFunction trait
2. Scala allows chaining partial functions `partialFunction.orElse(secondPartialFunction)`
3. Scala allows converting a partial function to a total function which returns an `Option` ex. `partialFunction.lift`
4. Scala provides `isDefinedAt` to determine if a partial function has amapping for some input

Under the hood partial functions are a subtype of total functions which means they can be use d in standard higher
order functions like `map, flatMap, forEach` etc.

A set of examples related to a partial function
```scala
  val aManualPartialFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }

    override def isDefinedAt(x: Int) = {
      x == 1 || x == 2 || x == 5
    }
  }

    val aMappedList = List(1, 2, 3).map {
      case 1 => 42
      case 2 => 78
      case 3 => 1000
    }

  val chatbot: PartialFunction[String, String] = {
    case "Hello" => "How are you"
    case "Goodbye" => "Have a nice day"
  }

  scala.io.Source.stdin.getLines().map(chatbot)
    .foreach(println)
```

### Functional Collections

Objects do exist in scala but collections should not be treated as objects, collections need to be treated as functions.

When you treat a collection as a function more interesting concepts begin to show up:

1. Can you define a collection with a mathematical function?
2. If you define a collection with a function can you compose it with other functions?
3. What happens if a collection is infinite and you want to call `map` or some other iterable function?
4. Can you tell if a collection is infinite ahead of time?
5. Can you define generators for collections using pure functions?

Example functional collection:

In the example you can see how a property (mathematical function) can be composed and modified.

For more see `MySet.scala`

```scala
trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  // #2
  // Removing of an element
  // Intersection with another set
  // Difference with another set

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]

  // #3 Implement the negation of a set
  def unary_! : MySet[A]
}

class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)


  // {x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || elem == x)

  // {x in A | property(x) || x in anotherSet}
  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet.contains(x) )

  override def map[B](f: A => B): MySet[B] = politelyFail
  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}
```

### Moral of Functional Collections

Collections are generally **partial functions**. They are defined on some domain (indices of list, keys in map)
and undefined outside that. This mentality is different from the OOP notion of a collection.

### Curried Partially Applied Functions

Lessons:

1. Lifting = ETA-expansion which converts a method to a Function due to JVM limitations
2. Lifting curried functions occurs automatically in `.map, .flatMap, etc.`
3. Using an underscore triggers lifting ex. `curriedAddMethod(7) _`
4. Reminder you can curry a function with `simpleAddFunction.curried(7)`
5. Underscores can be used to replace function parameters and create a curried function ex. `val insertName = concatenator("Hello, I'm ", _, "how are you?")`

Call by function vs. call by name:

1. Parameterless methods != Methods with parameters
2. Parameterless methods cannot be called as functions or lifted
3. Functions with parameters cannot be passed by name and implicitly converted to values

```scala
  def byName(n: Int) = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(method) // Okay
  byFunction(method) // Not okay case #2

  byFunction(parenMethod()) // Okay
  byName(parenMethod) // Not okay case #3
```

### Lazy Evaluation

By default Scala functional calls are not lazy. We need to use the `lazy` keyword to trigger that behavior.

1. `lazy` says compute when I need this and only when I need this. Ex. `lazy val x: Int = throw new RuntimeException` will not throw exception until someone uses `x`
2. Use lazy for **call by need**. Instead of computing a value multiple times, putting `lazy` on the val will force it to only be computed once.
```scala
  def byNameMethod(n: => Int): Int = {
    // Call by name will trigger calculation of value three times
    // So switch to lazy with CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }

    def retrieveMagicValue: Int = {
      // side effect or a long computation
      println("Waiting")
      Thread.sleep(1000)
      42
    }
    
    println(byNameMethod(retrieveMagicValue))
```
3. For comprehensions are lazy by default which is great for handling Future and other async things
4. Putting lazy on functions delays calculation
5. If you want something to be lazy all of the things it calls need to be lazy and vice versa.


### Monads

```scala

trait MonadTemplate[A] {
  def unit(value: A): MonadTemplate[A]    // Also called pure or apply
  def flatMap[B](f: A => MonadTemplate[B]): MonadTemplate[B]  // also called bind
}
```

All monads must satisfy the following:

1. Left-identity: `unit(x).flatMap(f) == f(x)`
2. Right-identity: `aMonadInstance.flatMap(unit) == aMonadInstance`
3. Associativity: `m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))`

Things that are monads:

1. List
2. Option
3. Future
4. Try
5. Stream
6. Set

The tricky part about monads: how do you make them lazy? **Rely on call by name and call by need**

1. Use `lazy val` to delay the evaluation of values provided to a Monad
2. Use `=>` to tell the compiler that a parameter is call by name
3. All parameters should be call by name including the parameters in functions passed
to the Monad for `flatMap`. 

Example from course:
```scala
class Lazy[+A](value: => A) {

  // Prevent value from being evaluated multiple times
  private lazy val internalValue = value

  // Trigger evaluation of value
  def use: A = value

  // this version flatMap will eagerly evaluate function parameter
  //    def flatMap[B](f: A => Lazy[B]): Lazy[B] = ...
  // You must delay when the function evaluates the parameter to also be call by name otherwise
  // the compiler will evaluate the parameter as soon as it is provided
  def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = {

    f(internalValue)

    // if called this way then every time "use" is called this value will be re-evaluated
    // f(value)
  }
}
```

## Futures

### Basics

A future is a wrapper around an asynchronous action that returns a `future.value` is of type `Try[T]`.

Futures have lots of methods on them for handling the results of an action either synchronously or
asynchronously.

Some examples:

1. onComplete: run a callback when a future completes, the callback must handle both success and failure
2. recover: on failure of a future recover using a default value
3. recoverWith: like recover but calls another future
4. Await.result(future) allows waiting for a future to finish

```scala
import scala.concurrent.{Awaitable, ExecutionContext}
import scala.util.Try

trait Future[+T] extends Awaitable[T] {
  
  def onComplete[U](f: Try[T] => U)(implicit executor: ExecutionContext): Unit
  
  def isCompleted: Boolean
  
  def value: Option[Try[T]]

  // etc.
```


### Important Things About Futures

1. Futures can be chained
2. Futures can be blocked on
3. For comprehensions block on futures
4. Futures are Monads and so flatMap blocks on a future until it is completed

### Promises

Promises are like futures but allow creating contracts. I will do this. A promise contains a future which can
be used with callbacks and methods to handle the eventual results. Kind of like JavaScript Promises.

Example:
```scala
import scala.concurrent.Promise
import scala.util.Success

  val promise = Promise[Int]()
  val future = promise.future

  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
  }

  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)
```


## Implicits

Implicits are methods, values, and accessors "looked up" by the compiler at compile time but not
explicitly defined for a type in that type's definition.

Example: 
```scala
  // -> is not defined for Strings
  // Compiler looks for a definition of -> at compile time and adds it
  val pair = "Daniel" -> "555"
```

### The Basics

What things can be implicits?

1. val/var default values for things
2. objects like singletons
3. accessor methods (methods with no parentheses)

What order are implicits discovered in?

1. First look at normal scope (local scope where code is written)
2. Look at imported scope
3. Look at companion objects for all types involved in the method signature

Best Practices

1. If there is a single possible value for an implicit -> Define the implicit in a companion object
2. If there are many possible values for the implicit, but a single good one -> Define the good value as an implicit in the companion object
3. If there are many possible values and no single good value -> Define the values as implicits in custom objects


### Type Classes

Implicits let you use type classes seamlessly across your code.

What is a type class? A type class defines a method/behavior available to all classes that implement that type.

The standard pattern with Scala is:

1. Create a trait with the behavior you want to implement
2. Create a companion object for that trait and add an apply method calling the behavior
3. Extend the trait with the desired classes you want to call that behavior
4Call that behavior with `MyTypeClassTemplate(args..)`

Example:
```scala
object Test {
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](value: T)(implicit instance: MyTypeClassTemplate[T]): String = instance.action(value)
  }

  case class FancyOrNot(fancy: Boolean)

  // Add a default implicit definition
  // Define additional objects locally to get custom behavior
  implicit object FancyOrNot extends MyTypeClassTemplate[FancyOrNot] {
    def action(value: FancyOrNot): String = if (value.fancy) "posh" else "not posh"
  }

  println(MyTypeClassTemplate(FancyOrNot(false)))
}
```

Why do we like this?

1. Preserves type safety and independence of classes that implement a trait
2. Implement as many times as you use it and in different ways for each type
4. Apply sensible defaults via implicit classes
5. Override those defaults with additional implicit classes
6. Minimum repetition possible

### Ad Hoc Polymorphism

Two distinct or unrelated types can have methods called as long as those methods are defined implicitly. Those methods
can do different things and can even be overridden whenever we want.

Compiler makes sure that we use the correct type class implementation of the trait.

**Ad Hoc Polymorphism gives us Type Enrichment**

### Type Enrichment

Ad Hoc polymorphism allows you to modify types you do not have access to the source code for.

Many of the syntactic sugars available in Scala are built off modifying types with implicits.

Example:
```scala
object Test {
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)
  }

  // Cool way to do this
  42.isEven
}
```

## Scala's Type System

### Diamond Problem

Scala resolves the diamond problem by picking the last override. If there are two mixins inheriting
from the same trait the last one extended will be included

```scala
  trait Animal { def name: String }
  trait Lion extends Animal {
    override def name: String = "LION"
  }
  trait Tiger extends Animal {
    override def name: String = "TIGER"
  }
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)

  // prints out TIGER
```

### Inheritance via super

Scala's inheritance linearizes stuff so `super` in Scala does not mean the same thing as in Java. Super moves to
the left in the type linearization.

1. Scala prevents an overrode type from being included twice by discovering them in order
2. Scala backtracks from the last up, but this is not the same as the order the types are discovered in
3. The backtrack order follows linearized path, not inheritance of each class

In the example:

1. Cold = AnyRef with <Cold>
2. Green = AnyRef with <Cold> with <Green>
3. Blue = AnyRef with <Cold> with <Blue>
4. Red = AnyRef with <Red>
5. White = AnyRef with <Red> with ~~AnyRef~~ with <Cold> with <Blue> with ~~AnyRef~~ with ~~<Cold>~~ with <Green> with <White>

In the example backtracking:

1. println("white")
2. super.print() - looks to left so it hits Green
3. println("green")
4. super.print() - looks to left so it hits Blue
5. println("blue")
6. super.print() - lookks to left so it hits green

```scala
  trait Cold {
    def print: Unit = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print: Unit = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  val color = new White
  println(color.print)

  // Prints out
  // white
  // blue
  // green
  // cold
```

### Variance Types in Scala

1. Covariance: subtype replaces supertype
2. Invariance: types must exactly match
3. Contravariance: super type replaces subtype

These types of variance are applied to more than generics, these types of variance apply to variance
aspects of Scala code. The compiler must limit the variance allowed for class fields, method return types, etc.
to prevent nonsensical compilation

Example:
```scala
trait Animal
trait Cat extends Animal

// 1 Covariance
class CCage[+T]
val ccage_1: CCage[Animal] = new CCage[Cat]
val ccage_2: CCage[Cat] = new CCage[Cat]
//val ccage: CCage[Cat] = new CCage[Animal]

// 2 Invariance
class ICage[T]
//val icage: ICage[Animal] = new ICage[Cat]
val icage: ICage[Cat] = new ICage[Cat]
//val icage: ICage[Cat] = new ICage[Animal]

// 3 Contravariance
class XCage[-T]
//val xcage: XCage[Animal] = new XCage[Cat]
val xcage_1: XCage[Cat] = new XCage[Cat]
val xcage_2: XCage[Cat] = new XCage[Animal]
```

### Variance Positions Enforced by Compiler

Specific aspects of scala code like class fields and method arguments have specific variance types.

1. Class `val` fields are in covariant position (so only covariant and invariant accepted)
2. Class `var` fields are in covariant and contravariant position (so only invariant accepted)
3. Method arguments are in contravariant position (so only contravariant and invariant accepted)
4. Method return types are in covariant position (so only covariant and invariant accepted)

#### Class `val` types

All `val`'s passed into Scala classes are covariant. A contravariant val would allow you to replace a super type
with an arbitrary subtype.

Becuase of this only covariant and invariant args can be passed as class fields

```scala

trait Animal
trait Cat extends Animal
trait Crocodile extends Animal

// COVARIANT position
// In this field the compiler accepts covariant types
// Also accepts invariant types
// But does not accept contravariant types
class CovariantCage[+T](val Animal: T)

  // contravariant type T occurs in covariant position in type => T of value animal
//  class ContravariantCage[-T](val animal: T)

  // Why the above error, because then you could do the following...
//  val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
```

#### Class `var` types

Because a `var` can be modified, they are in both covariant and contravariant position. When this occurs, the only
legal overlap of the two is an invariant type.

```scala
  trait Animal
  trait Cat extends Animal
  trait Crocodile extends Animal
  
  // covariant type t occurs in contravariant position in type => T of variable animal
  class CovariantVariableCage[+T](var animal: T)

  // Why the above error, because then you could do the following
  val cCage: CovariantVariableCage[Animal] = new CovariantVariableCage[Cat](new Cat)
  cCage.animal = new Crocodile

  // contravariant type t occurs in covariant position in type => T of variable animal
  class ContravariantVariableCage[-T](var animal: T) // also in covariant position
  val CatCage: ContravariantVariableCage[Cat] = new ContravariantVariableCage[Animal](new Crocodile)
```

#### Method Argument Types

Method arguments are in contravariant position to prevent someone from casting a subtype to a supertype and replacing
with another arbitrary subtype.

```scala
class Animal
class Cat extends Animal
class Dog extends Animal

// Compiler will not allow because covariant arg in contravariant position
class CovariantCage[+T] {
  def addAnimal(animal: T) = true
}

// If compiler allowed, this would work
val ccage: CovariantCage[Animal] = new CovariantCage[Dog]
ccage.addAnimal(new Cat)


// Compiler wants contravariant args
class ContravariantCage[-T] {
  def addAnimal(animal: T) = true
}
val acc: ContravariantCage[Cat] = new ContravariantCage[Animal]
acc.addAnimal(new Cat)

// Contravariant arg so this won't work
//acc.addAnimal(new Animal)

class Kitty extends Cat
acc.addAnimal(new Kitty)
```

#### Method Return Types

Method return types are in covariant position. This means that return types must be invariant or a super type of the
provided class.

```scala
class Animal
class Cat extends Animal
class Dog extends Animal

// If method return types could be contravariant 
abstract class ContravariantPetShop[-T] {
  def get(isItApuppy: Boolean): T
}

// Then we could do the following
val catShop: ContravariantPetShop[Animal] = new ContravariantPetShop[Animal] {
  override def get(isItApuppy: Boolean): Animal = new Cat
}

val dogShop: ContravariantPetShop[Dog] = catShop
dogShop.get(true) // and return a cat!!
```


#### Getting around variance limits

Get around variance limits by telling the compiler to widen or narrow a type appropriately.


##### Method Argument Types Example
```scala
// Method argument element is now of type B which is a super type of A, so if you pass an A everything
// is good because that is contravariant. By widening the type first we preserve contravariance on the method
// argument.
class MyList[+A] {
  def add[B >: A](element: B): MyList[B] = new MyList[B]
}
```

##### Method Return Types Example
```scala
class Animal
class Cat extends Animal
class Dog extends Animal

// To allow covariance in the return without violating contravariance of T create a replacement type
// that artificially narrows the type
// At worst we will return a B == T but T is a super type of B
class PetShop[-T] {
  def get[B <: T](isItApuppy: Boolean, defaultAnimal: B): B = defaultAnimal
}

val shop: PetShop[Dog] = new PetShop[Animal]

//  val cat = shop.get(true, new Cat) // Won't work
class TerraNova extends Dog

val bigDog = shop.get(true, new TerraNova)
```