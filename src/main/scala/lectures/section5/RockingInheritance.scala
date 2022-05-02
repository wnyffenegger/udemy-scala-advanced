package lectures.section5

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(vlaue: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    // some methods hear
    def foreach(f: T => Unit): Unit

    def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
      stream.foreach(println)
      stream.close(0)
    }
  }

  // diamond problem

  // All of these compile even though you don't have a straight line for inheritance, you have a diamond
  // Whose behavior wins?
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

  /**
   * Why does this print out tiger. Last thing to override wins, lion overrides animal, tiger overrides lion.
   */

  // the super problem + type linearization

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

  // Prints
  // white
  // blue
  // green
  // cold
}
