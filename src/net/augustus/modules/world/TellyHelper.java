package net.augustus.modules.world;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.gotaj.InventoryUtils;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class TellyHelper extends Module {

    public TellyHelper() {
        super("TellyHelper", new Color(255,255,255), Categorys.WORLD);
    }

    public DoubleValue airTicks = new DoubleValue(9599,"AirTicks",this,4,0,8,0);
    public BooleanValue switchBlock = new BooleanValue(8013, "SwitchBlock", this, true);
    public BooleanValue switchBlock_SwitchBack = new BooleanValue(7152, "SwitchBack", this, true);
    public BooleanValue sameY = new BooleanValue(9095,"SameY",this,true);

    public BooleanValue autoJump = new BooleanValue(1362,"AutoJump",this,true);
    public BooleanValue noToggleSound = new BooleanValue(10515,"NoToggleSound",this,true);
    public BooleanValue noNotify = new BooleanValue(8693,"NoNotify",this,true);
    public BooleanValue onWorld = new BooleanValue(11031,"DisableOnWorld",this,true);
    public BooleanValue suffix = new BooleanValue(6929,"Suffix",this,true);
    int lastY;
    int ticksInAir;
    int lastSlot;
    int slot;
    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.theWorld != null && mc.thePlayer != null) {
            lastY = mc.thePlayer.realPosY;
            ticksInAir = 0;
        }
        if (mc.thePlayer != null) {
            this.lastSlot = mc.thePlayer.inventory.currentItem;
        }
    }
    public void switchBlock() {
        if (this.switchBlock.getBoolean()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if (stack != null && stack.getItem() instanceof ItemBlock && !(stack.getItem() == InventoryUtils.invalidBlocks)) {

                    if (mc.thePlayer.getHeldItem() != null && (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) || getBlocksAmount() < 5) {
                        mc.thePlayer.inventory.currentItem = i;
                        this.slot = i;
                    }
                }
            }
        }

    }
    private int getBlocksAmount() {
        int amount = 0;
        for (int i = 36; i <= 44; i++) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.getItem() != InventoryUtils.invalidBlocks) {
                Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                ItemStack heldItem = mc.thePlayer.getHeldItem();
                if (heldItem != null && heldItem.equals(itemStack)) {
                    amount += itemStack.stackSize;
                }
            }
        }

        return amount;
    }
    @Override
    public void onDisable() {
        super.onDisable();
        lastY = 0;
        ticksInAir = 0;
        mm.scaffold.setToggled(false);
        if (this.autoJump.getBoolean() && mc.gameSettings.keyBindJump.pressed) {
            mc.gameSettings.keyBindJump.pressed = false;
        }
        this.switchBack();
        lastSlot = -1;
    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        if (onWorld.getBoolean()) {
            this.setToggled(false);
        }
        if (mc.theWorld != null && mc.thePlayer != null) {
            lastY = mc.thePlayer.realPosY;
            ticksInAir = 0;
        }
    }
    @EventTarget
    public void onEventTick(EventTick eventTick) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (!mc.thePlayer.onGround) {
                ticksInAir++;
            }
            else ticksInAir = 0;
        }
        else ticksInAir = 0;
        this.setSuffix(String.valueOf(ticksInAir),suffix.getBoolean());
    }
    public void switchBack() {
        if (lastSlot >= 0 && switchBlock.getBoolean() && switchBlock_SwitchBack.getBoolean() && switchBlock_SwitchBack.isVisible()) {
            mc.thePlayer.inventory.currentItem = this.lastSlot;
        }
    }
    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        this.switchBlock();

        if (!mc.thePlayer.onGround) {

            if (ticksInAir > this.airTicks.getValue()) {
                if (!(sameY.getBoolean() && mc.thePlayer.realPosY > lastY)) {
                    mm.scaffold.setToggled(true);
                }

            } else {
                mm.scaffold.setToggled(false);
            }
        }
        else {
            mm.scaffold.setToggled(false);
        }
        if (autoJump.getBoolean()) {
            mc.gameSettings.keyBindJump.pressed = Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air;
        }
    }
    @EventTarget
    public void onEventNotify(EventNotify eventNotify) {
        if (eventNotify.getModuleName().contains(mm.scaffold.getName())) {
            if (this.noNotify.getBoolean()) {
                eventNotify.setCancelled(true);
            }
        }
    }
    @EventTarget
    public void onEventToggleSound(EventToggleSound eventToggleSound) {
        if (eventToggleSound.getModuleName().contains(mm.scaffold.getName()) && noToggleSound.getBoolean()) {
            eventToggleSound.setCancelled(true);
        }
    }
}
