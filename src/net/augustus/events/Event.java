package net.augustus.events;

import net.lenni0451.eventapi.events.IEvent;

public abstract class Event implements IEvent {
   private boolean cancelled;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }


}
