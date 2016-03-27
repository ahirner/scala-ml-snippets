
import java.io.File

import helpers.HashHelper
import HashHelper._
import learning.{Datum, LogitMutable}

object HashLearn extends App {

  val fileName = """data/dac/train.txt"""
  val D = math.pow(2, 20).toInt

  val file = io.Source.fromFile(fileName)

  lazy val data = file.getLines()
    //.filter(_ => true)    //later: hold out test set
    .map(_.split("\\t"))
    .map(v => Datum(v.head.toInt, sparseHash(v.tail, D)))

  val logit = new LogitMutable(D, 0.05)

  def train(data: Iterator[Datum]): Unit = {

    var logloss = 0D
    for(row <- data.zipWithIndex) {
      val (datum, line) = (row._1, row._2)
      val y = datum.target
      val y_hat = logit.predict(datum.features)
      val err = logit.cost(datum.target, y_hat)
      logloss = logloss + logit.logLoss(datum.target, y_hat)
      logit.update(datum.target, y_hat, datum.features)

      def log() : Unit = {
        val time = java.time.Instant.now().toString
        println(f"($time) - $line%d: $y%d -> $y_hat%1.2f with $err%1.2f | sum $logloss%f.2")
        logloss = 0D
      }

      if (line % 20000 == 0) log()

    }

  }

  train(data)
  //data.take(10).foreach(v => println(v))

}
