package net.augustus.utils.skid.fdpclient;

import net.augustus.utils.MoveUtil;
import net.augustus.utils.interfaces.MC;

import static java.lang.Math.*;

/**
 * @author Genius
 * @since 2024/6/10 9:25
 * IntelliJ IDEA
 */

public class MovementUtils implements MC {

    public static float getSpeed() {
        return (float) sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(float speed) {
        if (!MoveUtil.isMoving()) return;
        mc.thePlayer.motionX = -sin(getDirection()) * speed;
        mc.thePlayer.motionZ = cos(getDirection()) * speed;
    }

    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.movementInput.moveForward < 0f) {
            rotationYaw += 180f;
        }
        float forward = 1f;
        if (mc.thePlayer.movementInput.moveForward < 0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.movementInput.moveForward > 0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.movementInput.moveStrafe > 0f) {
            rotationYaw -= 90f * forward;
        }
        if (mc.thePlayer.movementInput.moveStrafe < 0f) {
            rotationYaw += 90f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

}
