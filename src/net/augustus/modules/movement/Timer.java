package net.augustus.modules.movement;

import java.awt.Color;
import net.augustus.events.EventPreMotion;
import net.augustus.events.EventTick;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.lenni0451.eventapi.reflection.EventTarget;

public class Timer extends Module {
   public DoubleValue timerSpeed = new DoubleValue(2550, "Timer", this, 1.0, 0.1, 10.0, 2);
   private BooleanValue suffix = new BooleanValue(13204,"Suffix",this,false);
   public Timer() {
      super("Timer", new Color(180, 176, 119), Categorys.MOVEMENT);
   }

   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      mc.getTimer().timerSpeed = (float)this.timerSpeed.getValue();
   }
   @EventTarget
   public void onEventTick(EventTick eventTick) {

         this.setSuffix(String.valueOf(timerSpeed.getValue()), suffix.getBoolean());

   }

   @Override
   public void onDisable() {
      mc.getTimer().timerSpeed = 1.0F;
   }

   @EventTarget
   public void onEventWorld(EventWorld eventWorld) {
      if (eventWorld.getWorldClient() == null) {
         this.toggle();
      }
   }
}
