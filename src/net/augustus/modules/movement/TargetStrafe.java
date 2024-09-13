package net.augustus.modules.movement;

import java.awt.Color;
import java.util.Comparator;
import net.augustus.Augustus;
import net.augustus.events.EventClickSetting;
import net.augustus.events.EventJump;
import net.augustus.events.EventMove;
import net.augustus.events.EventSilentMove;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.AntiBot;
import net.augustus.modules.combat.KillAura;
import net.augustus.modules.misc.MidClick;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.RotationUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class TargetStrafe extends Module {
   public final BooleanValue legit = new BooleanValue(1578, "LegitStrafe", this, false);

   public final StringValue towards = new StringValue(1587, "StrafeTowards", this, "Left", new String[] {"Right", "Left"});

   public final BooleanValue clickOnly = new BooleanValue(1548, "ClickToStrafe", this, true);

   public final BooleanValue autowalk = new BooleanValue(44155, "AutoWalk", this, true);

   private final DoubleValue radius = new DoubleValue(2181, "Radius", this, 2.5, 0.1, 6.0, 1);
   private final BooleanValue killAuraTarget = new BooleanValue(3809, "KillAuraTarget", this, true);
   private final BooleanValue autoStrafe = new BooleanValue(12618, "AutoStrafe", this, true);
   private final TimeHelper timeHelper = new TimeHelper();
   public float moveYaw;
   public EntityLivingBase target = null;
   private Object[] listOfTargets;
   private boolean leftRight;
   private boolean lastSlientMove = false;

   public TargetStrafe() {
      super("TargetStrafe", new Color(52, 0, 194), Categorys.MOVEMENT);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      lastSlientMove = mm.killAura.silentMoveFix.getBoolean();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      mm.killAura.silentMoveFix.setBoolean(lastSlientMove);
   }


   @EventTarget
   public void onEventSilentMove(EventSilentMove eventSilentMove) {
      this.target = null;
      if (this.killAuraTarget.getBoolean()) {
         if (mm.killAura.isToggled()) {
            this.target = KillAura.target;
         }
      } else {
         this.listOfTargets = mc.theWorld
                 .loadedEntityList
                 .stream()
                 .filter(this::canAttacked)
                 .sorted(Comparator.comparingDouble(entity -> (double)mc.thePlayer.getDistanceToEntity(entity)))
                 .toArray();
         if (this.listOfTargets.length > 0) {
            this.target = (EntityLivingBase)this.listOfTargets[0];
         }
      }

      if (this.legit.getBoolean()) {
         if (!mm.killAura.moveFix.getBoolean()) {
            LogUtil.addAntiLIQChatMessage(this,"Please Enable the KillAura MoveFix to Use This !");
            this.setToggled(false);
            return;
         } else {

            KeyBinding key = this.towards.getSelected().equals("Left") ? mc.gameSettings.keyBindLeft : mc.gameSettings.keyBindRight;

            if ((!this.clickOnly.getBoolean() || mc.gameSettings.keyBindAttack.isPressed()) && this.target != null) {
               if (this.autowalk.getBoolean()) {
                  mc.gameSettings.keyBindForward.pressed = true;
               }
               key.pressed = true;
            } else {
               key.pressed = key.isKeyDown();
               if (this.autowalk.getBoolean()) {
                  mc.gameSettings.keyBindForward.pressed = mc.gameSettings.keyBindForward.isKeyDown();
               }
            }

         }
      }

      if (!this.legit.getBoolean()) {
         if (this.target != null && !mc.thePlayer.isOnLadder()) {
            if (mc.thePlayer.movementInput.moveStrafe > 0.0F) {
               this.leftRight = false;
            } else if (mc.thePlayer.movementInput.moveStrafe < 0.0F) {
               this.leftRight = true;
            }

            Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2.0, mc.thePlayer.posZ)).getBlock();
            if (mc.thePlayer.isCollidedHorizontally) {
               this.leftRight = !this.leftRight;
            }

            if (block instanceof BlockAir && !mc.thePlayer.onGround && this.timeHelper.reached(300L)) {
               this.leftRight = !this.leftRight;
               this.timeHelper.reset();
            }

            double radius = this.radius.getValue();
            RotationUtil rotationUtil = new RotationUtil();
            double yaw = (double) rotationUtil.middleRotation(this.target, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false)[0];
            yaw += (double) ((this.leftRight ? -1 : 1) * 45);
            double direction = Math.toRadians(yaw);
            double posX = this.target.posX + Math.sin(direction) * radius;
            double posZ = this.target.posZ - Math.cos(direction) * radius;
            this.moveYaw = rotationUtil.positionRotation(posX, this.target.posY, posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false)[0];
            if (this.autoStrafe.getBoolean()) {
               MoveUtil.strafe();
            }

         } else {
            this.moveYaw = Augustus.getInstance().getYawPitchHelper().realYaw;
         }
      }
   }

   @EventTarget
   public void onEventMove(EventMove eventMove) {
      if (this.target != null && !BlockUtil.isScaffoldToggled() && !this.legit.getBoolean()) {
         eventMove.setYaw(this.moveYaw);
      }
   }

   @EventTarget
   public void onEventJump(EventJump eventJump) {
      if (this.target != null && !BlockUtil.isScaffoldToggled() && !this.legit.getBoolean()) {
         eventJump.setYaw(this.moveYaw);
      }
   }

   private boolean canAttacked(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         if (!mc.thePlayer.canEntityBeSeen(entity)) {
            return false;
         }

         if (entity.isInvisible() || ((EntityLivingBase)entity).deathTime > 1) {
            return false;
         }

         if (entity.ticksExisted < 1) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
            return false;
         }

         if (entity.isDead) {
            return false;
         }

         if (entity instanceof EntityPlayer && mm.antiBot.isToggled() && AntiBot.isServerBot(entity)) {
            return false;
         }

         if (entity instanceof EntityPlayer && !mm.midClick.noFiends && MidClick.friends.contains(entity.getName())) {
            return false;
         }
      }

      return entity instanceof EntityLivingBase && entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) < 7.0F;
   }
}
