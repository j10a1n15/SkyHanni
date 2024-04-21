package at.hannibal2.skyhanni.features.inventory

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.events.GuiContainerEvent
import at.hannibal2.skyhanni.utils.InventoryUtils
import at.hannibal2.skyhanni.utils.LorenzUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiInventory.drawEntityOnScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class WardrobeOverlay {

    private val config get() = SkyHanniMod.feature.inventory.wardrobeOverlay

    @SubscribeEvent
    fun onGuiRender(event: GuiContainerEvent.BeforeDraw) {
        if (!isEnabled()) return
        if (!InventoryUtils.openInventoryName().startsWith("Wardrobe")) return

        val gui = event.gui
        val player = Minecraft.getMinecraft().thePlayer
        val centerX = gui.width / 2
        val centerY = gui.height / 2
        val totalPlayers = 9
        val playerWidth = 50
        val spacing = 20

        // Calculate the total width occupied by players and spacing
        val totalWidth = totalPlayers * playerWidth + (totalPlayers - 1) * spacing

        // Calculate the starting X position to center the players
        val startX = centerX - (totalWidth - playerWidth) / 2
        val startY = centerY + playerWidth

        // Draw each player
        for (i in 0 until totalPlayers) {
            val playerX = startX + i * (playerWidth + spacing)

            // Calculate the new mouse position relative to the player
            val mouseXRelativeToPlayer = (playerX - event.mouseX).toFloat()
            val mouseYRelativeToPlayer = (startY - event.mouseY - 1.62 * playerWidth).toFloat()

            drawEntityOnScreen(
                playerX,
                startY,
                playerWidth,
                mouseXRelativeToPlayer,
                mouseYRelativeToPlayer,
                player
            )
        }

        event.cancel()
    }


    fun isEnabled() = LorenzUtils.inSkyBlock && config.enabled

}