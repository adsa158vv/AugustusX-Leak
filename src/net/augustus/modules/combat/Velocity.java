package net.augustus.modules.combat;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.PlayerUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Velocity extends Module {

   private final TimeHelper timeHelper = new TimeHelper();
   private final TimeHelper timeDelay = new TimeHelper();
   private final ArrayList<Packet> packets = new ArrayList<>();
   public StringValue mode = new StringValue(7193, "Mode", this, "Basic", new String[]{"Basic", "Legit", "PushGround", "Push", "Intave", "IntaveTest","Polar","SetXZMotion","AttackReduce", "Reverse", "Spoof", "Test", "Grim", "BuzzReverse", "TickZero","PolarReduce"});
   public DoubleValue XZValue = new DoubleValue(10426, "XZVelocity", this, 20.0, 0.0, 100.0, 0);
   public DoubleValue setXZMotion_Amount = new DoubleValue(6392,"XZMotion",this,0.6,-1,1,3);
   public BooleanValue setXZMotion_NoAttacking = new BooleanValue(945,"NoAttacking",this,false);
   public BooleanValue setXZMotion_cancelSprint = new BooleanValue(4902,"CancelSprint",this,false);
   public DoubleValue YValue = new DoubleValue(7879, "YVelocity", this, 20.0, 0.0, 100.0, 0);
   public DoubleValue XZValueIntave = new DoubleValue(3403, "XZVelocity", this, 0.6, -1.0, 1.0, 2);
   public DoubleValue MaxAfterHurtTime = new DoubleValue(112133,"MaxAfterHurtTime",this,3,1,9,0);
   public BooleanValue lreverse = new BooleanValue(112113,"Reverse",this,false);
   public BooleanValue yreducer = new BooleanValue(112116,"YReducer",this,false);
   public DoubleValue yreduce = new DoubleValue(112115,"YReduce",this,0.05,0.0,0.5,2);
   public BooleanValue jumpIntave = new BooleanValue(5856, "Jump", this, false);
   public BooleanValue ignoreExplosion = new BooleanValue(12941, "Explosion", this, true);
   public DoubleValue pushXZ = new DoubleValue(14164, "Push", this, 1.1, 0.01, 20.0, 2);
   public DoubleValue pushStart = new DoubleValue(12182, "PushStart", this, 9.0, 1.0, 10.0, 0);
   public DoubleValue pushEnd = new DoubleValue(4735, "PushEnd", this, 2.0, 1.0, 10.0, 0);
   public DoubleValue reverseStart = new DoubleValue(12669, "ReverseStart", this, 9.0, 1.0, 10.0, 0);
   public DoubleValue karhuStart = new DoubleValue(11992, "TickZero", this, 4, 1, 10, 0);
   public BooleanValue tickZeroY = new BooleanValue(4238, "TickZeroY", this, false);
   public BooleanValue reverseStrafe = new BooleanValue(6998, "ReverseStrafe", this, false);
   public BooleanValue pushOnGround = new BooleanValue(8229, "OnGround", this, false);
   public BooleanValue hitBug = new BooleanValue(848, "HitBug", this, false);
   public DoubleValue attackReduceMotionAmount = new DoubleValue(5892,"XZMotion",this,0.5,0,1,3);
   public BooleanValue debug = new BooleanValue(9696,"Debug",this,false);

   public Vec3 position = new Vec3(0.0, 0.0, 0.0);
   private int counter = 0;
   private int grimTCancel = 0;
   private boolean grimFlag;
   private double posY;
   private double posZ;
   private double posX;
   boolean polar_Attacked = false;
   boolean setXZMotion_Attacked = false;
   //polar reduce
   private boolean start = false;
   private int ticks,ms;
   //
   public Velocity() {
      super("Velocity", new Color(83, 102, 109, 255), Categorys.COMBAT);
   }
   @EventTarget
   public void onWorld(EventWorld eventWorld) {

      resetAll();
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
   private void resetAll() {
      polar_Attacked = false;
      setXZMotion_Attacked = false;
      start = false;
      ms = ticks = 0;
   }
   @EventTarget
   public void onEventAttackEntity(EventAttackEntity eventAttackEntity) {
      if (this.mode.getSelected().equalsIgnoreCase("AttackReduce")) {
         if (mc.thePlayer.hurtTime < 3) return;
         mc.thePlayer.motionX *= attackReduceMotionAmount.getValue();
         mc.thePlayer.motionZ *= attackReduceMotionAmount.getValue();
         if (debug.getBoolean()) {
            LogUtil.addChatMessage("§F[§6Velocity§F]§F[§5Debug§F] MotionXZ *= "+attackReduceMotionAmount.getValue());
         }
      }
      if (this.mode.getSelected().equalsIgnoreCase("SetXZMotion") && setXZMotion_NoAttacking.getBoolean()) {
         setXZMotion_Attacked = true;
      }
   }
   @EventTarget
   public void onEventTicks(EventTick eventTick) {

      if (mc.thePlayer != null && mc.theWorld != null && this.mode.getSelected().equals("PolarReduce")) {
         ms++;
      }

   }

   @EventTarget
   public void onEventReadPacket(EventReadPacket eventReadPacket) {
      Packet packet = eventReadPacket.getPacket();
      if (this.mode.getSelected().equals("PolarReduce")) {
         if (KillAura.target != null && packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;

            if (s12.getEntityID() == mc.thePlayer.getEntityId() && !start) {
               start = true;
               ms = 0;
            }

         }
      }
      if (this.mode.getSelected().equalsIgnoreCase("IntaveTest")) {
         posX = mc.thePlayer.posX;
         posY = mc.thePlayer.posY;
         posZ = mc.thePlayer.posZ;
      }
      if (this.mode.getSelected().equalsIgnoreCase("Grim")) {
         if (!mm.scaffold.isToggled()/* && !mm.scaffoldWalk.isToggled() && !mm.newScaffold.isToggled()*/) {
            Packet p = eventReadPacket.getPacket();
            if (p instanceof S12PacketEntityVelocity) {
               if (((S12PacketEntityVelocity) p).getEntityID() == mc.thePlayer.getEntityId()) {
                  eventReadPacket.setCancelled(true);
                  grimTCancel = 6;
               }
            }
            if (p instanceof S32PacketConfirmTransaction) {
               eventReadPacket.setCancelled(true);
               grimTCancel--;
            }
         }
      }
      if (this.mode.getSelected().equalsIgnoreCase("Basic")) {
         Packet p = eventReadPacket.getPacket();
         if (p instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)p).getEntityID() == mc.thePlayer.getEntityId()) {
            if (!(this.XZValue.getValue() > 0.0) && !(this.YValue.getValue() > 0.0)) {
               eventReadPacket.setCancelled(true);
            } else {
               ((S12PacketEntityVelocity)p).setMotionX((int)((double)((S12PacketEntityVelocity)p).getMotionX() * this.XZValue.getValue() / 100.0));
               ((S12PacketEntityVelocity)p).setMotionY((int)((double)((S12PacketEntityVelocity)p).getMotionY() * this.YValue.getValue() / 100.0));
               ((S12PacketEntityVelocity)p).setMotionZ((int)((double)((S12PacketEntityVelocity)p).getMotionZ() * this.XZValue.getValue() / 100.0));
            }
         }

         if (p instanceof S27PacketExplosion && this.ignoreExplosion.getBoolean()) {
            if (!(this.XZValue.getValue() > 0.0) && !(this.YValue.getValue() > 0.0)) {
               eventReadPacket.setCancelled(true);
            } else {
               ((S27PacketExplosion)p).setMotionX((float)((int)((double)((S27PacketExplosion)p).getMotionX() * this.XZValue.getValue() / 100.0)));
               ((S27PacketExplosion)p).setMotionY((float)((int)((double)((S27PacketExplosion)p).getMotionX() * this.YValue.getValue() / 100.0)));
               ((S27PacketExplosion)p).setMotionZ((float)((int)((double)((S27PacketExplosion)p).getMotionX() * this.XZValue.getValue() / 100.0)));
            }
         }
      }

      if (packet instanceof S29PacketSoundEffect && this.hitBug.getBoolean()) {
         S29PacketSoundEffect soundEffect = (S29PacketSoundEffect)packet;
         if (soundEffect.getSoundName().equalsIgnoreCase("game.player.hurt") || soundEffect.getSoundName().equalsIgnoreCase("game.player.die")) {
            eventReadPacket.setCancelled(true);
         }
      }

      if (packet instanceof S12PacketEntityVelocity && this.mode.getSelected().equals("Spoof")) {
         S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity)packet;
         if (s12PacketEntityVelocity.getEntityID() == mc.thePlayer.getEntityId()) {
            eventReadPacket.setCancelled(true);
            mc.getNetHandler()
                    .addToSendQueue(
                            new C03PacketPlayer.C04PacketPlayerPosition(
                                    mc.thePlayer.posX + (double)s12PacketEntityVelocity.getMotionX() / 8000.0,
                                    mc.thePlayer.posY + (double)s12PacketEntityVelocity.getMotionY() / 8000.0,
                                    mc.thePlayer.posZ + (double)s12PacketEntityVelocity.getMotionZ() / 8000.0,
                                    false
                            )
                    );
         }
      }
   }

   @EventTarget
   public void onSendPacket(EventSendPacket eventSendPacket) {
      Packet packet = eventSendPacket.getPacket();
      switch (mode.getSelected()) {
         case "IntaveTest": {
            if(mc.thePlayer.hurtTime > 7 && packet instanceof C0FPacketConfirmTransaction) {
               eventSendPacket.setCancelled(true);
            }
            break;
         }
         case "Grim2": {
            if(mc.thePlayer.hurtTime != 0) {
               ((C03PacketPlayer) packet).setY(mc.thePlayer.posY - 2);
            }
            break;
         }
      }
   }

   @EventTarget
   public void onEventSilentMove(EventSilentMove eventSilentMove) {
      String var2 = this.mode.getSelected();
      switch(var2) {
         case "BuzzReverse": {
            try {
               if (mc.thePlayer.hurtTime == 7)
                  MoveUtil.multiplyXZ(-1.0D);
            } catch (Exception exception) {}
            break;
         }
         case "Legit":
            if (mc.thePlayer.hurtTime > 0 && mm.killAura.isToggled() && KillAura.target != null) {
               ArrayList<Vec3> vec3s = new ArrayList<>();
               HashMap<Vec3, Integer> map = new HashMap<>();
               Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
               Vec3 onlyForward = PlayerUtil.getPredictedPos(false, KillAura.target, 1.0F, 0.0F).add(playerPos);
               Vec3 strafeLeft = PlayerUtil.getPredictedPos(false, KillAura.target, 1.0F, 1.0F).add(playerPos);
               Vec3 strafeRight = PlayerUtil.getPredictedPos(false, KillAura.target, 1.0F, -1.0F).add(playerPos);
               map.put(onlyForward, 0);
               map.put(strafeLeft, 1);
               map.put(strafeRight, -1);
               vec3s.add(onlyForward);
               vec3s.add(strafeLeft);
               vec3s.add(strafeRight);
               Vec3 targetVec = new Vec3(KillAura.target.posX, KillAura.target.posY, KillAura.target.posZ);
               vec3s.sort(Comparator.comparingDouble(targetVec::distanceXZTo));
               if (!mc.thePlayer.movementInput.sneak) {
                  System.out.println(map.get(vec3s.get(0)));
                  mc.thePlayer.movementInput.moveStrafe = (float)map.get(vec3s.get(0)).intValue();
               }
            }
            break;
         case "Intave":
            if (this.jumpIntave.getBoolean() && mc.thePlayer.hurtTime == 9 && mc.thePlayer.onGround && this.counter++ % 2 == 0) {
               mc.thePlayer.movementInput.jump = true;
            }
            break;
         case "Test":
            if (mc.thePlayer.hurtTime > 2) {
               MoveUtil.setSpeed(0.01F);
               if (mc.thePlayer.hurtTime == 9 && mc.thePlayer.onGround) {
                  mc.thePlayer.movementInput.jump = true;
               }
            }
            break;
      }
   }

   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
      if (this.mode.getSelected().equalsIgnoreCase("IntaveTest") && mc.thePlayer.hurtTime == 8) {
         mc.thePlayer.setPosition(posX, posY, posZ);
      }
      if (this.mode.getSelected().equals("Reverse")
              && this.reverseStrafe.getBoolean()
              && (double)mc.thePlayer.hurtTime <= this.reverseStart.getValue()
              && mc.thePlayer.hurtTime > 0) {
         MoveUtil.strafe();
      }
   }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      this.setSuffix(this.mode.getSelected(),true);
      String var2 = this.mode.getSelected();

      switch(var2) {
         case "Intave":{
            if (mc.objectMouseOver == null)
               return;

            if(mc.thePlayer.hurtTime <= 6 && mc.thePlayer.isSwingInProgress && MovingObjectPosition.entityHit !=null&& mc.thePlayer.hurtTime>0){
               if (lreverse.getBoolean() && mc.thePlayer.isJumping){
                  mc.thePlayer.motionX = -Math.sin(Math.toRadians((mc.thePlayer.rotationYaw))) * 0.02f;
                  mc.thePlayer.motionZ = Math.cos(Math.toRadians((mc.thePlayer.rotationYaw))) * 0.02f;
               }
               if (yreducer.getBoolean() && mc.thePlayer.isJumping){
                  mc.thePlayer.motionY *= 1-yreduce.getValue();
               }
            }
         }
         case "Grim2": {
            if(mc.thePlayer.hurtTime != 0) {
               mc.thePlayer.setPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
            }
            break;
         }
         case "TickZero": {
            if(mc.thePlayer.hurtTime == karhuStart.getValue()) {
               MoveUtil.multiplyXZ(0);
               mc.thePlayer.motionY = tickZeroY.getBoolean() ? 0 : mc.thePlayer.motionY;
            }
            break;
         }
         case "PushGround":
            this.pushGround();
            break;
         case "Push":
            this.push();
            break;
         case "Reverse":
            this.reverse();
            break;
         case "PolarReduce":
            if (start) {

               if (ms >= 20) {
                  start = false;
                  ms = 0;
               }

               if (mc.thePlayer.motionY <= -0.10) {
                  ticks++;
                  if (ticks % 2 == 0) {
                     mc.thePlayer.motionY = -0.1;
                     mc.thePlayer.jumpMovementFactor = 0.0265f;
                  } else {
                     mc.thePlayer.motionY = -0.16;
                     mc.thePlayer.jumpMovementFactor = 0.0265f;
                  }
               } else {
                  ticks = 0;
               }

            }
            break;
         case "Polar" :
            polar_Attacked = mc.thePlayer.isSwingInProgress;

            if (
                    mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)
                            && mc.thePlayer.hurtTime > 0
                            && !polar_Attacked
            ) {
               mc.thePlayer.motionX *= 0.45D;
               mc.thePlayer.motionZ *= 0.45D;
               mc.thePlayer.setSprinting(false);
            }

            polar_Attacked = false;
            break;
         case "SetXZMotion" :
            if (setXZMotion_Attacked && !mc.thePlayer.isSwingInProgress && setXZMotion_NoAttacking.getBoolean()) {
               setXZMotion_Attacked = false;
            }
            if (!setXZMotion_NoAttacking.getBoolean() || !setXZMotion_Attacked) {
               if (
                       mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)
                               && mc.thePlayer.hurtTime > 0) {
                  mc.thePlayer.motionX *= setXZMotion_Amount.getValue();
                  mc.thePlayer.motionZ *= setXZMotion_Amount.getValue();
                  if (setXZMotion_cancelSprint.getBoolean()) {
                     mc.thePlayer.setSprinting(false);
                  }
               }

            }
      }
   }
   private void reverse() {
      if ((double)mc.thePlayer.hurtTime == this.reverseStart.getValue()) {
         mc.thePlayer.motionX *= -1.0;
         mc.thePlayer.motionZ *= -1.0;
         if (this.reverseStrafe.getBoolean() && (double)mc.thePlayer.hurtTime <= this.reverseStart.getValue() && mc.thePlayer.hurtTime > 0) {
            MoveUtil.strafe();
         }
      }
   }

   private void push() {
      if ((double)mc.thePlayer.hurtTime <= Math.max(this.pushStart.getValue(), this.pushEnd.getValue())
              && (double)mc.thePlayer.hurtTime >= Math.min(this.pushStart.getValue(), this.pushEnd.getValue())) {
         mc.thePlayer.moveFlying(0.0F, 0.98F, (float)(this.pushXZ.getValue() / 100.0));
         if (this.pushOnGround.getBoolean()) {
            mc.thePlayer.onGround = true;
         }
      }
   }

   private void pushGround() {
      if (mc.thePlayer.hurtTime > 0) {
         mc.thePlayer.onGround = true;
      }
   }
}
