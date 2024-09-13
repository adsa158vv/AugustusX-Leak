package net.augustus.utils;

import net.augustus.Augustus;
import net.augustus.modules.combat.KillAura;
import net.augustus.utils.interfaces.MC;
import net.augustus.utils.interfaces.MM;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

public class MoveUtil implements MC, MM {
   public static void setMotion(double speed, double strafeDegree, float yaw) {
      setMotion(speed, strafeDegree, yaw, false);
   }

   public static void setMotion(double speed, double strafeDegree, float yaw, boolean ignoreSprint) {
      float strafe = mc.thePlayer.moveStrafing;
      float forward = mc.thePlayer.moveForward;
      float friction = (float)speed;
      if (strafe != 0.0F && forward != 0.0F) {
         if (strafe > 0.0F) {
            yaw = (float)(forward > 0.0F ? (double)yaw - strafeDegree : (double)yaw + strafeDegree);
         } else {
            yaw = (float)(forward > 0.0F ? (double)yaw + strafeDegree : (double)yaw - strafeDegree);
         }

         strafe = 0.0F;
      }

      float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
      float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
      mc.thePlayer.motionX = strafe * friction * f2 - forward * friction * f1;
      mc.thePlayer.motionZ = forward * friction * f2 + strafe * friction * f1;
      if (mc.thePlayer.isSprinting() && !ignoreSprint) {
         float f = yaw * (float) (Math.PI / 180.0);
         mc.thePlayer.motionX -= MathHelper.sin(f) * 0.2F;
         mc.thePlayer.motionZ += MathHelper.cos(f) * 0.2F;
      }
   }

   public static double[] getMotion(double speed, float strafe, float forward, float yaw) {
      float friction = (float)speed;
      float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
      float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
      double motionX = strafe * friction * f2 - forward * friction * f1;
      double motionZ = forward * friction * f2 + strafe * friction * f1;
      return new double[]{motionX, motionZ};
   }

   public static void setMotion2(double speed, double strafeDegree, float yaw) {
      float strafe = mc.thePlayer.moveStrafing;
      float forward = mc.thePlayer.moveForward;
      float friction = (float)speed;
      if (strafe != 0.0F && forward != 0.0F) {
         if (strafe > 0.0F) {
            yaw = (float)(forward > 0.0F ? (double)yaw - strafeDegree : (double)yaw + strafeDegree);
         } else {
            yaw = (float)(forward > 0.0F ? (double)yaw + strafeDegree : (double)yaw - strafeDegree);
         }

         strafe = 0.0F;
      }

      float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
      float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
      mc.thePlayer.motionX = strafe * friction * f2 - forward * friction * f1;
      mc.thePlayer.motionZ = forward * friction * f2 + strafe * friction * f1;
   }

