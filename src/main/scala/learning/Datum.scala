package learning

case class Datum(target: Int, features: Seq[Long])
{
  override def toString: String =
    s"Datum(target = $target, features = $features)"
}
