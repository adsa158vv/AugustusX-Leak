package net.augustus.utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static net.augustus.Augustus.mc;

public class KeyPressUtil {
    public static boolean isPressingRight() {
        try {


            if (mc.gameSettings.keyBindUseItem.getKeyCode() == -99) {
                return Mouse.isButtonDown(1);
            } else if (mc.gameSettings.keyBindUseItem.getKeyCode() == -98) {
                return Mouse.isButtonDown(2);
            } else if (mc.gameSettings.keyBindUseItem.getKeyCode() == -97) {
                return Mouse.isButtonDown(0);
            } else {
                return Keyboard.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode());
            }
        } catch (NullPointerException nullPointerException) {
            nullPointerException.getCause().printStackTrace();
        } catch (Exception ignored) {}
        return false;
    }
}
