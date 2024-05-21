package lectures.part1

object Recap extends App {


  val a = for{
      num <- List(1,2,3,4)
      ch <- List ("a", "b", "c")
    } yield num + "_" + ch

  println(a)


  val list = List(1,2)
  val list2 = List(1,2)

  println(list :+ 3)
  println( 3 :: list)

  class Mutable {
    private var value: Int = 0

    def member = value
    def member_=(v: Int) = value = v
  }

  val m: Mutable = new Mutable
  m.member = 2
}
