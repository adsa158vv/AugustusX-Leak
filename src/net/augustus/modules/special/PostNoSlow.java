package net.augustus.modules.special;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class PostNoSlow extends Module {


   public BooleanValue flagDetect = new BooleanValue(15562,"FlagDetect",this,true);
   public BooleanValue flagDetect_Debug = new BooleanValue(1184,"FlagDebug",this,false);


 //  public BooleanValue flagDetect_autoRestart = new BooleanValue(13472,"FlagRestart",this,true);
   public BooleanValue autoRestart = new BooleanValue(15756,"AutoRestart",this,true);
   public BooleanValue autoRestart_NoNoti = new BooleanValue(10509,"NoNoti",this,true);


   public PostNoSlow() {
      super("PostNoSlow", Color.DARK_GRAY, Categorys.SPECIAL);
   }
   boolean restarted = false;
   @Override
   public void onDisable() {
      super.onDisable();
      restarted = true;
   }
   @Override
   public void onEnable() {
      super.onEnable();

   }

   @Override
   public void onWorld(EventWorld event) {
      super.onWorld(event);

   }



   @EventTarget
   public void onEventReadPacket(EventReadPacket eventReadPacket) {
      if (eventReadPacket.getPacket() instanceof S08PacketPlayerPosLook) {
         if (flagDetect.getBoolean() && (mc.gameSettings.keyBindUseItem.pressed) && mc.currentScreen == null) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            if (flagDetect_Debug.getBoolean()) LogUtil.addChatMessage("§F[§6NoSlow2§F]§F[§4FlagDetect§5Debug§F] S08 Detected. Automatically set KeyBindUseItem Pressed = False.");
         }
      }


   }

   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
       if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
           return ;
       }
      if (MoveUtil.noMoveKey()) return;
      if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword  && mc.gameSettings.keyBindUseItem.pressed) {
         mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }
      if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow  && mc.gameSettings.keyBindUseItem.pressed) {
         mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 +1));
         mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
         mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      }
   }
   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
       if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
           return ;
       }
      if (MoveUtil.noMoveKey()) return;
      if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.pressed) {
         PacketUtil.sendPacketC0F();
         mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      }
      if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow  && mc.gameSettings.keyBindUseItem.pressed) {
         mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      }
   }


   @EventTarget
   public void onEventNoSlow(EventSlowDown eventSlowDown) {
       if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
           return ;
       }
      if (MoveUtil.noMoveKey()) return ;
      if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() != null && mc.gameSettings.keyBindUseItem.pressed && (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)) {
          Item item = mc.thePlayer.getCurrentEquippedItem().getItem();
            eventSlowDown.setSprint(true);
            eventSlowDown.setMoveForward(getMultiplier(item));
            eventSlowDown.setMoveStrafe(getMultiplier(item));

      }
   }
   private float getMultiplier(Item item) {



      if (item instanceof ItemSword || item instanceof ItemBow) {
         return 1.0f;
      } else {
         return 0.2f;
      }
   }
   @EventTarget
   public void onEventTick(EventTick eventTick) {

      if (mc.thePlayer.ticksExisted > 20 && !restarted && this.autoRestart.getBoolean()) {
         this.restart(autoRestart_NoNoti.getBoolean());

      }
   }


}
