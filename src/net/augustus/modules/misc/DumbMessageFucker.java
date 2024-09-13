package net.augustus.modules.misc;

import net.augustus.events.EventReadPacket;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.*;
import java.util.*;


public class DumbMessageFucker extends Module {


   public DumbMessageFucker() {
      super("DumbMessageFucker", new Color(169, 66, 237), Categorys.MISC);
      readBlockedMessages();
   }

   public BooleanValue notBlockMessageAbove = new BooleanValue(12144, "NotBlockMessageAbove", this, true);
   public DoubleValue notBlockMessageAbove_Amount = new DoubleValue(11783, "NBMA-Amout", this, 5, 0, 256, 0);
   private BooleanValue debug = new BooleanValue(13490, "Debug", this, true);

   @EventTarget
   public void onEventReadPacket(EventReadPacket eventReadPacket) {
      if (mc.thePlayer == null || mc.theWorld == null) {
         return;
      }

      if (eventReadPacket.getPacket() instanceof S02PacketChat) {
         S02PacketChat s02PacketChat = (S02PacketChat) eventReadPacket.getPacket();
         String message = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText()).toLowerCase();
         String message2 = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
         String message3 = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getFormattedText());
         String playerName = findPlayerName(message3);
         String desp = "§2[§f"+arrayListToString(findDescriptions(extractBlockedMessages(readBlockedMessages(),message)))+"§2]§f";
        if (playerName.toLowerCase().contains(mc.thePlayer.getNameClear().toLowerCase())){
            if (debug.getBoolean()) {
               System.out.println("[DumbMessageFucker][Debug] Are You Saying F Words ? Returned.");
            }
            return;
         }
         if (notBlockMessageAbove.getBoolean() && message.length() >= notBlockMessageAbove_Amount.getValue()) return;
         if (shouldBlock(readBlockedMessages(), message)) {
            if (debug.getBoolean()) {
               LogUtil.addChatMessage("§F[§6DumbMessageFucker§F]§F[§5Debug§F] Blocked a Dumb Message Sended by §2<§f" + EnumChatFormatting.RED + playerName + EnumChatFormatting.RESET + "§2>§f ! Description(s) is(are)§6 "+desp+"§f !");
               System.out.println("[DumbMessageFucker][Debug] Blocked Dumb Message: " + message2);
            }
            eventReadPacket.setCancelled(true);
         }
      }
   }
   public static String arrayListToString(ArrayList<String> stringArrayList) {
      if (stringArrayList.isEmpty()) {
         return "";
      }

      StringBuilder builder = new StringBuilder();
      builder.append(stringArrayList.get(0));

      for (int i = 1; i < stringArrayList.size(); i++) {
         builder.append(",");
         builder.append(stringArrayList.get(i));
      }

      return builder.toString();
   }




   private boolean shouldBlock(ArrayList<String> whatShouldBlock, String input) {
       return !(extractBlockedMessages(whatShouldBlock, input).isEmpty());
   }

   public ArrayList<String> extractBlockedMessages(ArrayList<String> whatShouldBlock, String input) {
      input = input.toLowerCase();
      ArrayList<String> result = new ArrayList<>();

      for (String content : whatShouldBlock) {
         String contentLower = content.toLowerCase();
         if (input.contains(contentLower)) {
            int startIndex = input.indexOf(contentLower);
            int endIndex = startIndex + contentLower.length();

            if ((startIndex == 0 || !Character.isLetter(input.charAt(startIndex - 1))) &&
                    (endIndex == input.length() || !Character.isLetter(input.charAt(endIndex)))) {
               result.add(content);
            }
         }
      }

      if (result.size() > 1) {
         result = new ArrayList<>(new HashSet<>(result));
      }

      if (mm.dumbMessageFucker.debug.getBoolean() && !result.isEmpty()) {
         System.out.println("[DumbMessageFucker][Debug] Triggered: " + result);
      }

      return result;
   }








   String filePath = "AugustusX/DumbMessages.txt";
   File file = new File(filePath);
   public void createTxtFile(){
      if (!file.exists()) {
          try {
              file.createNewFile(); // 如果文件不存在，则创建

          FileWriter fileWriter = new FileWriter(file);
         fileWriter.write("{\n");
         fileWriter.write("[]<>\n");
         fileWriter.write("[]<>\n");
         fileWriter.write("[]<>\n");
         fileWriter.write("//以上中括号内填写你要屏蔽的消息，后面跟着的转义字符中填写注释，可为空。不够可以加。\n");
         fileWriter.write("}\n");
         fileWriter.close();
          } catch (Exception e) {
             e.printStackTrace();
             if (mc.thePlayer != null && mc.theWorld != null) {
                LogUtil.addChatMessage("§F[§6DumbMessageFucker§F]§F[§4ERROR§F] Failed to Create or Read DumbMessage.txt ! Check ?");
             }
             System.err.println("[DumbMessageFucker][ERROR]: " + e.getMessage());
             if (e instanceof IOException) {
                System.err.println("[DumbMessageFucker][ERROR] Failed to Create DumbMessage.txt ! Check ?");
             }
          }
      }
   }


   private ArrayList<String> readBlockedMessages() {
      ArrayList<String> messages = new ArrayList<>();
      BufferedReader reader;
      createTxtFile();
         try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            boolean readMessages = false;
            while ((line = reader.readLine()) != null) {
               if (line.contains("{")) {
                  readMessages = true;
                  // LogUtil.addChatMessage("readMEssages = true");
                  continue;
               }
               if (readMessages && line.contains("[")) {
                  // LogUtil.addChatMessage("readMEssages");
                  String content = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                  messages.add(content);
               }
               if (line.contains("}")) {
                  readMessages = false;
                  // LogUtil.addChatMessage("readMEssages = false");
                  continue;
               }
            }
            reader.close();


         } catch (Exception e) {
            e.printStackTrace();

            System.err.println("[DumbMessageFucker][ERROR]: " + e.getMessage());
            if (e instanceof IOException) {
               System.err.println("[DumbMessageFucker][ERROR] Failed to Read DumbMessage.txt ! Check ?");
            }
            if (mc.thePlayer != null && mc.theWorld != null) {
               LogUtil.addChatMessage("§F[§6DumbMessageFucker§F]§F[§4ERROR§F] Failed to Read DumbMessage.txt ! Check ?");
            }
         }



      return messages;
   }
   public ArrayList<String> convertToLowerCase(ArrayList<String> list) {
      ArrayList<String> lowerCaseList = new ArrayList<>();

      for (String str : list) {
         lowerCaseList.add(str.toLowerCase());
      }

      return lowerCaseList;
   }
   public ArrayList<String> findDescriptions(ArrayList<String> blockedMessages) {
      createTxtFile();
      ArrayList<String> descriptions = new ArrayList<>();
      BufferedReader reader;
      try {
         reader = new BufferedReader(new FileReader(file));
         String line;
         boolean readMessages = false;
         while ((line = reader.readLine()) != null) {
            if (line.contains("{")) {
               readMessages = true;
               continue;
            }
            if (readMessages && line.contains("[")) {
               String content = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
               if (convertToLowerCase(blockedMessages).contains(content.toLowerCase())) {
                  // Get the description after <>
                  int startIndex = line.indexOf("<") + 1;
                  int endIndex = line.indexOf(">");
                  String description = line.substring(startIndex, endIndex);
                  descriptions.add(description);

               }
            }
            if (line.contains("}")) {
               readMessages = false;
               continue;
            }
         }
         reader.close();
      } catch (Exception e) {
         e.printStackTrace();

         System.err.println("[DumbMessageFucker][ERROR]: " + e.getMessage());
         if (e instanceof IOException) {
            System.err.println("[DumbMessageFucker][ERROR] Failed to Read DumbMessage.txt ! Check ?");
         }
         if (mc.thePlayer != null && mc.theWorld != null) {
            LogUtil.addChatMessage("§F[§6DumbMessageFucker§F]§F[§4ERROR§F] Failed to Read DumbMessage.txt ! Check ?");
         }
      }
      return descriptions;
   }

   public String findPlayerName(String content) {
      String result = "";
      int startIndex = content.indexOf("<");
      int endIndex = content.indexOf(">");

      if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
         result = content.substring(startIndex + 1, endIndex);
      }

      return result;
   }


   @Override
   public void onEnable() {
      super.onEnable();
      createTxtFile();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      createTxtFile();
   }


}



