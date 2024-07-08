from ...json import *
from dataclasses import field
from typing import List
from functools import cached_property
from ._common import is_pow2

__all__ = ["Config", "kind"]

kind = "axi4"


_AXI4_SIGNALS = [
    "AWVALID",
    "AWREADY",
    "AWADDR",
    "AWPROT",
    "AWUSER",
    "AWREGION",
    "AWQOS",
    "AWCACHE",
    "AWBURST",
    "AWSIZE",
    "AWLEN",
    "AWID",
    "AWLOCK",
    "WVALID",
    "WREADY",
    "WDATA",
    "WSTRB",
    "WUSER",
    "WLAST",
    "BVALID",
    "BREADY",
    "BRESP",
    "BUSER",
    "BID",
    "ARVALID",
    "ARREADY",
    "ARADDR",
    "ARPROT",
    "ARUSER",
    "ARREGION",
    "ARQOS",
    "ARCACHE",
    "ARBURST",
    "ARSIZE",
    "ARLEN",
    "ARID",
    "ARLOCK",
    "RVALID",
    "RREADY",
    "RDATA",
    "RRESP",
    "RUSER",
    "RID",
    "RLAST"
]

_AXI4LITE_SIGNALS = [
    "AWVALID",
    "AWREADY",
    "AWADDR",
    "AWPROT",
    "WVALID",
    "WREADY",
    "WDATA",
    "WSTRB",
    "BVALID",
    "BREADY",
    "BRESP",
    "ARVALID",
    "ARREADY",
    "ARADDR",
    "ARPROT",
    "RVALID",
    "RREADY",
    "RDATA",
    "RRESP"
]


@register_dataclass_adv("hdlinfo.protocols.amba.axi4.Config", aliases=["chext.amba.axi4.Config"])
@dataclass(frozen=True)
class Config:
    wId: int = 0
    wAddr: int = 32
    wData: int = 32
    read: bool = True
    write: bool = True
    lite: bool = False
    wUserAR: int = 0
    wUserR: int = 0
    wUserAW: int = 0
    wUserW: int = 0
    wUserB: int = 0

    skippedSignals: List[str] = field(default_factory=list)

    def __post_init__(self):
        if self.wData < 8:
            raise ValueError("wData must be at least 8")
        if not is_pow2(self.wData):
            raise ValueError("wData must be a power of 2")
        if self.lite and self.wData not in [32, 64]:
            raise ValueError("If lite is True, wData must be 32 or 64")
        if self.wAddr <= 0:
            raise ValueError("wAddr must be greater than 0")
        if any(user < 0 for user in [self.wUserAR, self.wUserR, self.wUserAW, self.wUserW, self.wUserB]):
            raise ValueError("all wUserX attributes must be greater than or equal to 0")

    @property
    def wStrobe(self):
        return self.wData // 8

    @property
    def signals(self) -> List[str]:
        if self.lite:
            return list(_AXI4LITE_SIGNALS)

        result = []
        for signal in _AXI4_SIGNALS:
            if signal in self.skippedSignals:
                continue

            if signal.endswith("USER"):
                width: int = getattr(self, f'wUser{signal.removesuffix("USER")}')
                if width == 0:
                    continue
            else:
                result.append(signal)
        return result
