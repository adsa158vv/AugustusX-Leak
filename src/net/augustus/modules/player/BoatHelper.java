package net.augustus.modules.player;

import java.awt.Color;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.ChatComponentText;


public class BoatHelper extends Module {

    public BoatHelper() {
        super("BoatHelper", new Color(224, 111, 49), Categorys.PLAYER);
    }
    private final BooleanValue debug = new BooleanValue(2487,"Debug",this,true);
    private Entity entity;
    private Packet storedpacket;
    private boolean canhelp;
    @Override
    public void onEnable(){
        super.onEnable();
        storedpacket = null;
        canhelp = false;
        entity = null;
    }
    @Override
    public void onDisable(){
        super.onDisable();
        storedpacket = null;
        canhelp = false;
        entity = null;
    }
    @EventTarget
    public void onWorld() {
        storedpacket = null;
        canhelp = false;
        entity = null;
    }
    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {

        if (debug.getBoolean()) {
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(storedpacket+"[dbg] "+(((C02PacketUseEntity) storedpacket).getAction()+" "+((C02PacketUseEntity) storedpacket).getHitVec()+" "+((C02PacketUseEntity) storedpacket).getEntityFromWorld(mc.theWorld))+" "+storedpacket.getClass()));
        }
        if (storedpacket != null && mc.thePlayer.isRiding() && mc.gameSettings.keyBindUseItem.pressed && entity != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }


    }
    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet packet = eventSendPacket.getPacket();
        if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity) packet).getAction() == C02PacketUseEntity.Action.INTERACT && ((C02PacketUseEntity) packet).getEntityFromWorld(entity.worldObj) instanceof EntityBoat) {
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(packet+" "+(((C02PacketUseEntity) packet).getAction()+" "+((C02PacketUseEntity) packet).getHitVec()+" "+((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld))+" "+packet.getClass()));
            storedpacket = packet;
            entity = ((C02PacketUseEntity) packet).getEntityFromWorld(entity.worldObj);
        }
    }
}

