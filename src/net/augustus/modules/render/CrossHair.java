package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.RainbowUtil;

public class CrossHair extends Module {
   public final ColorSetting color = new ColorSetting(1074, "Color", this, new Color(94, 0, 255, 255));
   public final DoubleValue length = new DoubleValue(13017, "Length", this, 4.0, 0.0, 20.0, 1);
   public final DoubleValue width = new DoubleValue(16121, "Width", this, 1.3, 0.0, 10.0, 1);
   public final DoubleValue margin = new DoubleValue(14510, "Margin", this, 1.3, 0.0, 20.0, 1);
   public final BooleanValue dot = new BooleanValue(6373, "Dot", this, true);
   public final BooleanValue tStyle = new BooleanValue(4773, "T-Style", this, false);
   public final BooleanValue rainbow = new BooleanValue(348, "Rainbow", this, false);
   public final DoubleValue rainbowSpeed = new DoubleValue(4440, "RainbowSpeed", this, 55.0, 0.0, 1000.0, 0);
   public final RainbowUtil rainbowUtil = new RainbowUtil();

   public CrossHair() {
      super("Crosshair", new Color(157, 230, 106), Categorys.RENDER);
   }
}
