package net.augustus.modules.player;

import net.augustus.events.EventSendPacket;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.PrePostUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public class AntiDesync extends Module {

   public AntiDesync() {
      super("AntiDesync", new Color(23, 123, 208, 255), Categorys.PLAYER);
   }
   public BooleanValue postC0EFix = new BooleanValue(8521,"PostC0EFix",this,true);
   public BooleanValue C03sSend = new BooleanValue(15859,"C03sSend",this,true);
   public DoubleValue C03sSend_DelayTicks = new DoubleValue(2698,"C03sDelayTicks",this,20,0,100,0);
   public BooleanValue C07sSend = new BooleanValue(12292,"C07sSend",this,true);
   public DoubleValue C07sSend_DelayTicks = new DoubleValue(12451,"C07sDelayTicks",this,20,0,100,0);
   public BooleanValue C0ESend = new BooleanValue(10844,"C0ESend",this,true);
   public DoubleValue C0ESend_DelayTicks = new DoubleValue(2928,"C0EDelayTicks",this,20,0,100,0);
   private BooleanValue debug = new BooleanValue(14116,"Debug",this,true);
   private LinkedBlockingQueue<Packet<?>> storedPackets = new LinkedBlockingQueue<>();

   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {

      if (PrePostUtil.isPost()) {
         if (eventSendPacket.getPacket() instanceof C0EPacketClickWindow && postC0EFix.getBoolean()) {
            if (this.debug.getBoolean()) LogUtil.addChatMessage("§F[§6AntiDesync§F]§F[§5Debug§F][§3PostC0EFix§F] Stored a Post C0E Packet!");
            storedPackets.add(eventSendPacket.getPacket());
            eventSendPacket.setCancelled(true);

         }
      }
      resetC0E();

   }
   @Override
   public void onEnable(){
      super.onEnable();
      C03sTicks = 0;
      C07sTicks = 0;
      C0ETicks = 0;
      callSend = false;
   }
   @Override
   public void onDisable(){
      super.onDisable();
      resetC0E();
      C03sTicks = 0;
      C07sTicks = 0;
      C0ETicks = 0;
      callSend = false;
   }
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      C03sTicks = 0;
      C07sTicks = 0;
      C0ETicks = 0;
      callSend = false;
      resetC0E();
   }

   private void resetC0E() {
      if (PrePostUtil.isPre() && !storedPackets.isEmpty()) {
         try {
            this.storedPackets.forEach(mc.thePlayer.sendQueue::addToSendQueueDirect);
            if (this.debug.getBoolean()) LogUtil.addChatMessage("§F[§6AntiDesync§F]§F[§5Debug§F][§3PostC0EFix§F] Send§2 "+storedPackets.size()+"§F Stored C0E Packet(s)!");
            storedPackets.clear();
         } catch (NullPointerException ignored) {} catch (Exception var2) {
            System.err.println("Error AntiDesync PostC0E Fix.");
         }
      }
   }

   private int C03sTicks = 0;
   private int C07sTicks = 0;
   private int C0ETicks = 0;
   private boolean callSend = false;
   @EventTarget
   public void onEventTick(EventTick eventTick) {
      C03sTicks++;
      C07sTicks++;
      C0ETicks++;


      callSend = true;
   }
   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (PrePostUtil.isPre() && callSend) {

         if (C03sSend.getBoolean() && C03sTicks >= C03sSend_DelayTicks.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition());
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook());
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook());
            if (this.debug.getBoolean())
               LogUtil.addChatMessage("§F[§6AntiDesync§F]§F[§5Debug§F][§3C03sSend§F] Send C03s Packets!");
            C03sTicks = 0;
         }
         if (C07sSend.getBoolean() && C07sTicks >= C07sSend_DelayTicks.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            if (this.debug.getBoolean())
               LogUtil.addChatMessage("§F[§6AntiDesync§F]§F[§5Debug§F][§3C07sSend§F] Send two C07 Packets!");
            C07sTicks = 0;
         }
         if (C0ESend.getBoolean() && C0ETicks >= C0ESend_DelayTicks.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow());
            if (this.debug.getBoolean())
               LogUtil.addChatMessage("§F[§6AntiDesync§F]§F[§5Debug§F][§3C0ESend§F] Send C0E Packet!");
            C0ETicks = 0;
         }
         callSend = false;
      }
   }

}
