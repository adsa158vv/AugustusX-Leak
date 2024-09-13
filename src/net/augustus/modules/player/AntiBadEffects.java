package net.augustus.modules.player;

import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.potion.Potion;

import java.awt.*;

public class AntiBadEffects extends Module {
   public AntiBadEffects() {
      super("AntiBadEffects", new Color(93, 98, 227), Categorys.PLAYER);
   }
   public BooleanValue blindness = new BooleanValue(8755,"Blindness",this,true);
   public BooleanValue confusion = new BooleanValue(13469,"Confusion",this,true);
   public BooleanValue debug = new BooleanValue(11721,"Debug",this,true);
   @EventTarget
   public void onUpdate(EventUpdate eventUpdate) {
      if (mc.thePlayer != null && mc.theWorld != null) {
         if (mc.thePlayer.getActivePotionEffect(Potion.blindness) != null && blindness.getBoolean()) {
            mc.thePlayer.removePotionEffect(Potion.blindness.getId());
            if (debug.getBoolean()) {
               LogUtil.addChatMessage("§F[§6AntiBadEffects§F]§F[§5Debug§F] Remove the §4"+Potion.blindness.getName()+"§f Effect !");
            }
         }

         if (mc.thePlayer.getActivePotionEffect(Potion.confusion) != null && confusion.getBoolean()) {
            mc.thePlayer.removePotionEffect(Potion.confusion.getId());
            if (debug.getBoolean()) {
               LogUtil.addChatMessage("§F[§6AntiBadEffects§F]§F[§5Debug§F] Remove the §4"+Potion.confusion.getName()+"§f Effect !");
            }
         }
      }
   }
}
