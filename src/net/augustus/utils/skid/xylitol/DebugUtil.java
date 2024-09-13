//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!
//Skid by ABW, thanks to Xylitol LEAK.

package net.augustus.utils.skid.xylitol;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;

public class DebugUtil
{
    private static Minecraft mc;
    
    public static void print(final Object... debug) {
        if (isDev()) {
            final String message = Arrays.toString(debug);
            DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        }
    }
    
    public static void log(final Object message) {
        final String text = String.valueOf(message);
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }
    
    public static void log(final boolean prefix, final Object message) {
        final String text = EnumChatFormatting.BOLD + "[" + "Xenza-ABW" + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }
    
    public static void log(final String prefix, final Object message) {
        final String text = EnumChatFormatting.BOLD + "[" + "Xenza-ABW" + "-" + prefix + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }
    
    public static void logcheater(final String prefix, final Object message) {
        final String text = EnumChatFormatting.BOLD + "[" + "Xenza-ABW" + "-" + prefix + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }
    
    private static boolean isDev() {
        return true;
    }
    
    static {
        DebugUtil.mc = Minecraft.getMinecraft();
    }
}
