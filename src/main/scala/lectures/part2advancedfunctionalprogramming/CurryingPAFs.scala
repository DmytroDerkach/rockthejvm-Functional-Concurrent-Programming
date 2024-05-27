package lectures.part2advancedfunctionalprogramming

// currying and partial applied functions
object CurryingPAFs {

  // currying
  val supperAdded: Int => Int => Int = x => y => x + y

  private val addThree: Int => Int = supperAdded(3) // y => 3 + y
  val eight: Int = addThree(5)
  val eight_v2 = supperAdded(3)(5)

  //curried methods
  def curriedAdder(x: Int)(y: Int): Int = x + y

  // methods != function values

  private val add4: Int => Int = curriedAdder(4) // eta-expansion: converting methods to functions
  val nine = add4(5) // 9

  def increment(x: Int): Int = x + 1
  val aList = List(1,2,3)
  val incrementedList: Seq[Int] = aList.map(increment) // eta-expansion


  def concatenator(a: String, b: String, c: String): String = a + b + c

  private val concatenatorFun: (String, String, String) => String = concatenator

  // x => concatenator("..", x, "..")
  val insertName: String => String = concatenator("Hello, my name is", _: String, ", I'm going to show you Scala trick")
  private val function: (String, String) => String = concatenator(_: String, "name", _: String)
  val result: String = function("hi", "how are you")


  // methods vs functions + by-name vs 0-lambdas
  def byName(n: => Int): Int = n + 1
  def byLambdas( f: () => Int) = f() + 1
  def method: Int = 42
  def method2(): Int = 43

  def main(args: Array[String]): Unit = {
    byName(43) // ok
    byName(method) // method is invoked = 43
    byName(method2()) // 43
    byName(method2)
    byName((() => 45)()) // ok


//    byLambdas(43) // NOT ok
//    byLambdas(method)// NOT ok
    byLambdas(method2) // ok eta-expansion is done
    byLambdas(() => 43)
    byLambdas(() => method2())
  }
}
