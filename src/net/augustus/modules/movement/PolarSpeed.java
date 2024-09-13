package net.augustus.modules.movement;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.utils.skid.tenacity.TimerUtil;
import net.augustus.utils.skid.xylitol.TimeUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import java.awt.*;

/**
 * @author Genius
 * @since 2024/6/30 20:21
 * IntelliJ IDEA
 */

public class PolarSpeed extends Module {


    public TimerUtil timeUtil = new TimerUtil();

    public int ticks = 0;


    private boolean start = false;

    public PolarSpeed() {
        super("PolarSpeed", new Color(-1), Categorys.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ticks = 0;
        start = false;
    }

    @Override
    public void onDisable() {
        ticks = 0;
        start = false;
    }

    @EventTarget
    public void onReadPacket(EventReadPacket eventReadPacket) {

        Packet p = eventReadPacket.getPacket();

        if (p != null && p instanceof S12PacketEntityVelocity) {

            S12PacketEntityVelocity s = (S12PacketEntityVelocity) p;

            if (s.getEntityID() == mc.thePlayer.getEntityId()) {
                start = true;
                timeUtil.reset();
            }

        }

    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (start) {

            if (this.timeUtil.hasTimeElapsed(20)) {
                start = false;
            }

            if (mc.thePlayer.motionY <= -0.10) {
                ticks++;
                if (ticks % 2 == 0) {
                    mc.thePlayer.motionY = -0.1;
                    mc.thePlayer.jumpMovementFactor = 0.0265f;
                } else {
                    mc.thePlayer.motionY = -0.16;
                    mc.thePlayer.jumpMovementFactor = 0.0265f;
                }
            } else {
                ticks = 0;
            }
        }
    }
}

