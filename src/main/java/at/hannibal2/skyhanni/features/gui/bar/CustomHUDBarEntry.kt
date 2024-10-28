package at.hannibal2.skyhanni.features.gui.bar

import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementAlignLeftRight
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElement
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementBits
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementDate
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementLocation
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementPurse
import at.hannibal2.skyhanni.features.gui.bar.elements.BarElementTime

enum class CustomHUDBarEntry(val element: BarElement) {
    PURSE(BarElementPurse),
    BITS(BarElementBits),
    LOCATION(BarElementLocation),
    ALIGN_LEFT_RIGHT(BarElementAlignLeftRight),
    DATE(BarElementDate),
    TIME(BarElementTime),
    ;

    override fun toString(): String = element.configLine
}
