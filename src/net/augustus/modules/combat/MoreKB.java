package net.augustus.modules.combat;

import java.awt.Color;

import net.augustus.events.EventAttackEntity;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.atani.RandomUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public class MoreKB extends Module {
   public StringValue mode = new StringValue(5147, "Modes", this, "Legit", new String[]{"Legit", "LegitFast", "LessPacket", "Packet", "DoublePacket", "MorePacket", "W-Tap"});
   public DoubleValue idkWhatToCallThisLmao = new DoubleValue(1142, "Packets", this, 5, 3, 10, 0);
   public DoubleValue legitFast_MinDelay = new DoubleValue(1498, "MinDelay", this, 150, 0, 400, 0);
   public DoubleValue legitFast_MaxDelay = new DoubleValue(11770, "MaxDelay", this, 150, 0, 400, 0);
   public BooleanValue intelligent = new BooleanValue(13362, "Intelligent", this, false);
   private BooleanValue suffix = new BooleanValue(2173,"Suffix",this,false);
   private boolean shouldSprintReset = false;
   private boolean isHit = false;
   private long delay = 0L;
   public TimeHelper attackTimer = new TimeHelper();

   private int ticks = 0;

   public MoreKB() {
      super("MoreKB", new Color(252, 73, 3), Categorys.COMBAT);
   }

   private void resetAll() {
      isHit = false;
      delay = 0L;
      ticks = 0;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      resetAll();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      resetAll();
   }

   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      resetAll();
   }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {

      if (this.mode.getSelected().equals("W-Tap")) {
         if (ticks == 2) {
            mc.gameSettings.keyBindForward.pressed = false;
            ticks = 1;
         } else if (ticks == 1) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            ticks = 0;
         }
      }

      if (this.legitFast_MinDelay.isVisible() && this.legitFast_MaxDelay.isVisible()) {
         this.setSuffix(this.mode.getSelected() + " Min:" + legitFast_MinDelay.getValue() + " Max:" + this.legitFast_MaxDelay.getValue(), suffix.getBoolean());
      } else {

         this.setSuffix(this.mode.getSelected(), suffix.getBoolean());

      }


      EntityLivingBase entity = null;
      if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && MovingObjectPosition.entityHit instanceof EntityLivingBase) {
         entity = (EntityLivingBase) MovingObjectPosition.entityHit;
      }

      if (mm.killAura.isToggled() && KillAura.target != null) {
         entity = KillAura.target;
      }

      if (entity != null) {
         double x = mc.thePlayer.posX - entity.posX;
         double z = mc.thePlayer.posZ - entity.posZ;
         float calcYaw = (float) (MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
         float diffY = Math.abs(MathHelper.wrapAngleTo180_float(calcYaw - entity.rotationYawHead));
         if (!this.intelligent.getBoolean() || !(diffY > 120.0F)) {
            String var9 = this.mode.getSelected();
            switch (var9) {
               case "MorePacket": {
                  if (entity.hurtTime == 10) {
                     for (int i = 0; i < idkWhatToCallThisLmao.getValue(); i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     }
                     mc.thePlayer.setServerSprintState(true);
                  }
                  break;
               }
               case "Packet":
                  if (entity.hurtTime == 10) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     mc.thePlayer.setServerSprintState(true);
                  }
                  break;
               case "DoublePacket":
                  if (entity.hurtTime == 10) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     mc.thePlayer.setServerSprintState(true);
                  }
                  break;
               case "Legit":
                  if (entity.hurtTime == 10) {
                     this.shouldSprintReset = true;
                     mc.thePlayer.reSprint = 2;
                     this.shouldSprintReset = false;
                     System.out.println("Resprint   " + entity.hurtTime);
                  }
                  break;
               case "Legit Fast":
                  if (isHit) {
                     mc.thePlayer.sprintingTicksLeft = 0;
                  }
                  break;
               case "LessPacket":
                  if (entity.hurtTime == 10) {
                     if (mc.thePlayer.isSprinting()) {
                        mc.thePlayer.setSprinting(false);
                     }

                     mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     mc.thePlayer.setServerSprintState(true);
                  }
            }
         }
      }
   }

   @EventTarget
   public void onEventAttackEntity(EventAttackEntity eventAttackEntity) {
      Entity entity = eventAttackEntity.getTarget();

      if (entity instanceof EntityLivingBase) {
         ticks = 2;
      }

      if (entity != null) {
         double x = mc.thePlayer.posX - entity.posX;
         double z = mc.thePlayer.posZ - entity.posZ;
         float calcYaw = (float) (MathHelper.func_181159_b(z, x) * 180.0 / Math.PI - 90.0);
         float diffY = Math.abs(MathHelper.wrapAngleTo180_float(calcYaw - entity.getRotationYawHead()));
         if (!this.intelligent.getBoolean() || !(diffY > 120.0F)) {
            switch (mode.getSelected()) {
               case "Legit":
               case "LegitFast":
                  if (!isHit) {
                     isHit = true;
                     attackTimer.reset();
                     delay = (long) RandomUtil.randomBetween(legitFast_MinDelay.getValue(), legitFast_MaxDelay.getValue());
                  }
                  break;
            }

         }
      }
   }
}
