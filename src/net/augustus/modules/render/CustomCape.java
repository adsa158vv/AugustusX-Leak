package net.augustus.modules.render;

import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.cape.LoadCape;

import java.awt.*;

/**
 * @author Genius
 * @since 2024/5/1 14:17
 * IntelliJ IDEA
 */

public class CustomCape extends Module {

    public StringValue customCapes = new StringValue(647, "CustomCapes", this, "Vanilla", LoadCape.getCapes());

    public BooleanValue reload = new BooleanValue(4886, "Reload", this, false);

    public CustomCape() {
        super("CustomCape", new Color(255,255,255), Categorys.RENDER);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.reload.getBoolean()) {
            this.reload.setBoolean(false);
        }
    }

}
