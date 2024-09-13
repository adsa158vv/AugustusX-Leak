package net.augustus.modules.player;

import net.augustus.events.EventHandleGui;
import net.augustus.events.EventMove;
import net.augustus.events.EventSendPacket;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;

import net.augustus.utils.BlockUtil;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.PrePostUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.atani.RandomUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;


import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", new Color(1,1,1), Categorys.PLAYER);
    }
    private final BooleanValue openInventory = new BooleanValue(1,"OnlyInv", this, true);
    private final BooleanValue spoofInv = new BooleanValue(111,"SpoofInv",this,false);
    public final BooleanValue postClick = new BooleanValue(8799, "PostClick", this, false);
    private final BooleanValue noMove = new BooleanValue(314,"NoMove",this,true);
    public final BooleanValue noChest = new BooleanValue(144, "NoStealingChest", this, true);
    private final BooleanValue interactionCheck = new BooleanValue(1414,"InteractCheck",this,true);
    private final BooleanValue hyt = new BooleanValue(15488, "HytBetterArmor", this, false);
    private final BooleanValue chesetCheck = new BooleanValue(11313,"ChestCheck",this,true);
    private final DoubleValue minStartDelay = new DoubleValue(2,"MinStartDelay", this, 250L, 0L, 1000L, 0),
            maxStartDelay = new DoubleValue(3,"MaxStartDelay",  this, 300L, 0L, 1000L, 0),
            minThrowDelay = new DoubleValue(4,"MinThrowDelay", this, 250L, 0L, 1000L, 0),
            maxThrowDelay = new DoubleValue(5,"MaxThrowDelay", this, 300L, 0L, 1000L, 0);
    private final BooleanValue suffix = new BooleanValue(1414,"Suffix",this,true);
    private final BooleanValue disableOnWorld = new BooleanValue(124561,"DisableOnWorld",this,true);
    private final BooleanValue debug = new BooleanValue(487916, "Debug", this, false);
    private final List<ItemArmor> helmet = Arrays.asList(Items.leather_helmet, Items.golden_helmet, Items.chainmail_helmet, Items.iron_helmet, Items.diamond_helmet);
    private final List<ItemArmor> chest = Arrays.asList(Items.leather_chestplate, Items.golden_chestplate, Items.chainmail_chestplate, Items.iron_chestplate, Items.diamond_chestplate);
    private final List<ItemArmor> legging = Arrays.asList(Items.leather_leggings, Items.golden_leggings, Items.chainmail_leggings, Items.iron_leggings, Items.diamond_leggings);
    private final List<ItemArmor> boot = Arrays.asList(Items.leather_boots, Items.golden_boots, Items.chainmail_boots, Items.iron_boots, Items.diamond_boots);

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper throwTimer = new TimeHelper();

    @EventTarget
    public void onEventSendPacket(EventSendPacket e) {
        Packet<?> p = e.getPacket();

        if (p instanceof C0EPacketClickWindow && this.postClick.getBoolean()) {
            C0EPacketClickWindow c0e = (C0EPacketClickWindow) p;
            int windowId = c0e.getWindowId();
            int slotID = c0e.getSlotId();
            int useButton = c0e.getUsedButton();
            int mode = c0e.getMode();
            ItemStack stack = c0e.getClickedItem();
            short actionNumber = c0e.getActionNumber();

            e.setCancelled(true);

            if (PrePostUtil.isPost()) {

                PacketUtil.sendPacketNoEvent(new C0EPacketClickWindow(windowId, slotID, useButton, mode, stack, actionNumber));

            }

        }

    }

    @EventTarget
    public void onEvnetMove(EventMove eventMove) {
        this.setSuffix("Min:"+this.minThrowDelay.getValue()+"ms"+" Max"+this.maxThrowDelay.getValue()+"ms",this.suffix.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null);
    }
    @EventTarget
    public void onGui(EventHandleGui guiHandleEvent) {
        if (!MoveUtil.noMoveKey() && noMove.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                mc.thePlayer.setServerInv(mc.currentScreen);
            }
            return;
        }

        if (!(mc.currentScreen instanceof GuiChest) && noChest.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                mc.thePlayer.setServerInv(mc.currentScreen);
            }
            return;
        }

        if (interactionCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && this.interactionCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null
                && (Mouse.isButtonDown(1) || Mouse.isButtonDown(0) || mm.killAura.isToggled() && mm.killAura.target != null || BlockUtil.isScaffoldToggled())) {
            if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                mc.thePlayer.setServerInv(mc.currentScreen);
            }
            return;
        }
        if (this.chesetCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null ) {
            BlockPos blockPos = null;
            if (this.mc.objectMouseOver != null
                    && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                    && this.mc.objectMouseOver.getBlockPos() != null) {
                blockPos = this.mc.objectMouseOver.getBlockPos();

            }
            if (blockPos != null) {
                IBlockState iblockstate = this.mc.theWorld.getBlockState(blockPos);
                if (iblockstate != null) {


                    if (iblockstate.getBlock() instanceof BlockChest || iblockstate.getBlock() instanceof BlockEnderChest || iblockstate.getBlock() instanceof BlockFurnace || iblockstate.getBlock() instanceof BlockEnchantmentTable || iblockstate.getBlock() instanceof BlockHopper || iblockstate.getBlock() instanceof BlockDispenser) {
                        return;
                    }
                }
            }
        }
        if (mc.currentScreen instanceof GuiInventory) {
            if (!timeHelper.reached((long) (RandomUtil.randomBetween(this.minStartDelay.getValue(), this.maxStartDelay.getValue())))) {
                throwTimer.reset();
                return;
            }
        } else {
            timeHelper.reset();
            if (openInventory.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null)
                return;
        }
        if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            mc.thePlayer.setServerInv(new GuiInventory(mc.thePlayer));
        }
        for (int i = 5; i < 45; i++) {
            if (!MoveUtil.noMoveKey() && noMove.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
                if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                    mc.thePlayer.setServerInv(mc.currentScreen);
                }
                return;
            }

            if (!(mc.currentScreen instanceof GuiChest) && noChest.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
                if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                    mc.thePlayer.setServerInv(mc.currentScreen);
                }
                return;
            }

            if (interactionCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && this.interactionCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null
                    && (Mouse.isButtonDown(1) || Mouse.isButtonDown(0) || mm.killAura.isToggled() && mm.killAura.target != null || BlockUtil.isScaffoldToggled())) {
                if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.getServerInv() instanceof GuiInventory) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                    mc.thePlayer.setServerInv(mc.currentScreen);
                }
                return;
            }
            if (this.chesetCheck.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null ) {
                BlockPos blockPos = null;
                if (this.mc.objectMouseOver != null
                        && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                        && this.mc.objectMouseOver.getBlockPos() != null) {
                    blockPos = this.mc.objectMouseOver.getBlockPos();

                }
                if (blockPos != null) {
                    IBlockState iblockstate = this.mc.theWorld.getBlockState(blockPos);
                    if (iblockstate != null) {


                        if (iblockstate.getBlock() instanceof BlockChest || iblockstate.getBlock() instanceof BlockEnderChest || iblockstate.getBlock() instanceof BlockFurnace || iblockstate.getBlock() instanceof BlockEnchantmentTable || iblockstate.getBlock() instanceof BlockHopper || iblockstate.getBlock() instanceof BlockDispenser) {
                            return;
                        }
                    }
                }
            }
            if (mc.thePlayer != null && mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                long throwDelay = (long) RandomUtil.randomBetween(this.minThrowDelay.getValue(), this.maxThrowDelay.getValue());
                if (throwTimer.reached((long) (throwDelay))) {
                    if (is.getItem() instanceof ItemArmor && isTrashArmor(is)) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && helmet.contains(is.getItem()) && is == bestHelmet() && !mc.thePlayer.inventoryContainer.getSlot(5).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && chest.contains(is.getItem()) && is == bestChestplate() && !mc.thePlayer.inventoryContainer.getSlot(6).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && legging.contains(is.getItem()) && is == bestLeggings() && !mc.thePlayer.inventoryContainer.getSlot(7).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && boot.contains(is.getItem()) && is == bestBoots() && !mc.thePlayer.inventoryContainer.getSlot(8).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    }
                }
            }
        }
        if (this.spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }

    public boolean isFinished() {
        if (mc != null && mc.thePlayer != null && mc.thePlayer.inventoryContainer != null && mc.thePlayer.inventoryContainer.getInventory() != null) {
            if (mc.thePlayer.inventoryContainer.getInventory().contains(bestHelmet()) && mc.thePlayer.inventoryContainer.getSlot(5).getStack() != bestHelmet())
                return false;
            if (mc.thePlayer.inventoryContainer.getInventory().contains(bestChestplate()) && mc.thePlayer.inventoryContainer.getSlot(6).getStack() != bestChestplate())
                return false;
            if (mc.thePlayer.inventoryContainer.getInventory().contains(bestLeggings()) && mc.thePlayer.inventoryContainer.getSlot(7).getStack() != bestLeggings())
                return false;
            return !mc.thePlayer.inventoryContainer.getInventory().contains(bestBoots()) || mc.thePlayer.inventoryContainer.getSlot(8).getStack() == bestBoots();
        }
        return true;
    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        if (this.disableOnWorld.getBoolean()) {
            this.setToggled(false);
        }
    }
    public boolean isTrashArmor(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemArmor && helmet.contains(itemStack.getItem()) && itemStack != bestHelmet() && bestHelmet() != null)
            return true;
        if (itemStack.getItem() instanceof ItemArmor && chest.contains(itemStack.getItem()) && itemStack != bestChestplate() && bestChestplate() != null)
            return true;
        if (itemStack.getItem() instanceof ItemArmor && legging.contains(itemStack.getItem()) && itemStack != bestLeggings() && bestLeggings() != null)
            return true;
        return itemStack.getItem() instanceof ItemArmor && boot.contains(itemStack.getItem()) && itemStack != bestBoots() && bestBoots() != null;
    }

    public ItemStack bestHelmet() {
        ItemStack lastHelmetStack = null;
        ItemStack bestArmor = null;
        float armorSkill = -1;

        if (mc.thePlayer != null && mc.theWorld != null) {
            lastHelmetStack = mc.thePlayer.inventoryContainer.getSlot(5).getStack();
        }

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is != null && is.getItem() instanceof ItemArmor && helmet.contains(is.getItem()) && !this.hyt.getBoolean()) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                } else if (is != null && is.getItem() instanceof ItemArmor && helmet.contains(is.getItem()) && this.hyt.getBoolean()) {
                    if (lastHelmetStack != null && lastHelmetStack.getItem() != null) {
                        ItemArmor armor = (ItemArmor) is.getItem();
                        ItemArmor lastArmor = (ItemArmor) lastHelmetStack.getItem();

                        if (armor == Items.diamond_helmet && lastArmor == Items.diamond_helmet) {
                            float armorStrength = getFinalArmorStrength(is);
                            if (armorStrength >= armorSkill) {
                                armorSkill = getFinalArmorStrength(is);
                                bestArmor = is;
                            }
                        }
                        if (armor == Items.diamond_helmet && lastArmor != Items.diamond_helmet) {
                            armorSkill = getFinalArmorStrength(is);
                            bestArmor = is;
                        }
                        if (armor != Items.diamond_helmet && lastArmor == Items.diamond_helmet) {
                            armorSkill = getFinalArmorStrength(lastHelmetStack);
                            bestArmor = lastHelmetStack;
                        }
                        if (armor != Items.diamond_helmet && lastArmor != Items.diamond_helmet){
                            float armorStrength = getFinalArmorStrength(is);
                            if (armorStrength >= armorSkill) {
                                armorSkill = getFinalArmorStrength(is);
                                bestArmor = is;
                            }
                        }
                    } else {
                        float armorStrength = getFinalArmorStrength(is);
                        if (armorStrength >= armorSkill) {
                            armorSkill = getFinalArmorStrength(is);
                            bestArmor = is;
                        }
                    }

                }
            }
        }
        return bestArmor;
    }

    public ItemStack bestChestplate() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && chest.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }

        return bestArmor;
    }

    public ItemStack bestLeggings() {
        ItemStack lastLeggingsStack = null;
        ItemStack bestArmor = null;
        float armorSkill = -1;

        if (mc.thePlayer != null && mc.theWorld != null) {
            lastLeggingsStack = mc.thePlayer.inventoryContainer.getSlot(7).getStack();
        }

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is != null && is.getItem() instanceof ItemArmor && legging.contains(is.getItem()) && !this.hyt.getBoolean()) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                } else if (is != null && is.getItem() instanceof ItemArmor && legging.contains(is.getItem()) && this.hyt.getBoolean()) {

                    if (lastLeggingsStack != null && lastLeggingsStack.getItem() != null) {

                        ItemArmor foundArmor = (ItemArmor) is.getItem();

                        if (foundArmor == Items.diamond_leggings && (ItemArmor) lastLeggingsStack.getItem() == Items.diamond_leggings) {
                            float armorStrength = getFinalArmorStrength(is);
                            if (armorStrength >= armorSkill) {
                                armorSkill = getFinalArmorStrength(is);
                                bestArmor = is;
                            }
                        } 
                        if (foundArmor == Items.diamond_leggings && (ItemArmor) lastLeggingsStack.getItem() != Items.diamond_leggings) {
                            armorSkill = getFinalArmorStrength(is);
                            bestArmor = is;
                        }
                        if (foundArmor != Items.diamond_leggings && (ItemArmor) lastLeggingsStack.getItem() == Items.diamond_leggings) {
                            armorSkill = getFinalArmorStrength(lastLeggingsStack);
                            bestArmor = lastLeggingsStack;
                        }
                        if (foundArmor != Items.diamond_leggings && (ItemArmor) lastLeggingsStack.getItem() != Items.diamond_leggings){
                            float armorStrength = getFinalArmorStrength(is);
                            if (armorStrength >= armorSkill) {
                                armorSkill = getFinalArmorStrength(is);
                                bestArmor = is;
                            }
                        }

                    } else {
                        float armorStrength = getFinalArmorStrength(is);
                        if (armorStrength >= armorSkill) {
                            armorSkill = getFinalArmorStrength(is);
                            bestArmor = is;
                        }
                    }

                }
            }
        }
        return bestArmor;
    }

    public ItemStack bestBoots() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && boot.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }

        return bestArmor;
    }

    public float getFinalArmorStrength(ItemStack itemStack) {
        float damage = getArmorRating(itemStack);
        if (itemStack != null && itemStack.getItem() instanceof ItemArmor && this.hyt.getBoolean()) {
            ItemArmor armor = (ItemArmor) itemStack.getItem();

            if (armor == Items.chainmail_leggings || armor == Items.chainmail_helmet) {
                return 0;
            }

            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * 1.25F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack) * 0.33F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack) * 0.10F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.05F;
        } else if (itemStack != null && itemStack.getItem() instanceof ItemArmor && !this.hyt.getBoolean()) {

            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * 1.25F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) * 1.20F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack) * 0.33F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack) * 0.10F;
            damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.05F;
        }
        return damage;
    }

    public float getArmorRating(ItemStack itemStack) {
        float rating = 0;

        if (itemStack.getItem() instanceof ItemArmor) {
            final ItemArmor armor = (ItemArmor) itemStack.getItem();
            switch (armor.getArmorMaterial()) {
                case LEATHER:
                    rating = 1;
                    break;
                case GOLD:
                    rating = 2;
                    break;
                case CHAIN:
                    rating = 3;
                    break;
                case IRON:
                    rating = 4;
                    break;
                case DIAMOND:
                    rating = 5;
                    break;
            }
        }
        return rating;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }
}