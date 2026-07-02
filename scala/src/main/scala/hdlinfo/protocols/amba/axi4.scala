package hdlinfo.protocols.amba.axi4

import hdlinfo.Registry
import hdlinfo.util.isPow2

/** AXI4 interface configuration.
  *
  * @param wId
  *   identification tag width
  * @param wAddr
  *   address width
  * @param wData
  *   data bus width
  * @param read
  *   enable read channels
  * @param write
  *   enable write channels
  * @param lite
  *   use AXI4-Lite variant
  * @param hasLock
  *   enable ARLOCK and AWLOCK
  * @param hasCache
  *   enable ARCACHE and AWCACHE
  * @param hasProt
  *   enable ARPROT and AWPROT
  * @param hasQos
  *   enable ARQOS and AWQOS
  * @param hasRegion
  *   enable ARREGION and AWREGION
  * @param axi3Compat
  *   AXI3 compatibility: 2-bit AxLOCK and 4-bit AxLEN, still without WID
  * @param wUserAR
  *   ARUSER field width
  * @param wUserR
  *   RUSER field width
  * @param wUserAW
  *   AWUSER field width
  * @param wUserW
  *   WUSER field width
  * @param wUserB
  *   BUSER field width
  */
case class Config(
    val wId: Int = 0,
    val wAddr: Int = 32,
    val wData: Int = 32,
    val read: Boolean = true,
    val write: Boolean = true,
    val lite: Boolean = false,
    val hasLock: Boolean = true,
    val hasCache: Boolean = true,
    val hasProt: Boolean = true,
    val hasQos: Boolean = true,
    val hasRegion: Boolean = true,
    val axi3Compat: Boolean = false,
    val wUserAR: Int = 0,
    val wUserR: Int = 0,
    val wUserAW: Int = 0,
    val wUserW: Int = 0,
    val wUserB: Int = 0
) {
  require(wData >= 8)
  require(isPow2(wData))
  require(!lite || (wData == 32 || wData == 64))

  /** width of the strobe signal for the write data channel */
  val wStrobe = wData / 8

  private def maybeZero(p: Boolean, w: Int) = if (p) w else 0

  val wLen = if (axi3Compat) 4 else 8
  val wLock = maybeZero(hasLock, if (axi3Compat) 2 else 1)
  val wCache = maybeZero(hasCache, 4)
  val wProt = maybeZero(hasProt, 3)
  val wQos = maybeZero(hasQos, 4)
  val wRegion = maybeZero(hasRegion, 4)
}

import io.circe.syntax._
import io.circe.generic.auto._

object register {
  def apply(): Unit = {
    Registry.register[Config]("hdlinfo.protocols.amba.axi4.Config")
    Registry.registerStringToType[Config]("chext.amba.axi4.Config")
  }
}
