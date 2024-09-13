package net.augustus.modules.misc;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;

import java.awt.*;


public class PacketDebugger extends Module {
   public final BooleanValue C0E = new BooleanValue(9710, "C0E", this, false);
   public final BooleanValue C08 = new BooleanValue(15243, "C08", this, false);
   private final BooleanValue C02 = new BooleanValue(4728,"C02",this,false);
   public PacketDebugger() {
      super("PacketDebugger", new Color(169, 66, 237), Categorys.MISC);
   }

   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
      Packet<?> packet = eventSendPacket.getPacket();
      if (packet instanceof C0EPacketClickWindow && C0E.getBoolean()){
         mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(packet+"WindowID "+((C0EPacketClickWindow) packet).getWindowId()+" SlotID "+((C0EPacketClickWindow) packet).getSlotId()+" Button "+((C0EPacketClickWindow) packet).getUsedButton()+" Mode "+((C0EPacketClickWindow) packet).getMode()+" Stack "+((C0EPacketClickWindow) packet).getClickedItem()+" Action "+((C0EPacketClickWindow) packet).getActionNumber()));
      }
      if (packet instanceof C08PacketPlayerBlockPlacement && C08.getBoolean()){
         mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(packet+" "+((C08PacketPlayerBlockPlacement) packet).getPosition()+" "+((C08PacketPlayerBlockPlacement) packet).getPlacedBlockDirection()+" "+((C08PacketPlayerBlockPlacement) packet).getStack()+" "+((C08PacketPlayerBlockPlacement) packet).getPlacedBlockOffsetX()+" "+((C08PacketPlayerBlockPlacement) packet).getPlacedBlockOffsetY()+" "+((C08PacketPlayerBlockPlacement) packet).getPlacedBlockOffsetX()));
      }
      if (packet instanceof C02PacketUseEntity && C02.getBoolean()){
         mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(packet+" "+(((C02PacketUseEntity) packet).getAction()+" "+((C02PacketUseEntity) packet).getHitVec()+" "+((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld))+" "+packet.getClass()));
      }
   }

}
