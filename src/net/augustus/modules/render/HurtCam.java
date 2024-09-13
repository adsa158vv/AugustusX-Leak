package net.augustus.modules.render;

import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.StringValue;
import net.lenni0451.eventapi.reflection.EventTarget;

import java.awt.*;

/**
 * @author Genius
 * @date 2024/4/13
 * @time 14:31
 */

public class HurtCam extends Module {
    public StringValue modValue = new StringValue(8092, "Mode", this,"Cancel", new String[] {"Vanilla", "Cancel"});

    public HurtCam() {
        super("HurtCam", new Color(255,255,255), Categorys.RENDER);
    }
}
