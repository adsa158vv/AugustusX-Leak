package net.augustus.modules.movement;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.KillAura;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.*;
import net.augustus.utils.skid.fdpclient.MovementUtils;
import net.augustus.utils.skid.tenacity.TimerUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import viamcp.ViaMCP;

import static java.lang.Math.*;


public class Speed extends Module {
   public StringValue mode = new StringValue(
           1, "Mode", this, "GroundStrafe", new String[]{"Vanilla", "VanillaHop", "LegitHop", "LegitAbuse", "Matrix", "TeleportAbuse", "NCP", "Test", "Verus", "NoCheatMinus", "GamerCheatOG", "GamerCheatBHop", "CheatmineSafe", "CheatmineRage", "NCPRace", "NCPWtf", "NCPLow", "GroundStrafe", "Strafe", "GrimCollide", "FixedStrafe", "IntaveStrafe","IntaveTimer","BlocksMC","Karhu","CubeCraft","Hypixel","Polar","Vulcan","AAC"});
   public StringValue aacMode = new StringValue(112066, "AACMode", this, "AAC", new String[]{
           "AAC",
           "AAC3.5.0",
           "AAC2",
           "AAC4",
           "AAC6",
           "AAC7",
           "LowHop2",
           "LowHop3"
   });

   public StringValue hyp_bypassMode = new StringValue(2,"BypassMode", this, "Latest",new String[]{"Latest", "Legit", "GroundStrafe"});
   public DoubleValue vanillaSpeed = new DoubleValue(5280, "Speed", this, 1.0, 0.0, 10.0, 2);
   public DoubleValue vanillaHeight = new DoubleValue(6192, "Height", this, 0.2, 0.01, 0.42, 2);
   public DoubleValue dmgSpeed = new DoubleValue(7334, "DMGSpeed", this, 1.0, 0.0, 2.0, 2);
   public DoubleValue timerSpeed = new DoubleValue(13344, "Timer", this, 1.0, 0.0, 10.0, 3);
   public BooleanValue intaveTimer_NoHurt = new BooleanValue(13341,"NoHurt",this,true);

   public BooleanValue verus_damageBoost = new BooleanValue(3226, "DamageBoost", this, false);
   public BooleanValue vanilla_Strafe = new BooleanValue(15557, "Strafe", this, false);

   public BooleanValue grimCollideCheckKeyPressed = new BooleanValue(13429, "CheckKeyPressed", this, true);


   public BooleanValue hyp_damageBoost = new BooleanValue(3,"DamageBoost", this,true);

   public BooleanValue hyp_sussyPacket = new BooleanValue(4,"SussyPacket",this, false);


   public BooleanValue hyp_fallingStrafe = new BooleanValue(5,"FallingDamageStrafe", this, false);

   public BooleanValue hyp_fastFall = new BooleanValue(6,"FastFall", this,false);


   public BooleanValue hyp_sneakStrafe = new BooleanValue(7,"SneakStrafe", this,true);

   public BooleanValue hyp_glide = new BooleanValue(8,"Glide", this,true);
   //public BooleanValue grimCollideNoAir = new BooleanValue(15896,"NoAir",this,true);
   public BooleanValue VulcanYport = new BooleanValue(112180,"VulcanY-Port",this,false);
   public BooleanValue vuclanSlow = new BooleanValue(112181,"VulcanSlow",this,false);
   public BooleanValue onlyHop = new BooleanValue(112179,"VulcanHop",this,false);
   public BooleanValue fixMove = new BooleanValue(9, "FixMovement", this , false);
   public BooleanValue disonWorld = new BooleanValue(577, "DisableOnWorld", this, false);

   public int stage = 0;

   public boolean collided = false;
   private int vulcanTicks;
   private double speed = 0D;

   public double less = 0D;
   private boolean isInCombat = false;

   private final TimeHelper lastCheck = new TimeHelper();

   private boolean first;
   private double lastDist;
   private int offGroundTicks;
   private int motionDelay;
   private float getYaw;
   //
   public TimerUtil polar_TimeUtil = new TimerUtil();

   public int polar_Ticks = 0;


   private boolean polar_Start = false;
   //Polar
   private boolean aac_LegitHop = true;
   private boolean aac_WaitForGround = false;
   //aac



   public Speed() {
      super("Speed", new Color(74, 133, 93), Categorys.MOVEMENT);
   }

   @Override
   public void onWorld(EventWorld event) {
      super.onWorld(event);
      isInCombat = false;

      if (disonWorld.getBoolean()) this.setToggled(false);
      if (mode.getSelected().equals("AAC")) {
         if (aacMode.getSelected().equals("AAC3.5.0") && mc.thePlayer.onGround) {
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.motionX = mc.thePlayer.motionZ;
         }
      }
   }

