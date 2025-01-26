package at.hannibal2.skyhanni.features.gui.bar.elements

import at.hannibal2.skyhanni.data.BitsApi
import at.hannibal2.skyhanni.utils.NumberUtil.addSeparators

object BarElementBits : BarElement() {
    override val configLine: String = "§bBits"
    override fun getString(): String = "Bits: §b${BitsApi.bits.addSeparators()}"
}
