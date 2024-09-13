package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;

public class HUD extends Module {
   public StringValue side = new StringValue(14133, "Side", this, "Left", new String[]{"Left", "Right"});
   public StringValue mode = new StringValue(195, "Mode", this, "Basic", new String[]{"None", "Basic", "Other", "Ryu", "Old", "CSGO"});
   public DoubleValue size = new DoubleValue(2326, "Size", this, 0.6, 0.01, 2.0, 2);
   public BooleanValue hotBar = new BooleanValue(10102, "Hotbar", this, true);
   public BooleanValue armor = new BooleanValue(12548, "Armor", this, true);
   public BooleanValue targethud = new BooleanValue(16060, "TargetHud", this, true);
   public StringValue targetMode = new StringValue(640, "TargetHud-Mode", this, "Basic", new String[]{"Basic", "AugustusX", "AugustusX_Round","Exhibition"});

   public ColorSetting color1 = new ColorSetting(11823, "Target_HUD_Color", this,  new Color(34, 173, 161));

   public BooleanValue blur = new BooleanValue(7057, "Blur", this, false);

   public DoubleValue radius = new DoubleValue(8859, "Radius", this, 1.5, 0f, 14f, 1);
   public BooleanValue backGround = new BooleanValue(7332, "BackGround", this, true);
   public ColorSetting backGroundColor = new ColorSetting(5806, "BackGroundColor", this, new Color(0, 0, 0, 100));
   public ColorSetting color = new ColorSetting(9616, "Color", this, Color.white);
   public DoubleValue targethud_x = new DoubleValue(13155, "Targethud X", this, 0, 0, 2000, 0);
   public DoubleValue targethud_y = new DoubleValue(7812, "Targethud Y", this, 0, 0, 2000, 0);

   public HUD() {
      super("HUD", new Color(75, 166, 91), Categorys.RENDER);
   }
}
