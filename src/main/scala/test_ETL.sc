import java.io.File

import helpers.HashHelper
import HashHelper._
import learning.Datum
import learning.LogitMutable


def getListOfFiles(dir: String):List[File] = {
  val d = new File(dir)
  if (d.exists && d.isDirectory) {
    d.listFiles.filter(_.isFile).toList
  } else {
    List[File]()
  }
}


val fileUrl = """file:///Users/dafcok/Documents/Development/scala-ml-snippets/dac_sample.txt"""

val file = io.Source.fromURL(fileUrl)


val D = math.pow(2, 20).toInt

lazy val data = file.getLines()
  .filter(_ => true)    //later: hold out test set
  .map(_.split("\\t"))
  .map(v => Datum(v.head.toInt, sparseHash(v.tail, D)))


val logit = new LogitMutable(D, 0.1)

def train(data: Iterator[Datum]): Unit = {

  for(row <- data.zipWithIndex) {
    val (datum, line) = (row._1, row._2)
    val y_hat = logit.predict(datum.features)
    val err = logit.cost(datum.target, y_hat)
    val logloss = logit.logLoss(datum.target, y_hat)
    logit.update(datum.target, y_hat, datum.features)
    if ((line != 0) && (line % 1000 == 0)) {
      println(datum.target.toString + " -> " + y_hat.toString + " " + line.toString + ": " + err.toString + " " + logloss.toString )
    }
  }

}

train(data)

/*
val lines = file.getLines().zipWithIndex
val testSamples = lines.filter(l => filterStochastic(l._2.toString()+"adsf", bmTest))
  .count(l => true)

val lines2 = file.getLines().zipWithIndex
val trainSamples = lines2.filter(l => filterStochastic(l._2.toString()+"asdf", bmTrain))
  .count(l => true)
*/