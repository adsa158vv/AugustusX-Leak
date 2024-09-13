package net.augustus.events;

import lombok.Setter;
import net.augustus.utils.PrePostUtil;

@Setter
public class EventPreMotion extends Event {
   public float yaw;
   public float pitch;
   public boolean ground;
   public double x;
   public double y;
   public double z;

   public EventPreMotion(float yaw, float pitch, boolean ground, double x, double y, double z) {
      this.yaw = yaw;
      this.pitch = pitch;
      this.ground = ground;
      this.x = x;
      this.y = y;
      this.z = z;
      PrePostUtil.Pre = true;
      PrePostUtil.Post = false;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public boolean onGround() {
      return this.ground;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }
}
