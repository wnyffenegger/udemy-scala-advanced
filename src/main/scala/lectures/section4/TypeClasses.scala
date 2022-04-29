package lectures.section4

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  // This works but this has two problems
  // 1. Only works for types that WE write
  // 2. One implementation out of many
  User("John", 32, "john@rockthejvm.com").toHtml

  // So improvements?

  // Better because we can now do whatever we want type wise
  // Problems
  // 1. lost type safety
  // 2. need to modify the code every time
  // 3. still one implementation, all others still need to be implemented
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      case d: java.util.Date =>
    }
  }

  // Attempt three, maybe a little better
  /**
   *   HTMLSerializer is called a TYPE CLASS
   *   a type class specifies a series of operations that can be performed on the class
   *
   *   Doesn't make sense to instantiate most times since it applies to all instances of a type. Normally done as singletons.
   *
   *
   */
  //

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  val john = User("John", 32, "john@rockthejvm.com")
  println(UserSerializer.serialize(john))

  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString()}"
  }

  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div> <a href=${user.email}/> </div>"
  }
  println(PartialUserSerializer.serialize(john))

  /**
   * Lets use implicits to improve this some more.
   *
   * Implicit objects with methods from Type Class can just be added right on in
   */



  // Specific metrics
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
  }

  // This of course works, but lets use implicits instead
  println(HTMLSerializer.serialize(42)(IntSerializer))

  println(HTMLSerializer.serialize(42))

  println(HTMLSerializer.serialize(john))

  // Adding apply gives access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  // What is all this?
  /**
   * Called AD-Hoc polymorphism
   *
   * Two distinct or unrelated types can have equal called if they implement Equal. Compiler takes care to find
   * the correct type class instance.
   */

  // Part 3

  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML(UserSerializer))
  println(john.toHTML)
  println(2.toHTML)

  /**
   * Why is this super valuable?
   *
   * - extend to new types
   * - different implementations for the same type
   * - super expressive
   */

  /**
   * - type class itself
   * - type class instances (some of which are implicit)
   * - conversion with implicit classes
   */

  // You could write a method this way
  def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String = {
    s"<html><doby> ${content.toHTML(serializer)}</body></html>"
  }

  // Or you could use even more syntactic sugar to get rid of the boilerplate implicit
  def htmlSugar[T : HTMLSerializer](content: T): String = {
    s"<html><doby> ${content.toHTML}</body></html>"
  }

  // Best of both worlds you can have compact method and surface implicit in use
  def htmlSugarWithImplicitly[T : HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    s"<html><doby> ${content.toHTML(serializer)}</body></html>"
  }

  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // Figure out which implicit is in use
  val standardPerms = implicitly[Permissions]
}
