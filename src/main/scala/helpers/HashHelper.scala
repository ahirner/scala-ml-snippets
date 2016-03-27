package helpers

import scala.util.Try

object HashHelper {
  def generateBitmask(p: Double): Int = {
    @annotation.tailrec
    def loop(c: Int, m: Int): Int = {
      if (c <= 0) return m
      loop(c - 1, (m | 1 << c) | 1)
    }

    val bits = (32 * p).toInt min 32 max 0
    loop(bits, 0)
  }

  def filterStochastic(x: Any, bitmask: Int): Boolean = {
    val hash = hashSimple(x) & 0xFFFFFFFF
    (hash & bitmask) != 0
  }

  def hashSimple(v : Any) : Int = {

    def loopMingle(base: Int): Int = {
      val base2 = base ^ (base >> 20) ^ (base >> 12)
      base2 ^ (base2 >> 7) ^ (base2 >> 4)
    }

    v match {
      case s: String => {
        @annotation.tailrec
        def loopChar(pos: Int, h: Int): Int =   //standard first transform in Java's hash function
          if (pos >= s.length) h
          else loopChar(pos + 1, h * 31 + s(pos).toInt)

        val base = loopChar(0, 0)
        loopMingle(base)
      }

      case i: Int => loopMingle(i)
      case _ => v.hashCode()
    }
  }

  def sparseHash(x: Seq[String], upperbound: Int, base: Int = 16) : Seq[Long] = {

    def hashIt(e: String, i: Long): Long = {
      val salted = i.toString + e
      Try {
        Integer.parseInt(salted, base)
      }.getOrElse(salted.hashCode()) & 0x00000000ffffffffL //gracefully revert to String's Java hash and erase sign
    }

    //Todo: test if including bias term at index 0 improves accuracy
    x.zipWithIndex.map(c => hashIt(c._1, c._2) % upperbound)
  }
}
