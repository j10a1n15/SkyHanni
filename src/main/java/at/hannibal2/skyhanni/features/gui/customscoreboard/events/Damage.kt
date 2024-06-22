package at.hannibal2.skyhanni.features.gui.customscoreboard.events

import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getSbLines
import at.hannibal2.skyhanni.features.gui.customscoreboard.ScoreboardPattern
import at.hannibal2.skyhanni.utils.RegexUtils.allMatches

object Damage : ScoreboardEvent() {
    override fun getDisplay() = listOf(
        ScoreboardPattern.bossHPPattern,
        ScoreboardPattern.bossDamagePattern,
    ).allMatches(getSbLines())

    override fun showWhen() = true

    override val configLine = "Dragon HP: §a6,180,925 §c❤\nYour Damage: §c375,298.5"
}
