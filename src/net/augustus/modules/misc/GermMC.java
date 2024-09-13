package net.augustus.modules.misc;

import io.netty.buffer.Unpooled;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.GermUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.awt.*;


public class GermMC extends Module {


   public GermMC() {
      super("GermMC", new Color(169, 66, 237), Categorys.MISC);
   }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSimpleFoiled){
         if(!mc.gameSettings.keyBindUseItem.isKeyDown()){
            return;
         }
         mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117}))));
         for(String name : GermUtil.data.keySet()){
            ChatComponentText textComponents = new ChatComponentText("");
            textComponents.appendSibling(createClickableText("§5[§6" + "GermMC§5]§2 " + name +"§1 ","/germ-btn-click "+name));
            mc.thePlayer.addChatComponentMessage(textComponents);

         }
      }
   }
   private IChatComponent createClickableText(String text, String command) {
      ChatComponentText clickableText = new ChatComponentText(text);
      clickableText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
      return clickableText;
   }
   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
      if(eventSendPacket.getPacket() instanceof C01PacketChatMessage){
         String message = ((C01PacketChatMessage) eventSendPacket.getPacket()).getMessage();
         if(message.startsWith("/germ-btn-click ")){
            String name = message.replace("/germ-btn-click ","");
            if(GermUtil.data.containsKey(name)){
               mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",new PacketBuffer(Unpooled.wrappedBuffer(GermUtil.data.get(name)))));
            }
         }
      }
   }
}
