package net.augustus.modules.special;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.PlayerUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.vestige.LogUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.ArrayList;

public class WatchdogNoSlow extends Module {




    public WatchdogNoSlow() {
        super("WatchdogNoSlow", Color.RED, Categorys.SPECIAL);
    }
    public final BooleanValue sword = new BooleanValue(1,"Sword",this,true);
    public final BooleanValue bow = new BooleanValue(12,"Bow",this,true);
    public final BooleanValue food = new BooleanValue(2,"Food",this,true);

    public final BooleanValue canAtt = new BooleanValue(2,"SetVar1",this,true);
    private boolean nextTemp = false;
    private boolean lastBlockingStat = false;
    private final ArrayList<Packet> packetBuf = new ArrayList<>();
    public TimeHelper timeHelper = new TimeHelper();

    public void resetAll() {
        nextTemp = false;
        lastBlockingStat = false;
        packetBuf.clear();
        timeHelper.reset();
    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        resetAll();
    }
    @Override
    public void onEnable() {
        LogUtil.addDebugChatMessage(this,"感谢Wxcer和Mufeng对这个模块的思路支持。");
        super.onEnable();
        resetAll();
    }
    @Override
    public void onDisable() {
        super.onDisable();
        resetAll();
    }
    private boolean isUsingItem() {
        if (!PlayerUtil.isUsingItemB()) return false;
        if (this.sword.getBoolean() && PlayerUtil.isHoldingSword()) return true;
        if (this.bow.getBoolean() && PlayerUtil.isHoldingBow()) return true;
        return this.food.getBoolean() && PlayerUtil.isHoldingFood();
    }
    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if((lastBlockingStat || isUsingItem())) {
            if (nextTemp && timeHelper.reached(230)) {
                nextTemp = false;

                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                if (!packetBuf.isEmpty()) {

                    for (Packet packet : packetBuf) {
                        if (!((packet instanceof C02PacketUseEntity || packet instanceof C0APacketAnimation) && !canAtt.getBoolean())) {
                            PacketUtil.sendPacketNoEvent(packet);
                        }
                    }
                    packetBuf.clear();
                }
            }

            if (!nextTemp) {
                lastBlockingStat = isUsingItem();
                if (!isUsingItem()) {
                    return;
                }
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                nextTemp = true;
                timeHelper.reset();
            }
        }
    }
    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet packet = eventSendPacket.getPacket();
        if (packet instanceof C07PacketPlayerDigging && isUsingItem()) {
            eventSendPacket.setCancelled(true);
        }
        if (nextTemp) {
            if ((packet instanceof C07PacketPlayerDigging ||packet instanceof C08PacketPlayerBlockPlacement) && (PlayerUtil.isHoldingSword() && PlayerUtil.isUsingItemB())){
                eventSendPacket.setCancelled(true);
            }else if (packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement){
                packetBuf.add(packet);
                eventSendPacket.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onEventSlowDown(EventSlowDown eventSlowDown) {
        if (isUsingItem()) {
            eventSlowDown.setMoveForward(1F);
            eventSlowDown.setMoveStrafe(1F);
            eventSlowDown.setSprint(true);
        } else {
            eventSlowDown.setMoveForward(0.2F);
            eventSlowDown.setMoveStrafe(0.2F);
            eventSlowDown.setSprint(false);
        }
    }


}
