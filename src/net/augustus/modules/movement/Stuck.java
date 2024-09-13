package net.augustus.modules.movement;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.awt.*;

public class Stuck extends Module {


   private final BooleanValue NoC03 = new BooleanValue(7906,"NoC03",this,true);
   private final BooleanValue NoC08 = new BooleanValue(10478,"NoC08",this,true);
   private final BooleanValue NoC0F = new BooleanValue(12208,"NoC0F",this,false);
   private final BooleanValue NoS32 = new BooleanValue(1955,"NoS32",this,false);
   private final BooleanValue NoMove = new BooleanValue(15706,"NoMove",this,false);
   private final BooleanValue NoPreMove = new BooleanValue(8277,"NoPreMove",this,false);
   private final BooleanValue NoPostMove = new BooleanValue(6070,"NoPostMove",this,false);
   private final BooleanValue NoUpdateMove = new BooleanValue(13620,"NoUpdateMove",this,false);
   private final BooleanValue disableOnWorld = new BooleanValue(7229,"DisableOnWorld",this,true);



   public Stuck() {
      super("Stuck", new Color(75, 5, 161), Categorys.MOVEMENT);
   }

   @Override
   public void onWorld(EventWorld event) {
      super.onWorld(event);
      if (disableOnWorld.getBoolean()) {
         this.setToggled(false);
      }
   }

   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
      Packet packet = eventSendPacket.getPacket();
      if (packet instanceof C03PacketPlayer && NoC03.getBoolean()){
         eventSendPacket.setCancelled(true);
      }
      if (packet instanceof C0FPacketConfirmTransaction && NoC0F.getBoolean()){
         eventSendPacket.setCancelled(true);
      }
      if (packet instanceof C08PacketPlayerBlockPlacement && NoC08.getBoolean()){
         eventSendPacket.setCancelled(true);
      }
      if (packet instanceof S32PacketConfirmTransaction && NoS32.getBoolean()){
         eventSendPacket.setCancelled(true);
      }
   }
   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (NoUpdateMove.getBoolean()){
         mc.thePlayer.motionX *= 0;
         mc.thePlayer.motionY *= 0;
         mc.thePlayer.motionZ *= 0;
      }
   }
   @EventTarget
   public void onEventMove(EventMove eventMove) {
      if (NoMove.getBoolean()){
         mc.thePlayer.motionX *= 0;
         mc.thePlayer.motionY *= 0;
         mc.thePlayer.motionZ *= 0;
      }
   }
   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      if (NoPreMove.getBoolean()) {
         mc.thePlayer.motionX *= 0;
         mc.thePlayer.motionY *= 0;
         mc.thePlayer.motionZ *= 0;
      }
   }
   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
      if (NoPostMove.getBoolean()) {
         mc.thePlayer.motionX *= 0;
         mc.thePlayer.motionY *= 0;
         mc.thePlayer.motionZ *= 0;
      }
   }
}
