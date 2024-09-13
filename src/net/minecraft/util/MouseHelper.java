package net.minecraft.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import static net.augustus.utils.interfaces.MM.mm;

public class MouseHelper
{
    /** Mouse delta X this frame */
    public int deltaX;

    /** Mouse delta Y this frame */
    public int deltaY;

    /**
     * Grabs the mouse cursor it doesn't move and isn't seen.
     */
    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Ungrabs the mouse cursor so it can be moved and set it to the center of the screen
     */
    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange() {
        if (mm != null && mm.fixes.isToggled() && mm.fixes.realMouse.getBoolean() && mm.fixes.realMouseThread != null && mm.fixes.realMouseThread.isAlive() ) {
            this.deltaX = mm.fixes.dx;
            mm.fixes.dx = 0;
            this.deltaY = mm.fixes.dy;
            mm.fixes.dy = 0;
        } else {
            this.deltaX = Mouse.getDX();
            this.deltaY = Mouse.getDY();
        }
    }
}