   @Override
   public void onEnable() {

      if (mode.getSelected().equals("AAC")) {
         if (aacMode.getSelected().equals("AAC3.5.0") && mc.thePlayer.onGround) {
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.motionX = mc.thePlayer.motionZ;
         }
      }
      if (!(mode.getSelected().equals("GrimCollide"))) {
         this.lastDist = 0.0D;
         first = true;
         boolean player = (mc.thePlayer == null);
         this.collided = !player && mc.thePlayer.isCollidedHorizontally;

         if (mc.thePlayer != null && (mode.getSelected().equals("NoCheatMinus"))) {
            this.speed = SigmaMoveUtils.defaultSpeed();
         }
         this.less = 0.0D;
         stage = 2;
         if (mc.thePlayer != null && ((mode.getSelected().equals("Matrix") || mode.getSelected().equals("NCPLow")))) {
            mc.thePlayer.jumpMovementFactor = 0.02F;
            mc.thePlayer.setSpeedInAir(0.02F);
            mc.thePlayer.setSpeedOnGround(0.1F);
         }
      }
      mc.getTimer().timerSpeed = 1.0F;
      isInCombat = false;
      polar_Ticks = 0;
      polar_Start = false;

   }

   @Override
   public void onPreDisable() {
      super.onPreDisable();

   }

   @Override
   public void onDisable() {
      if (!(mode.getSelected().equals("GrimCollide"))) {

         if (mc.thePlayer != null && ((mode.getSelected().equals("Matrix") || mode.getSelected().equals("NCPLow")))) {
            mc.thePlayer.jumpMovementFactor = 0.02F;
            mc.thePlayer.setSpeedInAir(0.02F);
            mc.thePlayer.setSpeedOnGround(0.1F);
         }
      }

       mc.getTimer().timerSpeed = 1.0F;
      isInCombat = false;
      if (this.mode.getSelected().equalsIgnoreCase("Hypixel")) {
         mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
      }
      polar_Ticks = 0;
      polar_Start = false;
      if (mode.getSelected().equals("AAC")) {
         mc.thePlayer.speedInAir = 0.02f;
         mc.thePlayer.jumpMovementFactor = 0.02f;
      }
   }
   public void setMotionY(int offGroundTicks) {
      if (offGroundTicks == 4) {
         mc.thePlayer.motionY = mc.thePlayer.motionY - 0.03;
      } else if (offGroundTicks == 6) {
         mc.thePlayer.motionY = mc.thePlayer.motionY - 0.084;
      } else {
         // 保持原有的motionY值不变
      }
   }
   @EventTarget
   public void onReadPacket(EventReadPacket eventReadPacket) {
      if (!this.mode.getSelected().equalsIgnoreCase("Polar")) return;
      Packet p = eventReadPacket.getPacket();

      if (p != null && p instanceof S12PacketEntityVelocity) {

         S12PacketEntityVelocity s = (S12PacketEntityVelocity) p;

         if (s.getEntityID() == mc.thePlayer.getEntityId()) {
            polar_Start = true;
            polar_TimeUtil.reset();
         }

      }

   }

