


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