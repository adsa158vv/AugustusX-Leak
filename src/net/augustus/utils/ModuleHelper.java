package net.augustus.utils;

import net.minecraft.util.ChatComponentText;

import static net.augustus.Augustus.mc;
import static net.augustus.utils.interfaces.MM.mm;

public class ModuleHelper {

    private static boolean isdead = false;
    private static double tickexisteddead = 0;

    public static void PostDisDeathFixer()  {
        if (mm.postDis.debug.getBoolean()) mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("ModuleHelper Works!"));
        isdead = true;
        tickexisteddead = 0;
        while (isdead ) {
            mm.postDis.setToggled(false);
            tickexisteddead = mc.thePlayer.ticksExisted;
            if (mc.thePlayer.isEntityAlive()) {
                isdead = false;
                break;
            }
        }
        while (true) {
            if (mc.thePlayer.ticksExisted - tickexisteddead == 10) {
                mm.postDis.setToggled(true);
                tickexisteddead = 0;
                break;
            }
        }
    }
}
