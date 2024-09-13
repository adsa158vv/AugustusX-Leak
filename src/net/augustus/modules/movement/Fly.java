package net.augustus.modules.movement;

import java.awt.Color;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import static net.minecraft.init.Blocks.air;

public class Fly extends Module {
   public StringValue mode = new StringValue(12326, "Mode", this, "Vanilla", new String[]{"Vanilla", "Motion", "Flag","LatestNCP","GrimC06","DuChenJian"});

   public DoubleValue vanilla_Speed = new DoubleValue(701, "Speed", this, 1.0, 0.1, 9.0, 1);
   public DoubleValue motion_Motion = new DoubleValue(2991, "Motion", this, 1.0, 0.1, 9.0, 1);
   public final BooleanValue latestNCP_Teleport = new BooleanValue(231,"Teleport", this,false);
   public final BooleanValue latestNCP_Timer = new BooleanValue(545,"Timer", this,true);
   public final DoubleValue latestNCP_AddSpeed = new DoubleValue(12388,"AddSpeed", this,0.0, 0.0, 1.5,3);
   private boolean started, notUnder, clipped;
   private int offGroundTicks;

   public final DoubleValue timer = new DoubleValue(12552,"Timer",this,1.0,0,5,4);
   public BooleanValue onWorld = new BooleanValue(11750, "DisableOnWorld", this, false);
   private double prevFlySpd = 0;
   private double prevY;
   private int dcjTicks = 0;
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      if(onWorld.getBoolean()) {
         setToggled(false);
      }
      dcjTicks = 0;
      switch (this.mode.getSelected()) {
         case "Motion":
            prevY = mc.thePlayer.posY;
            break;
      }
   }


   public Fly() {
      super("Fly", new Color(123, 240, 156), Categorys.MOVEMENT);
   }

   @Override
   public void onDisable() {

      mc.getTimer().timerSpeed = 1.0F;

      switch(this.mode.getSelected()) {
         case "Vanilla":
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            if (prevFlySpd > 0) {
               mc.thePlayer.capabilities.setFlySpeed((float) prevFlySpd);
            }
            mc.thePlayer.capabilities.isFlying = false;
            break;
         case "Motion":
            prevY = 0;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            break;
      }
      dcjTicks = 0;
   }


   @EventTarget
   public void onEventTick(EventTick eventTick) {
      mc.getTimer().timerSpeed = (float)this.timer.getValue();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.thePlayer != null && mc.theWorld != null) {
         prevFlySpd = mc.thePlayer.capabilities.getFlySpeed();

         switch (this.mode.getSelected()) {
            case "Motion":
               prevY = mc.thePlayer.posY;
               break;
         }
      }
      dcjTicks = 0;
   }



   @EventTarget
   public void onUpdate(EventUpdate eventUpdate) {

      this.setSuffix(this.mode.getSelected(), true);

      if (mc.thePlayer == null && mc.theWorld == null) return;

      switch (this.mode.getSelected()) {
         case "Flag": {
            if (mc.isSingleplayer()) {
               mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6Fly§F]§F[§5Anti§4LIQ§F] Do §4NOT§F Use Flag Mode Fly in SinglePlayer ! It will Cause your game to §4CRASH §F!"));
               this.setToggled(false);
               return;
            }
            float flagmotionjump;
            float flagmotionsneak;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
               flagmotionjump = 1.5624f;
            } else {
               flagmotionjump = 0.00000001f;
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
               flagmotionsneak = 0.0624f;
            } else {
               flagmotionsneak = 0.00000002f;
            }


            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY + flagmotionjump - flagmotionsneak, mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + mc.thePlayer.motionX * 999, mc.thePlayer.posY - 6969, mc.thePlayer.posZ + mc.thePlayer.motionZ * 999, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            mc.thePlayer.setPosition(mc.thePlayer.posX + mc.thePlayer.motionX * 11, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.motionZ * 11);
            break;
         }
         case "Vanilla": {
            mc.thePlayer.capabilities.isFlying = true;
            mc.thePlayer.capabilities.setFlySpeed((float) this.vanilla_Speed.getValue());

            break;
         }
         case "Motion":

            double moveSpeed = motion_Motion.getValue();

            if (mc.gameSettings.keyBindJump.isKeyDown())
               mc.thePlayer.motionY = moveSpeed / 2;

            else if (mc.gameSettings.keyBindSneak.isKeyDown())
               mc.thePlayer.motionY = -moveSpeed / 2;
            else
               mc.thePlayer.motionY = 0;
            if (MoveUtil.noMoveKey()) {
               mc.thePlayer.motionX = 0;
               mc.thePlayer.motionY = 0;
               mc.thePlayer.motionZ = 0;
            }
            MoveUtil.strafe(moveSpeed);

            break;
         case "LatestNCP": {
            if (mc.thePlayer.onGround) {
               offGroundTicks = 0;
            } else offGroundTicks++;

            if (latestNCP_Timer.getBoolean()) {
               if (!mc.thePlayer.onGround) {
                  mc.timer.timerSpeed = 0.4f;
               } else {
                  mc.timer.timerSpeed = 1.0F;
               }
            }
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, 1, 0);

            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() || started) {
               switch (offGroundTicks) {
                  case 0:
                     if (notUnder) {
                        if (clipped) {
                           started = true;
                           MoveUtil.crosSine_Strafe(9.5 + latestNCP_AddSpeed.getValue());
                           mc.thePlayer.motionY = 0.42f;
                           notUnder = false;
                        }
                     }
                     break;

                  case 1:
                     if (started) MoveUtil.crosSine_Strafe(8.0 + latestNCP_AddSpeed.getValue());
                     break;
               }
            } else {
               notUnder = true;

               if (clipped) return;

               clipped = true;

               if (latestNCP_Teleport.getBoolean()) {
                  PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                  PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                  PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
               }
            }

            MoveUtil.crosSine_Strafe(MoveUtil.getSpeed());
            break;
         }
         case "GrimC06":

            mc.getNetHandler().addToSendQueue(
                    new C03PacketPlayer.C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            mc.thePlayer.onGround
                    )
            );
         case "DuChenJian": {
            dcjTicks++;
            if (dcjTicks >= 2) {
               mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
               dcjTicks = 0;
            }
            break;
         }

      }
   }
      @EventTarget
      public void onPacket(EventSendPacket eventSendPacket){
         Packet packet = eventSendPacket.getPacket();
         if (mode.getSelected().equals("DuChenJian")) {
            if (packet instanceof C03PacketPlayer) {
               final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;
               packetPlayer.setOnGround(false);
            }
         }
      }
   @EventTarget
   public void BlockBB(EventBlockBoundingBox eventBlockBoundingBox){
      if (mode.getSelected().equals("DuChenJian")) {
         if (eventBlockBoundingBox.getBlock() == air && eventBlockBoundingBox.getBlockPos().getY() < mc.thePlayer.posY)
            eventBlockBoundingBox.setAxisAlignedBB(new AxisAlignedBB(
                    eventBlockBoundingBox.getBlockPos().getX(),
                    eventBlockBoundingBox.getBlockPos().getY(),
                    eventBlockBoundingBox.getBlockPos().getZ(),
                    eventBlockBoundingBox.getBlockPos().getX() + 1.0,
                    mc.thePlayer.posY,
                    eventBlockBoundingBox.getBlockPos().getZ() + 1.0));
      }
   }


}
