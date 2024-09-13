package net.lenni0451.eventapi.reflection;

import java.lang.reflect.Method;

import lombok.Getter;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.listener.IEventListener;

public class ReflectedEventListener implements IEventListener {
   @Getter
   private final Object callInstance;
   private final Class<?> eventClass;
   private final Method method;

   public ReflectedEventListener(Object callInstance, Class<?> eventClass, Method method) {
      this.callInstance = callInstance;
      this.eventClass = eventClass;
      this.method = method;
   }

   @Override
   public void onEvent(IEvent event) {
      try {
         this.method.invoke(this.callInstance, this.eventClass.cast(event));
      } catch (Throwable var3) {
         throw new RuntimeException("Error invoking reflected method in Event "+event+" ! ", var3);
      }
   }
}
