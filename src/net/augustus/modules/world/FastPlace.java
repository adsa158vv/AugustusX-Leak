package net.augustus.modules.world;

import java.awt.Color;
import net.augustus.events.EventClick;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.RandomUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class FastPlace extends Module {
   private final TimeHelper timeHelper = new TimeHelper();
   public StringValue mode = new StringValue(4323, "Mode", this, "RemoveDelay", new String[]{"RemoveDelay", "RightClick"});
   public DoubleValue delay = new DoubleValue(6051, "Delay", this, 50.0, 0.0, 300.0, 0);
   public BooleanValue suffix = new BooleanValue(5645,"Suffix",this,false);
   public FastPlace() {
      super("FastPlace", new Color(54, 144, 217), Categorys.WORLD);
   }

   @EventTarget
   public void onEventClick(EventClick eventClick) {
      if (mode.getSelected().equalsIgnoreCase("RightClick")) {
         if (mc.currentScreen == null
                 && Mouse.isButtonDown(1)
                 && mc.objectMouseOver != null
                 && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                 && mc.thePlayer.getCurrentEquippedItem() != null
                 && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
            long random = this.delay.getValue() == 0.0 ? 0L : (long) (this.delay.getValue() + (double) RandomUtil.nextLong(-30L, 30L));
            if (this.timeHelper.reached(random)) {
               mc.rightClickMouse();
               this.timeHelper.reset();
            }

            eventClick.setCancelled(true);
         }
      }
   }
   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (mode.getSelected().equalsIgnoreCase("RemoveDelay")) {
         this.setSuffix(this.mode.getSelected(), suffix.getBoolean());
         mc.setRightClickDelayTimer(0);
      }

      else {

            this.setSuffix(this.mode.getSelected() + " D:"+delay.getValue(), suffix.getBoolean());

      }
   }
}
