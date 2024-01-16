package at.hannibal2.skyhanni.data

import at.hannibal2.skyhanni.data.jsonobjects.repo.MaxwellPowersJson
import at.hannibal2.skyhanni.events.InventoryFullyOpenedEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.events.RepositoryReloadEvent
import at.hannibal2.skyhanni.utils.ItemUtils.getLore
import at.hannibal2.skyhanni.utils.LorenzUtils
import at.hannibal2.skyhanni.utils.LorenzUtils.isInIsland
import at.hannibal2.skyhanni.utils.StringUtils.matchMatcher
import at.hannibal2.skyhanni.utils.StringUtils.matches
import at.hannibal2.skyhanni.utils.StringUtils.trimWhiteSpaceAndResets
import at.hannibal2.skyhanni.utils.repopatterns.RepoPattern
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object MaxwellAPI {
    private val storage get() = ProfileStorageData.profileSpecific
    var currentPower: String?
        get() = storage?.maxwell?.currentPower
        set(value) {
            storage?.maxwell?.currentPower = value ?: return
        }
    var magicalPower: Int?
        get() = storage?.maxwell?.magicalPower
        set(value) {
            storage?.maxwell?.magicalPower = value ?: return
        }

    private var powers = mutableListOf<String>()

    private val group = RepoPattern.group("data.maxwell")
    private val chatPowerpattern by group.pattern(
        "chat.power",
        "§eYou selected the §a(?<power>.*) §e(power )?for your §aAccessory Bag§e!"
    )
    private val inventoryPowerPattern by group.pattern(
        "inventory.power",
        "§7Selected Power: §a(?<power>.*)"
    )
    private val inventoryMPPattern by group.pattern(
        "inventory.magicalpower",
        "§7Magical Power: §6(?<mp>[\\d,]+)"
    )
    private val thaumatorgyrGuiPattern by group.pattern(
        "gui.thaumatorgyr",
        "Accessory Bag Thaumaturgyr"
    )
    private val yourBagsGuiPattern by group.pattern(
        "gui.yourbags",
        "Your Bags"
    )

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        val message = event.message.trimWhiteSpaceAndResets()

        chatPowerpattern.matchMatcher(message) {
            val power = group("power")
            currentPower = getPowerByNameOrNull(power) ?: return
            return@matchMatcher
        }
    }

    @SubscribeEvent
    fun onInventoryFullyLoaded(event: InventoryFullyOpenedEvent) {
        if (!LorenzUtils.inSkyBlock) return

        if (thaumatorgyrGuiPattern.matches(event.inventoryName)) {
            val stacks = event.inventoryItems
            val selectedPower =
                stacks.values.find { it.getLore().isNotEmpty() && it.getLore().last() == "§aPower is selected!" }
                    ?: return

            currentPower = getPowerByNameOrNull(selectedPower.getLore().first()) ?: return
            return
        }

        if (yourBagsGuiPattern.matches(event.inventoryName)) {
            val stacks = event.inventoryItems

            stack@for (stack in stacks.values) {
                val lore = stack.getLore()
                for (line in lore) {
                    inventoryPowerPattern.matchMatcher(line) {
                        val power = group("power")
                        currentPower = getPowerByNameOrNull(power) ?: return
                        continue@stack
                    }
                    inventoryMPPattern.matchMatcher(line) {
                        // MagicalPower is boosted in catacombs
                        if (IslandType.CATACOMBS.isInIsland()) return

                        val mp = group("mp")
                        magicalPower = mp.replace(",", "").toIntOrNull() ?: return
                        continue@stack
                    }
                }
            }
        }
    }

    private fun getPowerByNameOrNull(name: String) = powers.find { it == name }

    // Load powers from repo
    @SubscribeEvent
    fun onRepoLoad(event: RepositoryReloadEvent) {
        val data = event.getConstant<MaxwellPowersJson>("MaxwellPowers")
        powers = data.powers
    }
}