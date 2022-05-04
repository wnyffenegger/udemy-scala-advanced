package lectures.section5

object SelfTypes extends App {

  trait Instrumentalist {
    def play(): Unit
  }

  // whoever implements singer must implement instrumentalist
  trait Singer {
    // self doesn't matter
//    a: Instrumentalist =>
    self: Instrumentalist =>

    def sing(): Unit
  }

  // This is legal now
  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = println("under pressure")

    override def play(): Unit = println("notes")
  }

  // Illegal
  // self type does not does not conform to self type Instrumentalist
//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  // This is valid
  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  // Also legal
  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }
  val ericClapton = new Guitarist with Singer:
    override def sing(): Unit = ???

  // vs inheritance
  class A
  class B extends A     // B IS AN A

  trait T
  trait S { self: T => } // S REQUIRES a T

  // CAKE PATTERN => "dependency injection"
  // force dependency at some point in the future
  // called a cake pattern because it creates layers to a dependency

  // Java equivalent
  // Classic Spring dependency injection
  class Component {

  }
  class ComponentA extends Component
  class ComponentB extends Component
  // lots more

  // Dependencies are injected by the framework at run time
  class DependentComponent(val component: Component)

  // Cake pattern
  trait ScalaComponent {
    def action(x: Int): String
  }

  // Can use any subtype at some point in the future
  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + "this rocks!"
  }
//  trait ScalaApplication { self: ScalaDependentComponent => }

  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

//  trait AnalyticsApp extends ScalaApplication with Analytics

  // scala allows cyclical dependencies via self types

  trait X { self: Y => }
  trait Y { self: X => }
}
