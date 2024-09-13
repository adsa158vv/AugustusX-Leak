package net.augustus.modules.movement;

import net.augustus.events.EventMove;
import net.augustus.events.EventReadPacket;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.*;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.awt.*;
import java.util.concurrent.LinkedBlockingDeque;

public class Blink extends Module {

   //public StringValue mode = new StringValue(10513, "Mode", this, "OnlyMovement", new String[]{"Advanced", "All"});
   public BooleanValue in = new BooleanValue(114132,"InBound",this,true);
   public BooleanValue out = new BooleanValue(133,"OutBound",this,true);
   public BooleansSetting mode = new BooleansSetting(12431,"Mode",this,new Setting[]{this.in,this.out});
   public BooleanValue out_AllowC00 = new BooleanValue(11445,"C00",this,true);
   public BooleanValue out_AllowC03 = new BooleanValue(114413,"C03",this,true);
   public BooleanValue out_AllowC0F = new BooleanValue(110413,"C0F",this,true);
   public BooleanValue out_AllowC13 = new BooleanValue(111413,"C13",this,true);
   public BooleanValue out_AllowC18 = new BooleanValue(114183,"C18",this,true);
   public BooleanValue out_AllowC0B = new BooleanValue(114613,"C0B",this,true);
   public BooleanValue out_AllowC02 = new BooleanValue(114113,"C02",this,true);
   public BooleanValue out_AllowC0A = new BooleanValue(11413,"C0A",this,true);
   public BooleanValue out_AllowC08 = new BooleanValue(11113,"C08",this,true);
   public BooleanValue out_AllowC07 = new BooleanValue(147413,"C07",this,true);
   public BooleanValue out_AllowC01Chat = new BooleanValue(144113,"C01Chat",this,true);
   public BooleansSetting outAllows = new BooleansSetting(12311,"OutAllows",this,new Setting[]{this.out_AllowC00,this.out_AllowC0A,this.out_AllowC02,this.out_AllowC03,this.out_AllowC0F,this.out_AllowC13,this.out_AllowC18,this.out_AllowC0B,this.out_AllowC08,this.out_AllowC07, this.out_AllowC01Chat});
   //
   public BooleanValue in_AllowS32 = new BooleanValue(114153,"S32",this,true);
   public BooleanValue in_AllowS14 = new BooleanValue(114813,"S14",this,true);
   public BooleanValue in_AllowS02Chat = new BooleanValue(114883,"S02Chat",this,true);
   public BooleansSetting inAllows = new BooleansSetting(12731,"InAllows",this,new Setting[]{this.in_AllowS32,this.in_AllowS14, this.in_AllowS02Chat});
   public BooleanValue debug = new BooleanValue(417424,"Debug",this,false);
   public BooleanValue suffix = new BooleanValue(419424,"Suffix",this,true);
   public BooleanValue onWorld = new BooleanValue(153191, "DisableOnWorld", this, false);
   public LinkedBlockingDeque<Packet> storedOutPackets = new LinkedBlockingDeque<>();
   public LinkedBlockingDeque<Packet> storedInPackets = new LinkedBlockingDeque<>();
   private void resetAll() {
      storedOutPackets.clear();
      storedInPackets.clear();
   }
   private void releasePackets() {
      try {
         if (!storedOutPackets.isEmpty()) {
            this.storedOutPackets.forEach(mc.thePlayer.sendQueue::addToSendQueueDirect);
            if (debug.getBoolean()) {
               LogUtil.addDebugChatMessage(this,"Sent §2"+storedOutPackets.size()+"§f Packets !");
            }
            storedOutPackets.clear();
         }

      } catch (NullPointerException ignored) {} catch (Exception var2) {
         var2.printStackTrace();
      }
      try {
         if (!storedInPackets.isEmpty()) {
            this.storedInPackets.forEach(mc.thePlayer.sendQueue::addToReceiveQueueUnregistered);
            if (debug.getBoolean()) {
               LogUtil.addDebugChatMessage(this,"Proceed §4"+storedInPackets.size()+"§f Packets !");
            }
            storedInPackets.clear();
         }

      } catch (NullPointerException ignored) {} catch (Exception var2) {
         var2.printStackTrace();
      }


   }
   @EventTarget
   public void onEventMove(EventMove eventMove) {

      if (this.in.getBoolean() && !this.out.getBoolean()) {
         this.setSuffix("I:"+this.storedInPackets.size(),suffix.getBoolean());
      }
      if (this.out.getBoolean() && !this.in.getBoolean()) {
         this.setSuffix("O:"+this.storedOutPackets.size(),suffix.getBoolean());
      }
      if (this.in.getBoolean() && this.out.getBoolean()) {
         this.setSuffix("I:"+this.storedInPackets.size()+" O:"+this.storedOutPackets.size(),suffix.getBoolean());
      }

   }
   @EventTarget
   public void onWorld(EventWorld eventWorld) {
      if(onWorld.getBoolean()) {
         setToggled(false);
      }
   }
   @Override
   public void onEnable() {
      super.onEnable();
      resetAll();
   }
   @Override
   public void onPreDisable() {
      super.onDisable();
      releasePackets();
   }


   public Blink() {
      super("Blink", new Color(75, 5, 161), Categorys.MOVEMENT);
   }
   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
      if (mc.currentScreen instanceof GuiSelectWorld || mc.currentScreen instanceof GuiScreenServerList) return;
      Packet packet = eventSendPacket.getPacket();
      if (this.out.getBoolean()) {
         if (packet instanceof C00PacketKeepAlive && out_AllowC00.getBoolean()) {
            return;
         }
         if (packet instanceof C0BPacketEntityAction && out_AllowC08.getBoolean()) {
            return;
         }
         if (packet instanceof C07PacketPlayerDigging && out_AllowC07.getBoolean()) {
            return;
         }
         if (packet instanceof C02PacketUseEntity && out_AllowC02.getBoolean()) {
            return;
         }
         if (packet instanceof C0FPacketConfirmTransaction && out_AllowC0F.getBoolean()) {
            return;
         }
         if (packet instanceof C0APacketAnimation && out_AllowC0A.getBoolean()) {
            return;
         }
         if (packet instanceof C13PacketPlayerAbilities && out_AllowC13.getBoolean()) {
            return;
         }
         if (packet instanceof C18PacketSpectate && out_AllowC18.getBoolean()) {
            return;
         }
         if (packet instanceof C0BPacketEntityAction && out_AllowC0B.getBoolean()) {
            return;
         }
         if (packet instanceof C03PacketPlayer && out_AllowC03.getBoolean()) {
            return;
         }
         if (packet instanceof C01PacketChatMessage && out_AllowC01Chat.getBoolean()) {
            return;
         }
         storedOutPackets.add(packet);
         eventSendPacket.setCancelled(true);
      }
   }
   @EventTarget
   public void onEventReadPacket(EventReadPacket eventReadPacket) {
      if (mc.currentScreen instanceof GuiSelectWorld || mc.currentScreen instanceof GuiScreenServerList) return;
      if (in.getBoolean()) {
         Packet packet = eventReadPacket.getPacket();
         if (packet instanceof S32PacketConfirmTransaction && in_AllowS32.getBoolean()) {
            return;
         }
         if (packet instanceof S14PacketEntity && in_AllowS14.getBoolean()) {
            return;
         }
         if (packet instanceof S02PacketChat && in_AllowS02Chat.getBoolean()) {
            return;
         }
         storedInPackets.add(packet);
         eventReadPacket.setCancelled(true);
      }
   }


}
