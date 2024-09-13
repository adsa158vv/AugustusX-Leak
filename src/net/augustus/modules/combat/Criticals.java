package net.augustus.modules.combat;

import net.augustus.events.EventAttackEntity;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventTick;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Criticals extends Module {
    public StringValue mode = new StringValue(10909, "Mode", this, "NoGround", new String[]{"NoGround",
            "Packet",
            "AAC5.0.4",
            "AAC5",
            "NCPLatest",
            "Vulcan",
            "VerusSmart",
            "Jump"
    });
    public BooleanValue verusSmart_UseC06 = new BooleanValue(31,"UseC06",this,false);
    public BooleanValue debug = new BooleanValue(311,"Debug",this,false);
    public DoubleValue jump_CustomJumpHeight = new DoubleValue(118192,"CustomJumpHeight",this,0.10,0,1,3);
    public BooleanValue jump_NoGround = new BooleanValue(118193,"NoGround",this,false);
    private BooleanValue suffix = new BooleanValue(14615,"Suffix",this,false);
    public Criticals() {
        super("Criticals", Color.RED, Categorys.COMBAT);
    }
    private int attackedv = 0;
    private int attackedn = 0;
    private int verusSmart_AttackCount = 0;
    @EventTarget
    public void onEventTick(EventTick eventTick) {

        setSuffix(mode.getSelected(), suffix.getBoolean());

    }
    @EventTarget
    public void onEventAttackEntity(EventAttackEntity eventAttackEntity) {
        String var2 = this.mode.getSelected();
        Entity target = eventAttackEntity.getTarget();
        switch (var2) {
            case "Packet":
                mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 6.0E-15, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                if (debug.getBoolean() && target.hurtResistantTime == 0) LogUtil.addDebugChatMessage(this,"Crit.");
            case "Vulcan":
                attackedv++;
                if (attackedv > 7) {
                    sendPositionPacket(0.16477328182606651, false);
                    sendPositionPacket(0.08307781780646721, false);
                    sendPositionPacket(0.0030162615090425808, false);
                    if (debug.getBoolean()) LogUtil.addDebugChatMessage(this,"Crit.");
                    attackedv = 0;
                }
                break;
            case "NCPLatest":
                attackedn++;
                if (attackedn >= 5) {
                    sendPositionPacket(0.00001058293536, false);
                    sendPositionPacket(0.00000916580235, false);
                    sendPositionPacket(0.00000010371854, false);
                    if (debug.getBoolean() ) LogUtil.addDebugChatMessage(this,"Crit.");
                    attackedn = 0;
                }
                break;
            case "AAC5":
                sendPositionPacket(0.0625, false);
                sendPositionPacket(0.0433, false);
                sendPositionPacket(0.2088, false);
                sendPositionPacket(0.9963, false);
                if (debug.getBoolean() ) LogUtil.addDebugChatMessage(this,"Crit.");
                break;
            case "AAC5.0.4":
                sendPositionPacket(0.00133545, false);
                sendPositionPacket(-0.000000433, false);
                if (debug.getBoolean()) LogUtil.addDebugChatMessage(this,"Crit.");
                break;
            case "VerusSmart" : {
                verusSmart_AttackCount++ ;
                if (verusSmart_AttackCount > 4) {
                    verusSmart_AttackCount = 0;

                    sendCriticalPacket( 0,0.001,0,true);
                    sendCriticalPacket(0,0,0,false);
                    if (debug.getBoolean()) LogUtil.addDebugChatMessage(this,"Crit.");
                }
                break;
            }
            case "Jump":{
                if (mc.thePlayer.onGround){
                    mc.thePlayer.motionY = jump_CustomJumpHeight.getValue();
                    if (debug.getBoolean()) {
                        LogUtil.addDebugChatMessage(this,"MotionY = §2"+jump_CustomJumpHeight.getValue()+"§f .");
                    }
                }
            }
        }

    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        attackedn = 0;
        attackedv = 0;
        verusSmart_AttackCount = 0 ;
    }

    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet packet = eventSendPacket.getPacket();
        String var3 = this.mode.getSelected();


        switch (var3) {
            case "NoGround":
                if (packet instanceof C03PacketPlayer) {
                    C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                    c03PacketPlayer.setOnGround(false);
                }

            case "Jump":{
                if (jump_NoGround.getBoolean()){
                    if (packet instanceof C03PacketPlayer) {
                        C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) packet;
                        c03PacketPlayer.setOnGround(false);
                    }
                }
            }
        }

    }
    public void sendPositionPacket(boolean ground) {
        this.sendPositionPacket(0, 0, 0, ground);
    }

    public void sendPositionPacket(double yOffset, boolean ground) {
        this.sendPositionPacket(0, yOffset, 0, ground);
    }

    public void sendPositionPacket(double xOffset, double yOffset, double zOffset, boolean ground) {
        double x = mc.thePlayer.posX + xOffset;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + zOffset;
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
    }
    @Override
    public void onEnable() {
        attackedn = 0;
        attackedv = 0;
        verusSmart_AttackCount = 0;
    }

    @Override
    public void onDisable() {
        attackedn = 0;
        attackedv = 0;
        verusSmart_AttackCount = 0;
    }
    public void sendCriticalPacket(double xOffset, double yOffset, double zOffset, boolean ground) {
        double x = mc.thePlayer.posX + xOffset;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + zOffset;
        if (verusSmart_UseC06.getBoolean()) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, ground));
        } else {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }
}
