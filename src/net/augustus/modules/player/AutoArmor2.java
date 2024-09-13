//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package net.augustus.modules.player;

import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;

import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.augustus.utils.skid.xylitol.TimeUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.gui.*;

import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.enchantment.*;

import java.awt.*;

public class AutoArmor2 extends Module
{

    public StringValue mode;
    private final BooleanValue drop;

    
    public AutoArmor2() {

        super("AutoArmor2", Color.cyan, Categorys.PLAYER);
        mode = new StringValue(5103, "Mode", this, "OpenInv", new String[]{"OpenInv", "SpoofInv","Basic"});
        this.drop = new BooleanValue(14518,"Drop",this, true);


    }
    
    @EventTarget
    public void onEventTick(EventTick event) {
        // LogUtil.addChatMessage("1");


        // LogUtil.addChatMessage("222");
        if (this.mode.getSelected().equals("OpenInv") && !(AutoArmor2.mc.currentScreen instanceof GuiInventory)) {
            // LogUtil.addChatMessage("2");
            return;
        }
        if ((AutoArmor2.mc.currentScreen == null || AutoArmor2.mc.currentScreen instanceof GuiInventory || AutoArmor2.mc.currentScreen instanceof GuiChat) ) {
            this.getBestArmor();
        }
    }
    
    public void getBestArmor() {
        // LogUtil.addChatMessage("3");
        for (int type = 1; type < 5; ++type) {
            if (AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                final ItemStack is = AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                }
                if (this.mode.getSelected().equals("SpoofInv")) {
                    final C16PacketClientStatus p = new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                    AutoArmor2.mc.thePlayer.sendQueue.addToSendQueue(p);
                }
                if (this.drop.getBoolean()) {
                    this.drop(4 + type);
                }
            }
            for (int i = 9; i < 45; ++i) {
                if (AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    final ItemStack is2 = AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is2, type) && getProtection(is2) > 0.0f) {
                        this.shiftClick(i);

                    }
                }
            }
        }
    }
    
    public static boolean isBestArmor(final ItemStack stack, final int type) {
        String strType = "";
        switch (type) {
            case 1: {
                strType = "helmet";
                break;
            }
            case 2: {
                strType = "chestplate";
                break;
            }
            case 3: {
                strType = "leggings";
                break;
            }
            case 4: {
                strType = "boots";
                break;
            }
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        final float protection = getProtection(stack);
        if (((ItemArmor)stack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN) {
            return false;
        }
        for (int i = 5; i < 45; ++i) {
            if (AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = AutoArmor2.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > protection && is.getUnlocalizedName().contains(strType)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void shiftClick(final int slot) {
        AutoArmor2.mc.playerController.windowClick(AutoArmor2.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, (EntityPlayer)AutoArmor2.mc.thePlayer);
    }
    
    public void drop(final int slot) {
        AutoArmor2.mc.playerController.windowClick(AutoArmor2.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, (EntityPlayer)AutoArmor2.mc.thePlayer);
    }
    
    public static float getProtection(final ItemStack stack) {
        float prot = 0.0f;
        if (stack.getItem() instanceof ItemArmor) {
            final ItemArmor armor = (ItemArmor)stack.getItem();
            prot += (float)(armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0);
        }
        return prot;
    }
    

    

}
