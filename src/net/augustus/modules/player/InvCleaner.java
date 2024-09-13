package net.augustus.modules.player;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.*;
import net.augustus.utils.skid.atani.RandomUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class InvCleaner extends Module {
    public InvCleaner() {
        super("InvCleaner",new Color(1,1,1), Categorys.PLAYER);
    }
    private final BooleanValue openInventory = new BooleanValue(1,"OnlyInv",  this, true);
    private final BooleanValue spoofInv = new BooleanValue(2,"SpoofInv",this,true);
    private final BooleanValue noMove = new BooleanValue(114,"NoMove",this,true);
    private final BooleanValue interactionCheck = new BooleanValue(1134,"InteractCheck",this,true);
    public final BooleanValue postClick = new BooleanValue(1549, "PostClick", this , false);
    private final BooleanValue chesetCheck = new BooleanValue(11313,"ChestCheck",this,true);

    private final DoubleValue minStartDelay = new DoubleValue(3,"MinStartDelay", this, 250L, 0L, 1000L, 0),
            maxStartDelay = new DoubleValue(4,"MaxStartDelay",  this, 300L, 0L, 1000L, 0),
            minThrowDelay = new DoubleValue(5,"MinThrowDelay", this, 250L, 0L, 1000L, 0),
            maxThrowDelay = new DoubleValue(6,"MaxThrowDelay",  this, 300L, 0L, 1000L, 0);
    private final BooleanValue preferSwords = new BooleanValue(7,"PreferSwords",  this, true),
            keepTools = new BooleanValue(8,"KeepTools",  this, true);
    private final DoubleValue weaponSlot = new DoubleValue(9,"WeaponSlot", this, 1, 0, 9, 0);
    private final DoubleValue blockSlot = new DoubleValue(91,"BlockSlot", this, 2, 0, 9, 0);
    private final DoubleValue gappleSlot = new DoubleValue(131,"GoldAppleSlot",this,3,0,9,0);
    private final DoubleValue bowSlot = new DoubleValue(10,"BowSlot", this, 2, 0, 9, 0);
    private final DoubleValue pickaxeSlot = new DoubleValue(11,"PickaxeSlot", this, 0, 0, 9, 0);
    private final DoubleValue axeSlot = new DoubleValue(12,"AxeSlot", this, 0, 0, 9, 0);
    private final DoubleValue shovelSlot = new DoubleValue(13,"ShovelSlot", this, 0, 0, 9, 0);
    private final BooleanValue throwFishingRods = new BooleanValue(1315315,"ThrowFishingRods", this, false);
    private final BooleanValue suffix = new BooleanValue(1414,"Suffix",this,true);
    private final BooleanValue disableOnWorld = new BooleanValue(124561,"DisableOnWorld",this,true);

    private List<Item> trashItems = Collections.emptyList();
    private AutoArmor autoArmor;
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
    public void onEventMove(EventMove eventMove) {
        this.setSuffix("Min:"+this.minThrowDelay.getValue()+"ms"+" Max"+this.maxThrowDelay.getValue()+"ms",this.suffix.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null);
        if (throwFishingRods.getBoolean() && (trashItems != Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner, Items.fishing_rod))) {
            trashItems = Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner, Items.fishing_rod);
        } else if (trashItems != Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner, Items.fishing_rod)) {
            trashItems = Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner);
        }
    }

    @EventTarget
    public void onGui(EventHandleGui guiHandleEvent) {
        if(autoArmor == null)
            autoArmor = mm.autoArmor;
        if (!MoveUtil.noMoveKey() && noMove.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
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

        if (autoArmor.isToggled() && !autoArmor.isFinished()) {
            timeHelper.reset();
            throwTimer.reset();
            if (openInventory.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null)
                return;
        }
        if (spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            mc.thePlayer.setServerInv(new GuiInventory(mc.thePlayer));
        }
        for (int i = 9; i < 45; i++) {
            if (!MoveUtil.noMoveKey() && noMove.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
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
            if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null && mc.thePlayer.inventoryContainer.getSlot(i) != null && mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                long throwDelay = (long) RandomUtil.randomBetween(this.minThrowDelay.getValue(), this.maxThrowDelay.getValue());
                if (throwTimer.reached((long) (throwDelay))) {
                    if (weaponSlot.getValue() != 0 && (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) && is == bestWeapon() && mc.thePlayer.inventoryContainer.getInventory().contains(bestWeapon()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + weaponSlot.getValue())).getStack() != is && !preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (weaponSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (weaponSlot.getValue() != 0 && is.getItem() instanceof ItemSword && is == bestSword() && mc.thePlayer.inventoryContainer.getInventory().contains(bestSword()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + weaponSlot.getValue())).getStack() != is && preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (weaponSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (bowSlot.getValue() != 0 && is.getItem() instanceof ItemBow && is == bestBow() && mc.thePlayer.inventoryContainer.getInventory().contains(bestBow()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + bowSlot.getValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (bowSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (pickaxeSlot.getValue() != 0 && is.getItem() instanceof ItemPickaxe && is == bestPick() && is != bestWeapon() && keepTools.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.inventoryContainer.getInventory().contains(bestPick()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + pickaxeSlot.getValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (pickaxeSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (blockSlot.getValue() != 0 && is.getItem() instanceof ItemBlock && mc.thePlayer.inventoryContainer.getSlot((int) (35 + blockSlot.getValue())).getStack() != is) {
                        if ((mc.thePlayer.inventory.getStackInSlot((int) (blockSlot.getValue() - 1)) != null) && (mc.thePlayer.inventory.getStackInSlot((int) (blockSlot.getValue() - 1)).getItem() instanceof ItemBlock)) continue;
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (blockSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (axeSlot.getValue() != 0 && is.getItem() instanceof ItemAxe && is == bestAxe() && is != bestWeapon() && keepTools.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.inventoryContainer.getInventory().contains(bestAxe()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + axeSlot.getValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (axeSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (shovelSlot.getValue() != 0 && is.getItem() instanceof ItemSpade && is == bestShovel() && is != bestWeapon() && keepTools.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc.thePlayer.inventoryContainer.getInventory().contains(bestShovel()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + shovelSlot.getValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (shovelSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (gappleSlot.getValue() != 0 && is.getItem() instanceof ItemAppleGold && mc.thePlayer.inventoryContainer.getSlot((int) (35 + gappleSlot.getValue())).getStack() != is) {
                        if (mc.thePlayer.inventory.getStackInSlot((int) (gappleSlot.getValue() - 1)) != null && mc.thePlayer.inventory.getStackInSlot((int) gappleSlot.getValue() - 1 ).getItem() instanceof ItemAppleGold) continue;
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (gappleSlot.getValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    } else if (trashItems.contains(is.getItem()) || isBadStack(is)) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay != 0) {
                            break;
                        }
                    }
                }
            }
        }
        if (this.spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }
    @EventTarget
    public void onClick(EventClick eventClick) {


    }
    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        if (this.disableOnWorld.getBoolean()) {
            this.setToggled(false);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.spoofInv.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null && mc != null && mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
            mc.thePlayer.setServerInv(mc.currentScreen);
        }
    }

    public boolean isBadStack(ItemStack is) {
        if ((is.getItem() instanceof ItemSword) && is != bestWeapon() && !preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null)
            return true;
        if (is.getItem() instanceof ItemSword && is != bestSword() && preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null)
            return true;
        if (is.getItem() instanceof ItemBow && is != bestBow())
            return true;
        if (keepTools.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            if (is.getItem() instanceof ItemAxe && is != bestAxe() && (preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && is != bestPick() && (preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null || is != bestWeapon()))
                return true;
            return is.getItem() instanceof ItemSpade && is != bestShovel();
        } else {
            if (is.getItem() instanceof ItemAxe && (preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && (preferSwords.getBoolean() && mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null || is != bestWeapon()))
                return true;
            return is.getItem() instanceof ItemSpade;
        }
    }

    public ItemStack bestWeapon() {
        ItemStack bestWeapon = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) {
                    float toolDamage = getItemDamage(is);
                    if (toolDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestWeapon = is;
                    }
                }
            }
        }

        return bestWeapon;
    }

    public ItemStack bestSword() {
        ItemStack bestSword = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float swordDamage = getItemDamage(is);
                    if (swordDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestSword = is;
                    }
                }
            }
        }

        return bestSword;
    }

    public ItemStack bestBow() {
        ItemStack bestBow = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBow) {
                    float bowDamage = getBowDamage(is);
                    if (bowDamage >= itemDamage) {
                        itemDamage = getBowDamage(is);
                        bestBow = is;
                    }
                }
            }
        }

        return bestBow;
    }

    public ItemStack bestAxe() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemAxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public ItemStack bestPick() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPickaxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public ItemStack bestShovel() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSpade) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    public float getToolRating(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, false);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.00F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;
        return damage;
    }

    public float getItemDamage(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, true);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;

        if (itemStack.getItem() instanceof ItemSword)
            damage += 0.2;
        return damage;
    }

    public float getBowDamage(ItemStack itemStack) {
        float damage = 5;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += itemStack.getMaxDamage() - itemStack.getItemDamage() * 0.001F;
        return damage;
    }

    public float getToolMaterialRating(ItemStack itemStack, boolean checkForDamage) {
        final Item is = itemStack.getItem();
        float rating = 0;

        if (is instanceof ItemSword) {
            switch (((ItemSword) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 4;
                    break;
                case "STONE":
                    rating = 5;
                    break;
                case "IRON":
                    rating = 6;
                    break;
                case "EMERALD":
                    rating = 7;
                    break;
            }
        } else if (is instanceof ItemPickaxe) {
            switch (((ItemPickaxe) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 2;
                    break;
                case "STONE":
                    rating = 3;
                    break;
                case "IRON":
                    rating = checkForDamage ? 4 : 40;
                    break;
                case "EMERALD":
                    rating = checkForDamage ? 5 : 50;
                    break;
                default:
                    break;
            }
        } else if (is instanceof ItemAxe) {
            switch (((ItemAxe) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 3;
                    break;
                case "STONE":
                    rating = 4;
                    break;
                case "IRON":
                    rating = 5;
                    break;
                case "EMERALD":
                    rating = 6;
                    break;
                default:
                    break;
            }
        } else if (is instanceof ItemSpade) {
            switch (((ItemSpade) is).getToolMaterialName()) {
                case "GOLD":
                case "WOOD":
                    rating = 1;
                    break;
                case "STONE":
                    rating = 2;
                    break;
                case "IRON":
                    rating = 3;
                    break;
                case "EMERALD":
                    rating = 4;
                    break;
                default:
                    break;
            }
        }

        return rating;
    }
}
