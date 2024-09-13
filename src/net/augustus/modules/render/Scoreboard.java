package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;

public class Scoreboard extends Module {
   public BooleanValue remove = new BooleanValue(11192, "Remove", this, false);
   public BooleanValue stick = new BooleanValue(6464, "Stick", this, false);
   public DoubleValue xCord = new DoubleValue(14896, "PositionX", this, 100.0, 0.0, 100.0, 1);
   public DoubleValue yCord = new DoubleValue(3423, "PositionY", this, 60.0, 0.0, 100.0, 1);

   public Scoreboard() {
      super("Scoreboard", new Color(34, 162, 162), Categorys.RENDER);
   }
}
