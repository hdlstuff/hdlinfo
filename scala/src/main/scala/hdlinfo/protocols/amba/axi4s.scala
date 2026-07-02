package hdlinfo.protocols.amba.axi4s

import hdlinfo.Registry

case class Config(
    val wData: Int,
    val wId: Int = 0,
    val wDest: Int = 0,
    val wUser: Int = 0,
    val hasStrobe: Boolean = true,
    val hasKeep: Boolean = true,
    val hasLast: Boolean = true
) {
  require(wData % 8 == 0)
  require(wId >= 0)
  require(wDest >= 0)
  require(wUser >= 0)

  private def maybeZero(p: Boolean, w: Int) = if (p) w else 0

  val wStrobe = maybeZero(hasStrobe, wData / 8)
  val wKeep = maybeZero(hasKeep, wData / 8)
  val wLast = maybeZero(hasLast, 1)
}

import io.circe.syntax._
import io.circe.generic.auto._

object register {
  def apply(): Unit = {
    Registry.register[Config]("hdlinfo.protocols.amba.axi4s.Config")
    Registry.registerStringToType[Config]("chext.amba.axi4s.Config")
  }
}
