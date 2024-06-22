package at.hannibal2.skyhanni.features.gui.customscoreboard

import at.hannibal2.skyhanni.data.MiningAPI
import at.hannibal2.skyhanni.data.PurseAPI
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboard.chunkedConfig
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboard.informationFilteringConfig
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.formatNumber
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getBank
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getBits
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getBitsLine
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getBitsToClaim
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getCopper
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getGems
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getHeat
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getMotes
import at.hannibal2.skyhanni.features.gui.customscoreboard.CustomScoreboardUtils.getNorthStars
import java.util.function.Supplier

private val hideEmptyLines get() = informationFilteringConfig.hideEmptyLines

enum class ChunkedStatsManager(
    private val displayPair: Supplier<String>,
    val showWhen: () -> Boolean,
    private val configLine: String,
) {
    PURSE(
        displayPair = { "§6${formatNumber(PurseAPI.currentPurse)}" },
        showWhen = { !(hideEmptyLines && PurseAPI.currentPurse.toInt() == 0) && ScoreboardElementManager.PURSE.element.showWhen() },
        configLine = "§6Purse",
    ),
    MOTES(
        displayPair = { "§b${getMotes()}" },
        showWhen = { !(hideEmptyLines && getMotes() == "0") && ScoreboardElementManager.MOTES.element.showWhen() },
        configLine = "§dMotes",
    ),
    BANK(
        displayPair = { "§6${getBank()}" },
        showWhen = {
            !(hideEmptyLines && (getBank() == "0" || getBank() == "0§7 / §60")) && ScoreboardElementManager.BANK.element.showWhen()
        },
        configLine = "§6Bank",
    ),
    BITS(
        displayPair = { getBitsLine() },
        showWhen = { !(hideEmptyLines && getBits() == "0" && getBitsToClaim() == "0") && ScoreboardElementManager.BITS.element.showWhen() },
        configLine = "§bBits",
    ),
    COPPER(
        displayPair = { "§c${getCopper()}" },
        showWhen = { !(hideEmptyLines && getCopper() == "0") && ScoreboardElementManager.COPPER.element.showWhen() },
        configLine = "§cCopper",
    ),
    GEMS(
        displayPair = { "§a${getGems()}" },
        showWhen = { !(hideEmptyLines && getGems() == "0") && ScoreboardElementManager.GEMS.element.showWhen() },
        configLine = "§aGems",
    ),
    HEAT(
        displayPair = { "§c${getHeat()}" },
        showWhen = { !(hideEmptyLines && getHeat() == "§c♨ 0") && ScoreboardElementManager.HEAT.element.showWhen() },
        configLine = "§cHeat",
    ),
    COLD(
        displayPair = { "§b${MiningAPI.cold}❄" },
        showWhen = { !(hideEmptyLines && MiningAPI.cold == 0) && ScoreboardElementManager.COLD.element.showWhen() },
        configLine = "§bCold",
    ),
    NORTH_STARS(
        displayPair = { "§d${getNorthStars()}" },
        showWhen = { !(hideEmptyLines && getNorthStars() == "0") && ScoreboardElementManager.NORTH_STARS.element.showWhen() },
        configLine = "§dNorth Stars",
    ),
    ;

    override fun toString() = configLine

    companion object {
        fun getChunkedStats() = buildList {
            chunkedConfig.chunkedStats.forEach { stat ->
                if (stat.showWhen()) {
                    add(stat.displayPair.get())
                }
            }
        }

        fun shouldShowChunkedStats() = chunkedConfig.chunkedStats.any { it.showWhen() }
    }
}
