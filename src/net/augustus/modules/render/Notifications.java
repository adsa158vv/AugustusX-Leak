package net.augustus.modules.render;

import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;

import java.awt.*;

public class Notifications extends Module {
    public StringValue mode = new StringValue(12979, "Mode", this, "AugustusX", new String[]{"Xenza", "Rise5", "New", "AugustusX","Stable", "Custom", "Double"});

    public DoubleValue radius = new DoubleValue(2 ,"Radius", this, 1.5f, 0.0, 14.0f, 3);

    public Notifications() {
        super("Notifications", Color.green, Categorys.RENDER);
    }



}
