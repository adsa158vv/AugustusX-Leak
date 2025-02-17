package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.events.EventItemRenderer;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.DoubleValue;
import net.lenni0451.eventapi.reflection.EventTarget;

public class CustomItemPos extends Module {
   public DoubleValue posX = new DoubleValue(11114, "X", this, 0.13, -1.0, 1.0, 2);
   public DoubleValue posY = new DoubleValue(5401, "Y", this, 0.13, -1.0, 1.0, 2);
   public DoubleValue posZ = new DoubleValue(8406, "Z", this, 0.04, -1.0, 1.0, 2);
   public DoubleValue blockPosX = new DoubleValue(12194, "BlockX", this, -0.27, -1.0, 1.0, 2);
   public DoubleValue blockPosY = new DoubleValue(3269, "BlockY", this, -0.04, -1.0, 1.0, 2);
   public DoubleValue blockPosZ = new DoubleValue(14080, "BlockZ", this, 0.0, -1.0, 1.0, 2);
   public DoubleValue scale = new DoubleValue(1333, "Scale", this, 0.2, 0.0, 2.0, 2);

   public CustomItemPos() {
      super("CustomItemPos", new Color(42, 145, 95), Categorys.RENDER);
   }

   @EventTarget
   public void onEventItemRenderer(EventItemRenderer eventItemRenderer) {
      eventItemRenderer.setX(this.posX.getValue());
      eventItemRenderer.setY(this.posY.getValue());
      eventItemRenderer.setZ(this.posZ.getValue());
      eventItemRenderer.setBlockX(this.blockPosX.getValue());
      eventItemRenderer.setBlockY(this.blockPosY.getValue());
      eventItemRenderer.setBlockZ(this.blockPosZ.getValue());
      eventItemRenderer.setScale(this.scale.getValue());
   }
}
