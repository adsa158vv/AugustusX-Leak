package net.augustus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.lenni0451.eventapi.reflection.EventTarget;

@Getter
@Setter
@AllArgsConstructor
public class EventMove extends Event {
   @Setter
   @Getter
   private float yaw;
   @Setter
   @Getter
   private float friction;
   public float forward=0;
   public float strafe=0;
   public boolean PRE;
   public boolean isPre() {
      return this.PRE;
   }

   public boolean isPost() {
      return !this.PRE;
   }
   public EventMove(float yaw, float friction) {
      this.yaw = yaw;
      this.friction = friction;

   }
   public EventMove(boolean PRE) {

      this.PRE = PRE;
   }


}
