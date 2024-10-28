package at.hannibal2.skyhanni.features.gui.bar.elements

import at.hannibal2.skyhanni.data.HypixelData

object BarElementLocation : BarElement() {
    override val configLine: String = "Location"
    override fun getString(): String = HypixelData.skyBlockAreaWithSymbol ?: "§7Unknown"
}
