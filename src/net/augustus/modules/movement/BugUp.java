package net.augustus.modules.movement;

import java.awt.Color;
import java.util.ArrayList;

import net.augustus.events.EventMove;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.PlayerUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class BugUp extends Module {
   public final DoubleValue maxDistance = new DoubleValue(8398, "MaxDistance", this, 15.0, 3.0, 30.0, 0);
   private final TimeHelper timeHelper = new TimeHelper();
   public StringValue mode = new StringValue(73, "Mode", this, "OnGround", new String[]{"Teleport", "OnGround","Vulcan","Freeze"});
   public BooleanValue onlyVoid = new BooleanValue(13,"OnlyVoid",this,true);

   private double[] xyz = new double[3];
   private final ArrayList<Packet> packets = new ArrayList<>();
   //
   private boolean tried = false;
   private double posX;
   private double posY;
   private double posZ;
   //Vulcan

   public BugUp() {
      super("BugUp", Color.DARK_GRAY, Categorys.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.packets.clear();
      tried = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.packets.clear();
      tried = false;
      super.onDisable();
   }
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      this.packets.clear();
      tried = false;
   }

   @EventTarget
   public void onEventTick(EventTick eventTick) {
      if (mc.thePlayer.onGround) {
         this.xyz = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
      }

      if (!onlyVoid.getBoolean() || shouldBugUp()) {
         String var2 = this.mode.getSelected();
         switch(var2) {
            case "Teleport":
               if (this.timeHelper.reached(200L)) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.xyz[0], this.xyz[1], this.xyz[2], true));
                  this.timeHelper.reset();
               }
               break;
            case "OnGround":
               mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
         }
      }
   }
   @EventTarget
   public void onMove(EventMove eventMove) {
      if (!onlyVoid.getBoolean() || shouldBugUp()) {
         if (this.mode.getSelected().equalsIgnoreCase("Freeze") && mc.thePlayer.fallDistance > maxDistance.getValue()) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
         }
      }
   }
   @EventTarget
   public void onUpdate(EventUpdate eventUpdate) {
      switch (this.mode.getSelected()) {
         case "Vulcan" : {
            if (mc.thePlayer.onGround && !(BlockUtil.getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir)) {
               posX = mc.thePlayer.prevPosX;
               posY = mc.thePlayer.prevPosY;
               posZ = mc.thePlayer.prevPosZ;
            }
            if (!onlyVoid.getBoolean() || shouldBugUp()) {
               if (mc.thePlayer.fallDistance > maxDistance.getValue() && !tried) {
                  mc.thePlayer.setPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ);
                  mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  mc.thePlayer.setPosition(posX, posY, posZ);
                  mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                  mc.thePlayer.fallDistance = 0.0F;
                  resetMotion(true);
                  tried = true;
               }
            }
         }
      }
   }

   private boolean shouldBugUp() {
      if (!mm.longJump.isToggled() && !mm.fly.isToggled() && !(mc.thePlayer.fallDistance < 2.0F)) {
         double posX = mc.thePlayer.posX;
         double posY = mc.thePlayer.posY;
         double posZ = mc.thePlayer.posZ;
         double motionX = mc.thePlayer.motionX;
         double motionY = mc.thePlayer.motionY;
         double motionZ = mc.thePlayer.motionZ;
         boolean isJumping = mc.thePlayer.isJumping;

         for(int i = 0; i < 200; ++i) {
            double[] doubles = PlayerUtil.getPredictedPos(
               mc.thePlayer.movementInput.moveForward, mc.thePlayer.movementInput.moveStrafe, motionX, motionY, motionZ, posX, posY, posZ, isJumping
            );
            isJumping = false;
            posX = doubles[0];
            posY = doubles[1];
            posZ = doubles[2];
            motionX = doubles[3];
            motionY = doubles[4];
            motionZ = doubles[5];
            BlockPos b = new BlockPos(posX, posY, posZ);
            Block block = mc.theWorld.getBlockState(b).getBlock();
            if (!(block instanceof BlockAir)) {
               return false;
            }

            if (Math.abs(mc.thePlayer.posY - posY) > this.maxDistance.getValue()) {
               break;
            }
         }
         int i = (int) Math.ceil(-(mc.thePlayer.posY - 1.4857625));
         boolean dangerous = true;
         while (i <= 0) {
            dangerous = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX * 0.5, i, mc.thePlayer.motionZ * 0.5)).isEmpty();
            i++;
            if (!dangerous) {
               break;
            }
         }
         return dangerous;


      } else {
         return false;
      }
   }

   public void resetMotion(boolean y) {
      mc.thePlayer.motionX = 0.0;
      mc.thePlayer.motionZ = 0.0;
      if (y) {
         mc.thePlayer.motionY = 0.0;
      }
   }
}
