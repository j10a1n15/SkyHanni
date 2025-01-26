package at.hannibal2.skyhanni.features.gui.bar.elements

import at.hannibal2.skyhanni.data.PurseApi
import at.hannibal2.skyhanni.utils.NumberUtil.addSeparators

object BarElementPurse : BarElement() {
    override val configLine: String = "§6Purse"
    override fun getString(): String = "Purse: §6${PurseApi.getPurse().addSeparators()}"
}
