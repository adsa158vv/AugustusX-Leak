// Decompiled with: CFR 0.152
// Class Version: 8
package net.augustus.modules.misc;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.audio.SoundCategory;
import org.lwjgl.opengl.Display;

import java.awt.*;


public class FocusSound
        extends Module {



    public FocusSound  () {
        super("FocusSound", new Color(189, 32, 110), Categorys.MISC);
    }
    private BooleanValue debug = new BooleanValue(14351,"Debug",this,true);
    public static float currentMasterSoundLevel = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        currentMasterSoundLevel = mc.gameSettings.getSoundLevel(SoundCategory.MASTER);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mm.focusSound.debug.getBoolean() && mc.thePlayer != null && mc.theWorld != null){
            LogUtil.addChatMessage("§F[§6FocusSound§F]§F[§5Debug§F] Module Disabled ! Set Master Volume to§2 "+currentMasterSoundLevel+"§f !");
        }
        if (mm.focusSound.debug.getBoolean()) {
            System.out.println("[FocusSound][Debug] Module Disabled ! Set Master Volume to "+currentMasterSoundLevel+" !");
        }
        mc.gameSettings.setSoundLevel(SoundCategory.MASTER,currentMasterSoundLevel);
        currentMasterSoundLevel = 0;
    }
    @EventTarget
    public void onEventRender2D(EventRender2D eventRender2D) {
        WinDef.HWND windowHandle = User32.INSTANCE.FindWindow(null, Display.getTitle());

        if (windowHandle != null) {
            WinDef.HWND currentFocus = User32.INSTANCE.GetForegroundWindow();

            if (windowHandle.equals(currentFocus)) {
                if (mc.gameSettings.getSoundLevel(SoundCategory.MASTER) == currentMasterSoundLevel) {
                    return;
                }
                if (mm.focusSound.debug.getBoolean() && mc.thePlayer != null && mc.theWorld != null){
                    LogUtil.addChatMessage("§F[§6FocusSound§F]§F[§5Debug§F] You are §2In Focus§F ! Set Master Volume to§2 "+currentMasterSoundLevel+"§f !");
                }
                if (mm.focusSound.debug.getBoolean()) {
                    System.out.println("[FocusSound][Debug] You are In Focus ! Set Master Volume to "+currentMasterSoundLevel+" !");
                }
                mc.gameSettings.setSoundLevel(SoundCategory.MASTER, currentMasterSoundLevel);
            } else {
                if (mc.gameSettings.getSoundLevel(SoundCategory.MASTER) == 0) {
                    return;
                }
                if (mm.focusSound.debug.getBoolean() && mc.thePlayer != null && mc.theWorld != null){
                    LogUtil.addChatMessage("§F[§6FocusSound§F]§F[§5Debug§F] You are §4Not In Focus§F ! Set Master Volume to§4 0§F !");
                }
                if (mm.focusSound.debug.getBoolean()) {
                    System.out.println("[FocusSound][Debug] You are Not In Focus ! Set Master Volume to 0 !");
                }
                mc.gameSettings.setSoundLevel(SoundCategory.MASTER, 0);
            }
        } else {
            System.out.println("[FocusSound] Error. ");
        }


    }
    @EventTarget
    public void onEventSetSound(EventSetSound eventSetSound) {
        if (eventSetSound.getSoundCategory() == SoundCategory.MASTER) {
            if (mm.focusSound.debug.getBoolean() && mc.thePlayer != null && mc.theWorld != null){
                LogUtil.addChatMessage("§F[§6FocusSound§F]§F[§5Debug§F] You are Setting Master Volume Manually to§2 "+eventSetSound.getValue()+" §f!");
            }
            if (mm.focusSound.debug.getBoolean()){
                System.out.println("[FocusSound][Debug] You are Setting Master Volume Manually to "+eventSetSound.getValue()+" !");
            }
            if (!eventSetSound.isCancelled()) {
                currentMasterSoundLevel = eventSetSound.getValue();
            }
        }
    }






}
