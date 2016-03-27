package learning

import breeze.linalg.DenseVector
import collection.mutable.ArrayBuffer

class LogitMutable(size: Int, alpha: Double = 0.1) {

  private var w = ArrayBuffer.fill(size)(0D)
  //Todo benchmark Algebird probabalistic data structure
  private var n = ArrayBuffer.fill(size)(0)

  private var c = 0

  //sigmoid
  def out(y: Double): Double = {
    val bounded = y min 50 max -50
    1 / (1 + math.exp(bounded))
  }

  private def agg(features: Seq[Long]) : Double = {

    val feature = features.toIterator
    @annotation.tailrec
    def loop(acc: Double) : Double = {
      if (!feature.hasNext) return acc
      val f = feature.next()
      loop(acc + w(f.toInt))
    }
    loop(0)

  }

  def cost(y: Int, y_hat: Double): Double = {
    y_hat - y.toDouble
  }

  def logLoss(y: Int, y_hat: Double) : Double = {
    //restrain prediction to restrain logloss
    val bounded = y_hat max 1e-12 min (1-1e-12)
    if (y == 1) -math.log(bounded)
    else -math.log(1-bounded)
  }

  def predict(features: Seq[Long]): Double = {
    out(agg(features))
  }

  def update(y: Int, y_hat: Double, features: Seq[Long]): Unit = {
    val feature = features.toIterator
    val err = -cost(y, y_hat)
    val delta = err*alpha

    @annotation.tailrec
    //bogus def
    def loop() : Unit = {

      if (feature.hasNext) {
        val f = feature.next().toInt
        w(f) = w(f) - delta / math.sqrt(n(f).toDouble + 1D)
        c = c + 1
        n(f) = n(f) + 1
        loop()
      }
      else ()
    }
    loop()
  }

}
