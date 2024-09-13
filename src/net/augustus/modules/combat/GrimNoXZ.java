package net.augustus.modules.combat;

import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.manager.EventManager;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;

import java.awt.*;

public final class GrimNoXZ extends Module {

    public DoubleValue attackCount = new DoubleValue(3, "AttackCount", this, 13, 0, 30, 1);

    public BooleanValue unregister = new BooleanValue(1, "KeepSprint", this , false);

    public BooleanValue c0fdisabled = new BooleanValue(4, "SendC0FDisabled", this, false);

    public BooleanValue debug = new BooleanValue(2, "Debug", this , false);

    public GrimNoXZ() {
        super("GrimNoXZ", new Color(73, 127, 163), Categorys.COMBAT);
    }

    public boolean start = false;

    @Override
    public void onDisable() {
        start = false;
        if (this.mc.thePlayer.hurtTime > 0 && !this.mc.thePlayer.isOnLadder() && this.c0fdisabled.getBoolean()) {
            PacketUtil.sendPacketC0F();
        }
    }

    @Override
    public void onEnable() {
        start = false;
    }

    @EventTarget
    public void onEventReadPacket(EventReadPacket eventReadPacket) {
        Packet<?> p = eventReadPacket.getPacket();

        if (p instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) p;

            if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                start = true;
            }

        }

    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            KillAura killaura = Augustus.getInstance().getModuleManager().killAura;
            if (killaura.target != null && !KillAura.target.isDead) {
                if (entity.getEntityId() == killaura.target.getEntityId()) {
                    if (mc.thePlayer.hurtTime == 9 && killaura.shouldHit() && start) {
                        if (!mc.thePlayer.isServerSprintState()) {
                            if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.isEventKey()) {
                                PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                mc.thePlayer.setServerSprintState(true);
                            }
                        }

                        if (unregister.getBoolean() && mc.thePlayer.onGround) {
                            mc.thePlayer.setSprinting(true);
                        }

                        for (int i = 0; i < (int) this.attackCount.getValue(); i++){

                            //PacketUtil.sendAttack(entity, this.unregister.getBoolean());

                            EventManager.call(new EventAttackEntity(killaura.target, true));
                            PacketUtil.send(new C02PacketUseEntity((Entity)killaura.target, C02PacketUseEntity.Action.ATTACK));
                            PacketUtil.send(new C0APacketAnimation());

                            if (debug.getBoolean()) {
                                LogUtil.addChatMessage("SendAttack  isRegister:  " + this.unregister.getBoolean());
                            }
                            mc.thePlayer.motionX *= 0.07776;
                            mc.thePlayer.motionZ *= 0.07776;

                            start = false;


                            if (debug.getBoolean()) {
                                LogUtil.addChatMessage("Reduce motion successfully");
                            }
                        }



                        break;
                    }
                }
            }
        }
    }

}