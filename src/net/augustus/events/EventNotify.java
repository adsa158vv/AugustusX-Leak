package net.augustus.events;

import lombok.Getter;
import net.augustus.modules.Module;
import net.augustus.notify.NotificationType;
import net.minecraft.network.Packet;

public class EventNotify extends Event {
   @Getter
   private String moduleName;
   @Getter
   private NotificationType type;

   public EventNotify(String moduleName , NotificationType type) {
      this.moduleName = moduleName;
      this.type = type;
   }



}
