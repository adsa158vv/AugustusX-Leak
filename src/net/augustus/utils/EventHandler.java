package net.augustus.utils;

import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.manager.EventManager;

public class EventHandler {
   public static void call(IEvent event) {

         EventManager.call(event);

   }
}
