package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;

public class BlockAnimation extends Module {
   public final StringValue animation = new StringValue(713, "Animation", this, "1.7", new String[]{
           "1.7",
           "Exhibition",
           "Slide",
           "Spin",
           "Spin2",
           "Smooth",
           "Push",
           "Liquid",
           "Astolfo",
           "AstolfoSpin",
           "Moon",
           "MoonPush",
           "Jigsaw",
           "Invent",
           "Leaked",
           "Aqua",
           "Astro",
           "Float",
           "Remix",
           "Avatar",
           "Xiv"
   });
   public final DoubleValue height = new DoubleValue(3311, "Height", this, 0.0, -0.5, 1.5, 2);
   public final BooleanValue fakeBlock = new BooleanValue(10816, "FakeBlock", this, true);
   public final DoubleValue speed = new DoubleValue(2319, "Speed", this, 1.0, 0.1, 3.0, 1);
   public final BooleanValue cancleE = new BooleanValue(120199,"CancelEquip",this,true);

   public BlockAnimation() {
      super("BlockAnimation", new Color(136, 29, 163), Categorys.RENDER);
   }
}
