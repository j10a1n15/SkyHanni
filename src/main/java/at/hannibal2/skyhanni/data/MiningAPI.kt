package at.hannibal2.skyhanni.data

import at.hannibal2.skyhanni.events.ColdUpdateEvent
import at.hannibal2.skyhanni.events.ScoreboardChangeEvent
import at.hannibal2.skyhanni.features.gui.customscoreboard.ScoreboardPattern
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.StringUtils.matchFirst
import at.hannibal2.skyhanni.utils.StringUtils.matches
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.absoluteValue

object MiningAPI {

    private val group = RepoPattern.group("data.miningapi")
    private val glaciteAreaPattern by group.pattern("area.glacite", "Glacite Tunnels")

    private var oldCold = 0


    fun inGlaciteArea() = glaciteAreaPattern.matches(HypixelData.skyBlockArea) || IslandType.MINESHAFT.isInIsland()

    fun getCold(): Int? = ScoreboardData.sidebarLinesFormatted.matchFirst(ScoreboardPattern.coldPattern) {
        return group("cold").toInt().absoluteValue
    }

    @SubscribeEvent
    fun onScoreboardChangeEvent(event: ScoreboardChangeEvent) {
        val newCold = getCold() ?: return

        if (newCold - oldCold != 0) {
            ColdUpdateEvent(newCold).postAndCatch()

            oldCold = newCold
        }
    }

}
