package at.hannibal2.skyhanni.config.features.gui.customscoreboard;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class AlignmentConfig {
    @Expose
    @ConfigOption(name = "Align to the right", desc = "Align the scoreboard to the right side of the screen.")
    @ConfigEditorBoolean
    public boolean alignRight = false;

    @Expose
    @ConfigOption(name = "Align to the center vertically", desc = "Align the scoreboard to the center of the screen vertically.")
    @ConfigEditorBoolean
    public boolean alignCenterVertically = false;
}