   @EventTarget
   public void onUpdate(EventUpdate eventUpdate) {
      if (!this.mode.getSelected().equalsIgnoreCase("Polar")) return;
      if (polar_Start) {

         if (this.polar_TimeUtil.hasTimeElapsed(20)) {
            polar_Start = false;
         }

         if (mc.thePlayer.motionY <= -0.10) {
            polar_Ticks++;
            if (polar_Ticks % 2 == 0) {
               mc.thePlayer.motionY = -0.1;
               mc.thePlayer.jumpMovementFactor = 0.0265f;
            } else {
               mc.thePlayer.motionY = -0.16;
               mc.thePlayer.jumpMovementFactor = 0.0265f;
            }
         } else {
            polar_Ticks = 0;
         }
      }
   }
   @EventTarget
   public void onEventTick(EventTick eventTick) {
      if (mm.targetStrafe.target != null && mm.targetStrafe.isToggled()) {
         getYaw = mm.targetStrafe.moveYaw;
      } else if (mm.killAura.isToggled() && KillAura.target != null && mm.killAura.moveFix.getBoolean()) {
         getYaw = mc.thePlayer.rotationYaw;
      } else {
         getYaw = Augustus.getInstance().getYawPitchHelper().realYaw;
      }
      this.setSuffix(this.mode.getSelected(),true);
      if (!this.mode.getSelected().equalsIgnoreCase("IntaveTimer")) {
         mc.timer.timerSpeed = (float) this.timerSpeed.getValue();
      }
      switch (mode.getSelected()) {
         case "GrimCollide": {
            if (MoveUtil.noMoveKey() && this.grimCollideCheckKeyPressed.getBoolean()) {
               return;
            }

            int collisions = 0;
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(1.0, 1.0, 1.0); // grow方法改为expand方法
            for (Entity entity : mc.theWorld.loadedEntityList) {
               AxisAlignedBB entityBox = entity.getEntityBoundingBox();
               if (canCauseSpeed(entity) && box.intersectsWith(entityBox)) { // intersects方法改为intersectsWith方法
                  collisions++;
               }
            }


            // Grim gives 0.08 leniency per entity.
            double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
            double boost = 0.08 * collisions;
            mc.thePlayer.motionX += -Math.sin(yaw) * boost;
            mc.thePlayer.motionZ += Math.cos(yaw) * boost;

         }
      }
   }
   private boolean canCauseSpeed(Entity entity) {
      return entity != mc.thePlayer && entity instanceof EntityLivingBase && !(entity instanceof EntityArmorStand);
   }
   @EventTarget
   public void onPreMotion(EventPreMotion eventPreMotion) {
      if (this.mode.getSelected().equals("Hypixel")) {
         if (!MoveUtil.isMoving()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
         }

         if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
         } else {
            offGroundTicks += 1;
         }

         if (hyp_fallingStrafe.getBoolean() && mc.thePlayer.motionY < 0.0 && mc.thePlayer.hurtTime > 0) {
            MovementUtils.strafe();
         }

         if (hyp_fastFall.getBoolean()) {
            setMotionY(offGroundTicks);
         }

         if (hyp_damageBoost.getBoolean()) {
            if (mc.thePlayer.hurtTime == 9) {
               MovementUtils.strafe(MovementUtils.getSpeed() * 0.7f);
            }
         }

         if (hyp_sneakStrafe.getBoolean()) {
            mc.gameSettings.keyBindSneak.pressed = false;
            if (!mc.thePlayer.onGround && offGroundTicks < 5) {
               MovementUtils.strafe();
               mc.gameSettings.keyBindSneak.pressed = true;
            }
         }

         if (hyp_glide.getBoolean()) {
            if (offGroundTicks >= 11 && offGroundTicks <= 14) {
               mc.thePlayer.motionY = 0.0;
               mc.thePlayer.onGround = true;
               MovementUtils.strafe();
            }
         }

         switch (hyp_bypassMode.getSelected().toLowerCase()) {

            case "latest": {
               if (hyp_sussyPacket.getBoolean()) {
                  PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(-1, -1, -1), EnumFacing.UP));
               }

               if (mc.thePlayer.onGround) {
                  mc.thePlayer.jump();

                  if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                     MovementUtils.strafe(0.51f);
                  } else {
                     MovementUtils.strafe(0.465f);
                  }

               } else {

                  if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                     mc.thePlayer.motionX *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                     mc.thePlayer.motionZ *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                     mc.thePlayer.speedInAir = 0.02f + 0.0003f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                  }
               }
            }

            case "legit": {
               if (mc.thePlayer.onGround) {
                  mc.thePlayer.jump();
               }
            }
            case "groundstrafe": {
               if (mc.thePlayer.onGround) {
                  MovementUtils.strafe(MovementUtils.getSpeed());
                  mc.thePlayer.jump();
                  MovementUtils.strafe(MovementUtils.getSpeed());
               }
            }
         }
      }
   }

   @EventTarget
   public void onRecv(EventReadPacket e) {
      Packet packet = e.getPacket();
      if (!(mode.getSelected().equals("GrimCollide"))) {
         if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook pac = (S08PacketPlayerPosLook) e.getPacket();
            if (this.lastCheck.reached(300L)) {
               pac.yaw = mc.thePlayer.rotationYaw;
               pac.pitch = mc.thePlayer.rotationPitch;
            }
            stage = -4;
            this.lastCheck.reset();
         }
         if (this.mode.getSelected().equals("Hypixel")) {
            if (packet instanceof S12PacketEntityVelocity) {
               if (mc.thePlayer == null || (mc.theWorld != null ? mc.theWorld.getEntityByID(((S12PacketEntityVelocity) packet).getEntityID()) : null) != mc.thePlayer) {
                  return;
               }
               if (!isInCombat) {
                  return;
               }

               if (((S12PacketEntityVelocity) packet).getMotionY() / 8000.0 > 0.1) {
                  if (hyp_damageBoost.getBoolean()) {
                     mc.thePlayer.motionX = mc.thePlayer.motionX * 1.07;
                     mc.thePlayer.motionZ = mc.thePlayer.motionZ * 1.07;
                  }
               }
            }
         }
      }
   }
   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
      if (!(mode.getSelected().equals("GrimCollide"))) {
         if (mode.getSelected().equals("AAC")) {
            if (aacMode.getSelected().equals("AAC3.5.0")) {
               if (MoveUtil.isMoving() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
                  mc.thePlayer.jumpMovementFactor += 0.00208f;
                  if (mc.thePlayer.fallDistance <= 1f) {
                     if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        mc.thePlayer.motionX *= 1.0118;
                        mc.thePlayer.motionZ *= 1.0118;
                     } else {
                        mc.thePlayer.motionY -= 0.0147;
                        mc.thePlayer.motionX *= 1.00138;
                        mc.thePlayer.motionZ *= 1.00138;
                     }
                  }
               }
            }
         }
         if (!mc.thePlayer.onGround) {
            offGroundTicks++;
         } else {
            offGroundTicks = 0;
         }
         switch (mode.getSelected()) {
            case "CheatmineSafe": {
               if (mc.thePlayer.onGround) {
                  mc.thePlayer.motionY = 0.38;
                  MoveUtil.strafe();
               }
               break;
            }
            case "CheatmineRage": {
               if (mc.thePlayer.onGround) {
                  mc.thePlayer.jump();
                  mc.thePlayer.motionY = 0.35;
               }
               break;
            }
         }
      }
   }
   @EventTarget
   public void onEventJump1(EventJump eventJump) {
      if (!this.fixMove.getBoolean()) {
         eventJump.setYaw(Augustus.getInstance().yawPitchHelper.realYaw);
      }
   }
   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {

      if (!(mode.getSelected().equals("GrimCollide"))) {
         String var2 = this.mode.getSelected();
         switch (var2) {
            case "GroundStrafe": {
               groundStrafe();
               break;
            }
            case "Strafe": {
               strafe();
               break;
            }
            case "FixedStrafe": {
               fixedstrafe();
               break;
            }
            case "NCPLow": {
               ncplow();
               break;
            }
            case "NCPWtf": {
               ncpwtf();
               break;
            }
            case "NCPRace": {
               ncprace();
               break;
            }
            case "AAC": {
               String var3 = this.aacMode.getSelected();
               switch (var3) {
                  case "AAC": {
                     aacBhop();
                     break;
                  }
                  case "AAC3.5.0": {
                     break;
                  }
                  case "AAC2": {
                     aac2();
                     break;
                  }
                  case "AAC4": {
                     aac4();
                     break;
                  }
                  case "AAC6": {
                     aac6();
                     break;
                  }
                  case "AAC7": {
                     aac7();
                     break;
                  }
                  case "LowHop2": {
                     aacLowHop2();
                     break;
                  }
                  case "LowHop3": {
                     aacLowHop3();
                     break;
                  }
               }
               break;
            }
            case "GamerCheatBHop": {
               gamercheatbhop();
               break;
            }
            case "GamerCheatOG": {
               gamercheatog();
               break;
            }
            case "NoCheatMinus": {
               nocheatminus();
               break;
            }
            case "Verus":
               verus();
               break;
            case "Vanilla":
               vonground();
               break;
            case "VanillaHop":
               vbhop();
               break;
            case "LegitAbuse": {
               legitAbuse();
               break;
            }
            case "TeleportAbuse": {
               teleportAbuse();
               break;
            }
            case "Matrix":
               matrix();
               break;
            case "Test":
               test();
               break;
            case "IntaveStrafe":
               iacStrafe();
               break;
            case "Vulcan" :
               vulcan();
               break;
         }
      }
   }
   private void aacBhop() {
      if (MoveUtil.isMoving()) {
         mc.timer.timerSpeed = 1.08f;
         if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.399f;
            float f = mc.thePlayer.rotationYaw * 0.017453292f;
            mc.thePlayer.motionX -= (MathHelper.sin(f) * 0.2f);
            mc.thePlayer.motionZ += (MathHelper.cos(f) * 0.2f);
            mc.timer.timerSpeed = 2f;
         } else {
            mc.thePlayer.motionY *= 0.97;
            mc.thePlayer.motionX *= 1.008;
            mc.thePlayer.motionZ *= 1.008;
         }
      } else {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         mc.timer.timerSpeed = 1f;
      }
   }
   private void aac2(){

      if (MoveUtil.isMoving()) {
         if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            mc.thePlayer.motionX *= 1.02;
            mc.thePlayer.motionZ *= 1.02;
         } else if (mc.thePlayer.motionY > -0.2) {
            mc.thePlayer.jumpMovementFactor = 0.08f;
            mc.thePlayer.motionY += 0.01431;
            mc.thePlayer.jumpMovementFactor = 0.07f;
         }
      } else {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      }
   }
   private void aac4(){

      if (MoveUtil.isMoving()) {
         if (aac_LegitHop) {
            if (mc.thePlayer.onGround) {
               mc.thePlayer.jump();
               mc.thePlayer.onGround = false;
               aac_LegitHop = false;
            }
            return;
         }
         if (mc.thePlayer.onGround) {
            mc.thePlayer.onGround = false;
            MoveUtil.strafe(0.375f);
            mc.thePlayer.jump();
            mc.thePlayer.motionY = 0.41;
         } else mc.thePlayer.speedInAir = 0.0211f;
      } else {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         aac_LegitHop = true;
      };
   }
   private void aac6(){
      mc.timer.timerSpeed = 1f;

      if (MoveUtil.isMoving()) {
         if (mc.thePlayer.onGround) {
            if (aac_LegitHop) {
               mc.thePlayer.motionY = 0.4;
               MoveUtil.strafe(0.15f);
               mc.thePlayer.onGround = false;
               aac_LegitHop = false;
               return;
            }
            mc.thePlayer.motionY = 0.41;
            MoveUtil.strafe(0.47458485f);
         }

         if (mc.thePlayer.motionY < 0 && mc.thePlayer.motionY > -0.2) mc.timer.timerSpeed = (float) (1.2 + mc.thePlayer.motionY);

         mc.thePlayer.speedInAir = 0.022151f;
      } else {
         aac_LegitHop = true;
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      }
   }
   private void aac7(){
      if (mc.thePlayer.onGround) {
         mc.thePlayer.jump();
         mc.thePlayer.motionY = 0.405;
         mc.thePlayer.motionX *= 1.004;
         mc.thePlayer.motionZ *= 1.004;
         return;
      }

      float speed = (float) (MoveUtil.getSpeed() * 1.0072);
      float yaw = (float) Math.toRadians(mc.thePlayer.rotationYaw);
      mc.thePlayer.motionX = -sin(yaw) * speed;
      mc.thePlayer.motionZ = cos(yaw) * speed;
   }
   private void aacLowHop2(){
      mc.timer.timerSpeed = 1f;

      if (MoveUtil.isMoving()) {
         mc.timer.timerSpeed = 1.09f;
         if (mc.thePlayer.onGround) {
            if (aac_LegitHop) {
               mc.thePlayer.jump();
               aac_LegitHop = false;
               return;
            }
            mc.thePlayer.motionY = 0.343;
            MoveUtil.strafe(0.534f);
         }
      } else {
         aac_LegitHop = true;
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      }
   }
   private void aacLowHop3(){
      if (MoveUtil.isMoving()) {
         if (mc.thePlayer.hurtTime <= 0) {
            if (mc.thePlayer.onGround) {
               aac_WaitForGround = false;
               if (!aac_LegitHop) aac_LegitHop = true;
               mc.thePlayer.jump();
               mc.thePlayer.motionY = 0.41;
            } else {
               if (aac_WaitForGround) return;
               if (mc.thePlayer.isCollidedHorizontally) return;
               aac_LegitHop = false;
               mc.thePlayer.motionY -= 0.0149;
            }
            if (!mc.thePlayer.isCollidedHorizontally){
               if (aac_LegitHop) {
                  MoveUtil.forward(0.0016F);
               }else MoveUtil.forward(0.001799F);
            }
         } else {
            aac_LegitHop = true;
            aac_WaitForGround = true;
         }
      } else {
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.motionX = 0.0;
      }

      double speed = MoveUtil.getSpeed();
      mc.thePlayer.motionX = -(sin(MoveUtil.getPlayerDirection()) * speed);
      mc.thePlayer.motionZ = cos(MoveUtil.getPlayerDirection()) * speed;
   }
   private void iacStrafe() {
      mc.gameSettings.keyBindJump.pressed = true;
      if (this.offGroundTicks >= 10 && this.offGroundTicks % 5 == 0) {
         MoveUtil.strafe();
      }
   }
   private void vulcan(){
      this.vulcanTicks = Speed.mc.thePlayer.onGround ? 0 : ++this.vulcanTicks;
      if (onlyHop.getBoolean()) {
         switch (this.vulcanTicks) {
            case 0: {
               if (this.isMoving()) {
                  Speed.mc.thePlayer.jump();
                  Speed.mc.timer.timerSpeed = 1.2f;
               } else {
                  Speed.mc.timer.timerSpeed = 1.0f;
               }
               MoveUtil.strafe(Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (double)0.6f : (double)0.485f);
               break;
            }
            case 1:
            case 2: {
               MoveUtil.strafe();
               Speed.mc.timer.timerSpeed = 1.0f;
               break;
            }
            case 5: {
               Speed.mc.thePlayer.motionY = -0.175;
            }
         }
      }
      if (vuclanSlow.getBoolean()){
         this.vulcanTicks = Speed.mc.thePlayer.onGround ? 0 : ++this.vulcanTicks;
         switch (this.vulcanTicks) {
            case 0: {
               if (this.isMoving()) {
                  Speed.mc.thePlayer.jump();
               }
               MoveUtil.strafe(Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (double)0.6f : (double)0.485f);
               break;
            }
            case 1:
            case 2:
            case 8: {
               MoveUtil.strafe();
            }
         }
      }
      if (VulcanYport.getBoolean()){
         if (Speed.mc.thePlayer.onGround) {
            this.vulcanTicks = 0;
            Speed.mc.thePlayer.jump();
            Speed.mc.timer.timerSpeed = 1.07f;
            MoveUtil.strafe(Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6 : 0.485);
         } else {
            ++this.vulcanTicks;
            Speed.mc.timer.timerSpeed = 1.0f;
         }
         if ((double)Speed.mc.thePlayer.fallDistance > 0.1) {
            Speed.mc.thePlayer.motionY = -1337.0;
         }
         if (3 <= this.vulcanTicks) MoveUtil.strafe();
      }
   }
   private void groundStrafe() {
      if (mc.thePlayer.onGround) {
         mc.thePlayer.jump();
         MoveUtil.strafe();
      }
   }


   private void strafe() {
      if (mc.thePlayer.onGround) {
         mc.thePlayer.jump();
      }
      MoveUtil.strafe();
   }

   private void fixedstrafe() {
      if (mc.thePlayer.onGround) {
         mc.thePlayer.jump();
      }
      MoveUtil.setSpeed((float) SigmaMoveUtils.defaultSpeed());
   }

   private void ncpwtf() {
      boolean hasSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed);
      double speedAmplifier = 0;
      if(hasSpeed)
         speedAmplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
      MoveUtil.strafe();

      mc.thePlayer.motionX *= 0.0045 + 1 + speedAmplifier * 0.02F;
      mc.thePlayer.motionZ *= 0.0045 + 1 + speedAmplifier * 0.02F;

      boolean pushDown = true;

      if(mc.thePlayer.onGround && isMoving() && !mc.gameSettings.keyBindJump.pressed) {
         float boost = (float) (speedAmplifier * 0.065F);
         mc.thePlayer.jump();
         if(!pushDown) return;
         mc.timer.timerSpeed = 1.405F;
         MoveUtil.strafe((mc.thePlayer.ticksExisted % 10 > 7 ? 0.4F : 0.325F) + boost);
         mc.thePlayer.motionX *= 0.01 + 1 + speedAmplifier * 0.175F;
         mc.thePlayer.motionZ *= 0.01 + 1 + speedAmplifier * 0.175F;
         mc.thePlayer.cameraPitch = 0;
         mc.thePlayer.cameraYaw = 0;
      } else if (!mc.thePlayer.onGround && mc.thePlayer.motionY > 0.3 && !mc.gameSettings.keyBindJump.pressed) {
         mc.timer.timerSpeed = 0.85F;
         mc.thePlayer.motionY = -0.42;
         mc.thePlayer.posY -= 0.45;
         mc.thePlayer.cameraPitch = 0;
      }

      mc.thePlayer.stepHeight = 0.5F;

      MoveUtil.strafe();
   }

   private void ncprace() {
      if (mc.thePlayer.onGround) {
         mc.thePlayer.jump();
         mc.timer.timerSpeed = 1.2F;
         MoveUtil.multiplyXZ(1.0708D);
         mc.thePlayer.moveStrafing *= 2.0F;
         return;
      }
      mc.timer.timerSpeed = 0.98F;
      mc.thePlayer.jumpMovementFactor = 0.0265F;
   }

   private void gamercheatbhop() {
      MoveUtil.setSpeed(0.6F);
      if (mc.thePlayer.isCollidedVertically) {
         MoveUtil.setSpeed(0.2F);
         mc.thePlayer.motionY = 0.35D;
      }
   }

   private void gamercheatog() {
      double y1;
      MoveUtil.setSpeed(0.56F);
      mc.thePlayer.motionY = 0.0D;
      if (mc.thePlayer.ticksExisted % 3 == 0) {
         double d = mc.thePlayer.posY - 1.0E-10D;
         mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, d, mc.thePlayer.posZ, true));
      }
      y1 = mc.thePlayer.posY + 1.0E-10D;
      mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
   }

   private void nocheatminus() {
      if (mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F)
         this.speed = SigmaMoveUtils.defaultSpeed();
      if (stage == 1 && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)) {
         this.speed = 0.25D + SigmaMoveUtils.defaultSpeed() - 0.01D;
      } else if (!SigmaBlockUtils.isInLiquid() && stage == 2 && mc.thePlayer.isCollidedVertically && SigmaMoveUtils.isOnGround(0.001D) && (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)) {
         mc.thePlayer.motionY = 0.4D;
         //em.setY(0.4D);
         mc.thePlayer.jump();
         this.speed *= 2.149D;
      } else if (stage == 3) {
         double difference = 0.66D * (this.lastDist - SigmaMoveUtils.defaultSpeed());
         this.speed = this.lastDist - difference;
      } else {
         List<AxisAlignedBB> collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0D, mc.thePlayer.motionY, 0.0D));
         if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) &&
                 stage > 0)
            if (1.35D * SigmaMoveUtils.defaultSpeed() - 0.01D > this.speed) {
               stage = 0;
            } else {
               stage = (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) ? 1 : 0;
            }
         this.speed = this.lastDist - this.lastDist / 159.0D;
      }
      this.speed = Math.max(this.speed, SigmaMoveUtils.defaultSpeed());
      if (stage > 0) {
         if (SigmaBlockUtils.isInLiquid())
            this.speed = 0.1D;
         MoveUtil.setSpeed((float) speed);
      }
      if (mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F)
         stage++;
   }

   private void verus() {
      MoveUtil.setSpeed2(0.292F);
      if (this.verus_damageBoost.getBoolean() && mc.thePlayer.hurtTime != 0 && mc.thePlayer.fallDistance < 3.0F) {
         MoveUtil.setSpeed2((float)this.dmgSpeed.getValue());
      } else {
         MoveUtil.setSpeed2(0.292F);
      }

      if (this.canJump()) {
         mc.thePlayer.jump();
      } else {
         mc.thePlayer.jumpMovementFactor = 0.1F;
      }
   }

   private void vonground() {
      if (this.isMoving()) {
         MoveUtil.setSpeed((float)(0.1 * this.vanillaSpeed.getValue()), this.vanilla_Strafe.getBoolean());
      }
   }

   private void vbhop() {
      if (this.canJump()) {
         mc.thePlayer.motionY = this.vanillaHeight.getValue();
         if (this.vanilla_Strafe.getBoolean()) {
            MoveUtil.strafe();
         }
      } else if (this.isMoving()) {
         MoveUtil.setSpeed((float)(0.1 * this.vanillaSpeed.getValue()), this.vanilla_Strafe.getBoolean());
      }
   }

   private void legitAbuse() {
      if(isMoving()) {
         if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
         }
         KeyBinding[] gameSettings = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};
         int[] down = {0};
         Arrays.stream(gameSettings).forEach(keyBinding -> down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0));
         boolean active = (down[0] == 1);
         if (!active)
            return;
         double increase = mc.thePlayer.onGround ? 0.0026000750109401644D : 5.199896488849598E-4D;
         double yaw = MoveUtil.direction();
         mc.thePlayer.motionX += -sin((float) yaw) * increase;
         mc.thePlayer.motionZ += MathHelper.cos((float) yaw) * increase;
      }
   }

   private void teleportAbuse() {
      if(mc.thePlayer.onGround) {
         mc.thePlayer.jump();
         if(!first) {
            mc.timer.timerSpeed = 30F;
         } else {
            mc.timer.timerSpeed = 5F;
         }
         first = false;
      } else {
         mc.timer.timerSpeed = 0.3F;
      }
   }

   private void matrix() {

      if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0) {
         return;
      }

      if (MoveUtil.noMoveKey()) {
         return;
      }
      float f = mc.thePlayer.rotationYaw * (float) (Math.PI / 180.0);
      if (mc.thePlayer.movementInput.moveForward > 0.0F && mc.thePlayer.movementInput.moveStrafe != 0.0F) {
         mc.thePlayer.motionX -= MathHelper.sin(f) * 0.22F;
         mc.thePlayer.motionZ += MathHelper.cos(f) * 0.22F;
      } else {
         mc.thePlayer.motionX -= MathHelper.sin(f) * 0.202F;
         mc.thePlayer.motionZ += MathHelper.cos(f) * 0.202F;
      }

      mc.thePlayer.motionY -= 0.009999F;
      if (mc.thePlayer.onGround) {

         MoveUtil.strafeMatrix();
      } else if (mc.thePlayer.movementInput.moveForward > 0.0F && mc.thePlayer.movementInput.moveStrafe != 0.0F) {
         mc.thePlayer.setSpeedInAir(0.02F);
      } else {
         mc.thePlayer.setSpeedInAir(0.0208F);
      }
   }

   private void test() {
      if (mc.thePlayer.onGround) {

      }

      if (MoveUtil.isMoving() && KillAura.target == null && !mm.longJump.isToggled()) {
         if (mc.thePlayer.isUsingItem()) {
            if (mc.thePlayer.isBlocking()) {
               mc.getTimer().timerSpeed = 1.23F;
            } else if (mc.getTimer().timerSpeed > 1.0F) {
               mc.getTimer().timerSpeed = 1.0F;
            }
         } else {
            mc.getTimer().timerSpeed = 1.23F;
         }
      } else if (mc.getTimer().timerSpeed > 1.0F) {
         mc.getTimer().timerSpeed = 1.0F;
      }

      if (mc.thePlayer.hurtTime > 8
              && !mm.longJump.isToggled()
              && !mc.thePlayer.isBurning()
              && mc.thePlayer.fallDistance < 2.0F
              && !mc.thePlayer.isPotionActive(Potion.wither)
              && !mc.thePlayer.isPotionActive(Potion.poison)) {
         MoveUtil.addSpeed(0.4F, false);
      }


   }

   private void ncplow() {
      if (isMoving()) {
         if (mc.gameSettings.keyBindJump.pressed)
            return;
         if (mc.thePlayer.onGround) {
            this.motionDelay++;
            this.motionDelay %= 3;
            if (this.motionDelay == 0) {
               mc.thePlayer.motionY += 0.18000000715255737D;
               mc.thePlayer.motionX *= 1.2000000476837158D;
               mc.thePlayer.motionZ *= 1.2000000476837158D;
            }
         }
         if (!mc.thePlayer.onGround) {
            mc.thePlayer.motionX *= 1.0499999523162842D;
            mc.thePlayer.motionZ *= 1.0499999523162842D;
         }
         mc.thePlayer.speedInAir = 0.022F;
      }
   }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (mode.getSelected().equals("GrimCollide")) {
         if (ViaMCP.getInstance().getVersion() < 107 ) {
            mc.thePlayer.addChatMessage(new ChatComponentText(("§F[§6Speed§F][§3GrimCollide§f]§f[§5Anti§4LIQ§F] You need to ViaVersion to§4 1.9+§F to Use GrimCollide Speed!")));
            this.setToggled(false);
         }
      }
      if (!(mode.getSelected().equals("GrimCollide"))) {
         String var2 = this.mode.getSelected();
         byte var3 = -1;
         switch (var2.hashCode()) {
            case 77115:
               if (var2.equals("NCP")) {
                  var3 = 0;
               }
            default:
               switch (var3) {
                  case 0:
                     if (MoveUtil.isMoving()) {
                        if (mc.thePlayer.onGround) {
                           mc.thePlayer.jump();
                           mc.thePlayer.motionX *= 1.01;
                           mc.thePlayer.motionZ *= 1.01;
                           mc.thePlayer.setSpeedInAir(0.022F);
                        }

                        mc.thePlayer.motionY -= 9.9999E-4;
                        MoveUtil.strafe();
                     } else {
                        mc.thePlayer.motionX = 0.0;
                        mc.thePlayer.motionZ = 0.0;
                     }

                     mc.getTimer().timerSpeed = 1.0865F;
               }
         }
      }
      if (mc.thePlayer.onGround && !MoveUtil.noMoveKey()) {
         switch (mode.getSelected()) {


            case "IntaveTimer":
               if (!(mc.thePlayer.hurtTime > 0 && this.intaveTimer_NoHurt.getBoolean())) {
                  mc.thePlayer.jump();
               }

            case "BlocksMC":
               mc.thePlayer.jump();
               MoveUtil.strafe(0.48);
               break;
            case "Karhu":
               mc.thePlayer.jump();
               mc.thePlayer.motionY -= 0.135;
               break;
            case "CubeCraft":
               MoveUtil.strafe(0.4);
               mc.thePlayer.motionY = 0.42;
               break;

            default:
               break;
         }
      }
      else if (!MoveUtil.noMoveKey()) {
         switch (mode.getSelected()) {
            case "BlocksMC":
               if (!mc.thePlayer.onGround && mc.thePlayer.motionY <= 0 && mc.thePlayer.ticksExisted % 13 == 0) {
                  mc.timer.timerSpeed = 1.5F;
               } else {
                  mc.timer.timerSpeed = 1.0F;
               }
               break;
            case "IntaveTimer":
               if (!(mc.thePlayer.hurtTime > 0 && this.intaveTimer_NoHurt.getBoolean())) {
                  if (mc.thePlayer.motionY < 0.2 && mc.thePlayer.ticksExisted % 3 == 1) {
                     mc.timer.timerSpeed = 1.5F;
                  } else if (mc.thePlayer.motionY < 0.8) {
                     mc.timer.timerSpeed = 1.06F;
                  } else {
                     mc.timer.timerSpeed = 1.0F;
                  }
               }
            default:
               break;
         }
      }
   }

   @EventTarget
   public void onEventSilentMove(EventSilentMove eventSilentMove) {
      String var2 = this.mode.getSelected();
      switch(var2) {
         case "Matrix2":
         case "LegitHop":
         case "Matrix":
            if (this.isMoving()) {
               mc.thePlayer.movementInput.jump = true;
            }
      }
   }

   @EventTarget
   public void onEventMove(EventMove eventMove) {
      if (mc.thePlayer.fallDistance > 4.0F && this.mode.getSelected().equals("LegitHop")) {
         eventMove.setCancelled(true);
      }

      if (!BlockUtil.isScaffoldToggled()) {
         eventMove.setYaw(
                 mm.targetStrafe.target != null && mm.targetStrafe.isToggled()
                         ? mm.targetStrafe.moveYaw
                         : (
                         mm.killAura.isToggled() && KillAura.target != null && mm.killAura.moveFix.getBoolean()
                                 ? mc.thePlayer.rotationYaw
                                 : Augustus.getInstance().getYawPitchHelper().realYaw
                 )
         );
      }
   }

   @EventTarget
   public void onEventJump(EventJump eventJump) {
      if (!BlockUtil.isScaffoldToggled()) {
         eventJump.setYaw(
                 mm.targetStrafe.target != null && mm.targetStrafe.isToggled()
                         ? mm.targetStrafe.moveYaw
                         : (
                         mm.killAura.isToggled() && KillAura.target != null && mm.killAura.moveFix.getBoolean()
                                 ? mc.thePlayer.rotationYaw
                                 : Augustus.getInstance().getYawPitchHelper().realYaw
                 )
         );
      }
   }

   private boolean isMoving() {
      return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F && !mc.thePlayer.isCollidedHorizontally;
   }

   private boolean canJump() {
      return this.isMoving() && mc.thePlayer.onGround;
   }
}
