package net.augustus.modules.player;

import net.augustus.events.EventSendPacket;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;

import java.awt.*;

public class AutoHeal extends Module {

   public AutoHeal() {
      super("AutoHeal", new Color(23, 123, 208, 255), Categorys.PLAYER);
   }

   public StringValue mode = new StringValue(16325, "Mode", this, "/heal", new String[]{"/heal"});
   public DoubleValue heal_MinHealth = new DoubleValue(4321,"MinHealth",this,5,0,25,0);
   public DoubleValue heal_Delay = new DoubleValue(6444,"Delay",this,500,0,10000,0);

   TimeHelper timeHelper = new TimeHelper();

   @EventTarget
   public void onEventUpdate(EventUpdate event) {
      if (mc.thePlayer != null && mc.theWorld != null) {
         if (mc.thePlayer.getHealth() <= heal_MinHealth.getValue()) {
            if (timeHelper.reached((long) this.heal_Delay.getValue())) {
               mc.thePlayer.sendChatMessage("/heal");
               this.timeHelper.reset();
            }
         }


      }
   }
   @Override
   public void onEnable(){
      super.onEnable();

   }
   @Override
   public void onDisable(){
      super.onDisable();

   }
   @EventTarget
   public void onWorld(EventWorld eventWorld) {

   }



}
