package net.augustus.utils;

import net.augustus.Augustus;
import net.augustus.utils.interfaces.MC;
import net.minecraft.util.ChatComponentText;

public class ChatUtil implements MC {
   public static void sendChat(String s) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§6[§9" + Augustus.getInstance().getName() + "§6] §7" + s));
   }
}
