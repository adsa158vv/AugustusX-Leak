package net.augustus.utils.spoof;

import lombok.Getter;
import net.augustus.utils.interfaces.MC;
import net.minecraft.item.ItemStack;

/**
 * @author Genius
 * @since 2024/6/22 10:58
 * IntelliJ IDEA
 */

public class SlotSpoofer implements MC {


    private int spoofedSlot;

    @Getter
    private boolean spoofing;

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }

}