   public static double[] addMotion(double speed, float yaw) {
      float strafe = 0.0F;
      float forward = mc.thePlayer.moveForward;
      float friction = (float)speed;
      float f = strafe * strafe + forward * forward;
      if (f >= 1.0E-4F) {
         f = MathHelper.sqrt_float(f);
         if (f < 1.0F) {
            f = 1.0F;
         }

         f = friction / f;
         strafe *= f;
         forward *= f;
         float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
         float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);
         return new double[]{(double)(strafe * f2 - forward * f1), (double)(forward * f2 + strafe * f1)};
      } else {
         return new double[]{0.0, 0.0};
      }
   }
   public static boolean noMoveKey() {
      return (
              !mc.gameSettings.keyBindForward.isKeyDown() &&
                      !mc.gameSettings.keyBindBack.isKeyDown() &&
                      !mc.gameSettings.keyBindLeft.isKeyDown() &&
                      !mc.gameSettings.keyBindRight.isKeyDown()
      );
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public static boolean isMoving() {
      return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F && !mc.thePlayer.isCollidedHorizontally;
   }

   public static void setSpeed(float f2) {
      mc.thePlayer.motionX = -(Math.sin(direction()) * (double)f2);
      mc.thePlayer.motionZ = Math.cos(direction()) * (double)f2;
   }
   public static void crosSine_Strafe(double speed) {
      if (!isMoving()) return;

      double rad = Math.toRadians(getYaw()); // 转换 direction 到弧度，如果 direction 已经是弧度则不需要此行
      mc.thePlayer.motionX = -Math.sin(rad) * speed;
      mc.thePlayer.motionZ = Math.cos(rad) * speed;
   }

   public static void setSpeed(float f2, boolean strafe) {
      double d = Math.toRadians(getYaw(strafe));
      mc.thePlayer.motionX = -(Math.sin(d) * (double)f2);
      mc.thePlayer.motionZ = Math.cos(d) * (double)f2;
   }

   public static void strafe(double d) {
      if (isMoving()) {
         double direction = direction();
         mc.thePlayer.motionX = -Math.sin(direction) * d;
         mc.thePlayer.motionZ = Math.cos(direction) * d;
      }
   }

   public static void strafe() {
      strafe(getSpeed());
   }

   public static void strafeMatrix() {
      double speed = getSpeed();
      if (isMoving()) {
         float f = getYaw();
         if (mc.thePlayer.moveForward < 0.0F) {
            f += 180.0F;
         } else {
            float f2 = 1.0F;
            if (mc.thePlayer.moveForward < 0.0F) {
               f2 = -0.5F;
            } else if (mc.thePlayer.moveForward > 0.0F) {
               f2 = 0.5F;
            }

            if (mc.thePlayer.moveStrafing > 0.0F) {
               f -= 90.0F * f2;
            }

            if (mc.thePlayer.moveStrafing < 0.0F) {
               f += 90.0F * f2;
            }
         }

         double direction = Math.toRadians(f);
         mc.thePlayer.motionX = -Math.sin(direction) * speed;
         mc.thePlayer.motionZ = Math.cos(direction) * speed;
      }
   }

   public static double getSpeed() {
      return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
   }
   public static double getSpeed2()
   {
      return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
   }

   public static double direction() {
      float f = getYaw();
      if (mc.thePlayer.moveForward < 0.0F) {
         f += 180.0F;
      }

      float f2 = 1.0F;
      if (mc.thePlayer.moveForward < 0.0F) {
         f2 = -0.5F;
      } else if (mc.thePlayer.moveForward > 0.0F) {
         f2 = 0.5F;
      }

      if (mc.thePlayer.moveStrafing > 0.0F) {
         f -= 90.0F * f2;
      }

      if (mc.thePlayer.moveStrafing < 0.0F) {
         f += 90.0F * f2;
      }

      return Math.toRadians(f);
   }

   public static void addSpeed(double speed, boolean strafe) {
      float f = getYaw(strafe) * (float) (Math.PI / 180.0);
      mc.thePlayer.motionX -= (double)MathHelper.sin(f) * speed;
      mc.thePlayer.motionZ += (double)MathHelper.cos(f) * speed;
   }

   public static float getYaw() {
      return mm.targetStrafe.target != null && mm.targetStrafe.isToggled()
              ? mm.targetStrafe.moveYaw
              : (
              mm.killAura.isToggled() && KillAura.target != null && mm.killAura.moveFix.getBoolean()
                      ? mc.thePlayer.rotationYaw
                      : Augustus.getInstance().getYawPitchHelper().realYaw
      );
   }

   public static float getYaw(boolean strafe) {
      return strafe ? (float)Math.toDegrees(direction()) : getYaw();
   }

   public static void setSpeed2(double speed) {
      EntityPlayerSP player = mc.thePlayer;
      double yaw = getYaw();
      boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
      boolean isMovingForward = player.moveForward > 0.0F;
      boolean isMovingBackward = player.moveForward < 0.0F;
      boolean isMovingRight = player.moveStrafing > 0.0F;
      boolean isMovingLeft = player.moveStrafing < 0.0F;
      boolean isMovingSideways = isMovingLeft || isMovingRight;
      boolean isMovingStraight = isMovingForward || isMovingBackward;
      if (isMoving) {
         if (isMovingForward && !isMovingSideways) {
            yaw += 0.0;
         } else if (isMovingBackward && !isMovingSideways) {
            yaw += 180.0;
         } else if (isMovingForward && isMovingLeft) {
            yaw += 45.0;
         } else if (isMovingForward) {
            yaw -= 45.0;
         } else if (!isMovingStraight && isMovingLeft) {
            yaw += 90.0;
         } else if (!isMovingStraight && isMovingRight) {
            yaw -= 90.0;
         } else if (isMovingBackward && isMovingLeft) {
            yaw += 135.0;
         } else if (isMovingBackward) {
            yaw -= 135.0;
         }

         yaw = Math.toRadians(yaw);
         player.motionX = -Math.sin(yaw) * speed;
         player.motionZ = Math.cos(yaw) * speed;
      }
   }

   public static void multiplyXZ(double v) {
      mc.thePlayer.motionX *= v;
      mc.thePlayer.motionZ *= v;
   }

   public static float getPlayerDirection() {
      float direction = mc.thePlayer.rotationYaw;

      if (mc.thePlayer.moveForward > 0) {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 45;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 45;
         }
      } else if (mc.thePlayer.moveForward < 0) {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 135;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 135;
         } else {
            direction -= 180;
         }
      } else {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 90;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 90;
         }
      }

      return direction;
   }

   public static float getPlayerDirection(float baseYaw) {
      float direction = baseYaw;

      if (mc.thePlayer.moveForward > 0) {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 45;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 45;
         }
      } else if (mc.thePlayer.moveForward < 0) {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 135;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 135;
         } else {
            direction -= 180;
         }
      } else {
         if (mc.thePlayer.moveStrafing > 0) {
            direction -= 90;
         } else if (mc.thePlayer.moveStrafing < 0) {
            direction += 90;
         }
      }

      return direction;
   }

   public static float getMoveYaw(float yaw) {
      Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ),
              to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ),
              diff = new Vector2f(to.x - from.x, to.y - from.y);

      double x = diff.x, z = diff.y;
      if (x != 0 && z != 0) {
         yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
      }
      return yaw;
   }
   public static void forward(float length) {
      double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
      mc.thePlayer.setPosition(
              mc.thePlayer.posX + -Math.sin(yaw) * length,
              mc.thePlayer.posY,
              mc.thePlayer.posZ + Math.cos(yaw) * length);
   }
   public static double getBaseMoveSpeed() {
      double baseSpeed = 0.272;
      if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int amplifier = MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         baseSpeed *= 1.0 + 0.2 * (double)amplifier;
      }
      return baseSpeed;
   }
   public static float getSpeedBoost(float times) {
      float boost = (float)((MoveUtil.getBaseMoveSpeed() - (double)0.2875f) * (double)times);
      if (0.0f > boost) {
         boost = 0.0f;
      }
      return boost;
   }
}
