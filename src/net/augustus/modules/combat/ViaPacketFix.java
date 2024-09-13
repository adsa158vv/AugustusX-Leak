package net.augustus.modules.combat;


import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;


import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.utils.PacketUtil;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.custompackets.CAnimateHandPacket;
import net.augustus.utils.custompackets.Hand;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.util.ChatComponentText;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.awt.*;

public class ViaPacketFix extends Module {


    public BooleanValue block2 = new BooleanValue(2781, "1.9BUseItem", this, false);
    public BooleanValue cancelC08 = new BooleanValue(706,"NoOriginalC08",this,false);
    public BooleanValue cancelC0F = new BooleanValue(706,"NoOriginalC0F",this,false);
    public BooleanValue swing2 = new BooleanValue(6505,"1.9AnimateHand",this,false);
    public BooleanValue cancelC0A = new BooleanValue(737,"NoOriginalC0A",this,false);
    private final BooleanValue autoSet = new BooleanValue(13124,"AutoSet",this,true);
    public final BooleanValue debug = new BooleanValue(13955,"Debug",this,false);
    public static boolean istoogled = false;



    public ViaPacketFix() {
        super("ViaPacketFix", Color.BLUE, Categorys.COMBAT);
    }
    @Override
    public void onEnable() {
        istoogled = true;

    }
    @Override
    public void onDisable() {
        istoogled = false;

    }

    @Override
    public void onWorld(EventWorld event) {
        super.onWorld(event);
    }

    @EventTarget
    public void onEventTick(EventTick eventTick) {
        if (autoSet.getBoolean()) {
            if (ViaMCP.getInstance().getVersion() < 107 ) {
                this.block2.setBoolean(false);
                this.swing2.setBoolean(false);
                this.cancelC08.setBoolean(false);
                this.cancelC0A.setBoolean(false);
            }
            else if (ViaMCP.getInstance().getVersion() == ProtocolCollection.R1_18.getVersion().getVersion()) {
                this.cancelC0F.setBoolean(true);
            } else {
                this.block2.setBoolean(true);
                this.swing2.setBoolean(true);
            }
        }
    }


    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet<?> packet = eventSendPacket.getPacket();


        if (packet instanceof C0FPacketConfirmTransaction && this.cancelC0F.getBoolean()) {
            eventSendPacket.setCancelled(true);
            if (debug.getBoolean()) {
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F]C0F Detected."));
            }
        }

        if (packet instanceof C08PacketPlayerBlockPlacement && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            if (cancelC08.getBoolean()) {
                eventSendPacket.setCancelled(true);
                if (debug.getBoolean())
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F] Original C08 Cancelled."));
            }
            if (block2.getBoolean()) {
                PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                useItem.write(Type.VAR_INT, 1);
                PacketUtil.sendToServer(useItem, Protocol1_8TO1_9.class, true, true);
                if (debug.getBoolean())
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F] UseItem Sent."));
            }

        }
        if (packet instanceof C0APacketAnimation) {
            if (debug.getBoolean())
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F]C0A Detected."));
            if (cancelC0A.getBoolean()) {
                eventSendPacket.setCancelled(true);
                if (debug.getBoolean())
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F] Original C08 Cancelled."));
            }
            if (swing2.getBoolean()) {
                mc.thePlayer.sendQueue.addToSendQueue(new CAnimateHandPacket(Hand.MAIN_HAND));
                if (debug.getBoolean())
                    mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§F[§6ViaPacketFix§F][§5Debug§F] CPacketAnimateHand Sent."));
            }
        }
    }
}
