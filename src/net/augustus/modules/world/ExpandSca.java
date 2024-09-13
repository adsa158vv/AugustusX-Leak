package net.augustus.modules.world;

import net.augustus.events.EventSendPacket;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.vestige.PlayerUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;


public class ExpandSca extends Module {
    private final DoubleValue expand = new DoubleValue(8283,"Expand",this,3,0,5,0);
    public StringValue blockPicker = new StringValue(12806,"BlockPicker", this, "Switch",new String[] { "None", "Switch"});
    public BooleanValue switchBack = new BooleanValue(2763,"SwitchBack",this,true);
    private final DoubleValue delay = new DoubleValue(159,"DelayTicks",this,1,0,5,0);
    private final BooleanValue autodisabler = new BooleanValue(1610, "GrimPlaceDis", this, false);
    private final BooleanValue noc0a = new BooleanValue(2353,"NoC0A",this,false);
    private final BooleanValue onWorld = new BooleanValue(13791, "DisableOnWorld", this, false);
    public ExpandSca() {
        super("ExpandSca", new Color(169, 66, 237), Categorys.WORLD);
    }
    private int lastSlot = -3;
    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        if (eventSendPacket.getPacket() instanceof C0APacketAnimation && noc0a.getBoolean()) {
            eventSendPacket.setCancelled(true);
        }
    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        if(onWorld.getBoolean()) {
            setToggled(false);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.theWorld != null) {
            lastSlot = mc.thePlayer.inventory.currentItem;
        }
        if (autodisabler.getBoolean()) {
            mm.disabler.grimplace.setBoolean(true);
        }
    }

    @Override
    public void onDisable() {
        if (switchBack.getBoolean() && switchBack.isVisible() && lastSlot >= 0) mc.thePlayer.inventory.currentItem = lastSlot;
        super.onDisable();
        if (autodisabler.getBoolean()) {


                mm.disabler.grimplace.setBoolean(false);

        }
    }
    private void pickBlock() {
        if(!blockPicker.getSelected().equals("None")) {


                if (mc.thePlayer.inventory.getItemStack() != pickStack()) {

                    for (int i = 8; i >= 0; i--) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                        if (stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {

                            mc.thePlayer.inventory.currentItem = i;
                            break;
                        }
                    }
                }


        }
    }
    private ItemStack pickStack() {
        if(!blockPicker.getSelected().equals("None")) {

            for(int i = 8; i >= 0; i--) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if(stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {

                    return mc.thePlayer.inventory.getStackInSlot(i);

                }
            }
        }
        return null;
    }
    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {

        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) || PlayerUtil.isBlockBlacklisted(mc.thePlayer.getHeldItem().getItem())) {
            pickBlock();
        }
        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) return;

        BlockPos blockPospx = new BlockPos(mc.thePlayer.posX+expand.getValueneg1(),mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1());
        BlockPos blockPosnx = new BlockPos(mc.thePlayer.posX-expand.getValueneg1(),mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1());

        BlockPos blockPospx1 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-1,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz1 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-1);
        BlockPos blockPosnx1 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+1,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz1 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+1);

        BlockPos blockPospx2 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-2,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz2 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-2);
        BlockPos blockPosnx2 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+2,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz2 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+2);

        BlockPos blockPospx3 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-3,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz3 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-3);
        BlockPos blockPosnx3 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+3,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz3 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+3);

        BlockPos blockPospx4 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-4,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz4 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-4);
        BlockPos blockPosnx4 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+4,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz4 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+4);

        BlockPos blockPospx5 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-5,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz5 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-5);
        BlockPos blockPosnx5 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+5,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz5 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+5);

        BlockPos blockPospx6 = new BlockPos(mc.thePlayer.posX+expand.getValueneg1()-6,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPospz6 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ+ expand.getValueneg1()-6);
        BlockPos blockPosnx6 = new BlockPos(mc.thePlayer.posX-expand.getValueneg1()+6,mc.thePlayer.posY-1,mc.thePlayer.posZ);
        BlockPos blockPosnz6 = new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ- expand.getValueneg1()+6);

        ItemStack item = mc.thePlayer.inventory.getCurrentItem();


        if (mc.thePlayer.ticksExisted % delay.getValue() !=0) return;
        if (mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx,
                    5,
                    item,
                    0,
                    0,
                    0
            ));

            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx1,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx2,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx3,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx4,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx5,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospx6,
                    5,
                    item,
                    0,
                    0,
                    0
            ));
        }
        if (mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx1,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx2,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx3,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx4,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx5,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnx6,
                    4,
                    item,
                    0,
                    0,
                    0
            ));
        }
        if (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz1,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz2,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz3,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz4,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz5,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPosnz6,
                    2,
                    item,
                    0,
                    0,
                    0
            ));
        }
        if (mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz1,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz2,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz3,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz4,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz5,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    blockPospz6,
                    3,
                    item,
                    0,
                    0,
                    0
            ));
        }



    }
}
