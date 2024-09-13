package net.augustus.events;

import lombok.Getter;
import net.augustus.notify.NotificationType;

public class EventToggleSound extends Event {
   @Getter
   private String moduleName;
   @Getter
   private boolean on;

   public EventToggleSound(String moduleName , boolean on) {
      this.moduleName = moduleName;
      this.on = on;
   }



}
