// Decompiled with: CFR 0.152
// Class Version: 8
package net.augustus.modules.world;

import net.augustus.events.EventMove;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;

import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.xylitol.ItemUtils;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.augustus.utils.skid.xylitol.TimeUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import net.minecraft.util.MathHelper;


import java.awt.*;
import java.util.Random;

public class ChestStealer2
        extends Module {
    protected Random rand;
    private final BooleanValue pre = new BooleanValue(13347,"Pre", this,true);
    public final BooleanValue post = new BooleanValue(7794,"Post", this,false);
    public final BooleanValue sendPostC0FFix = new BooleanValue(10850,"SendPostC0FFix", this, true);
    private final BooleanValue chest = new BooleanValue(15509,"Chest", this,true);
    private final BooleanValue furnace = new BooleanValue(10216,"Furnace", this,true);
    private final BooleanValue brewingStand = new BooleanValue(15818,"BrewingStand", this,true);
    public static final TimeUtil timer = new TimeUtil();

    public static TimeUtil openChestTimer = new TimeUtil();
    private final DoubleValue delay = new DoubleValue(3957,"StealDelay", this,100.0, 0.0, 1000.0, 0);
    private final BooleanValue trash = new BooleanValue(3492,"PickTrash",this, true);

    private int nextDelay = 0;

    public ChestStealer2() {
        super("ChestStealer2", Color.cyan, Categorys.WORLD);
    }

    @EventTarget
    public void onEventMove(EventMove event) {

    //    LogUtil.addChatMessage("1");
        if (((this.post.getBoolean() && event.isPost()) || (this.pre.getBoolean() && event.isPre()))) {
         //   LogUtil.addChatMessage("2");
            int i;
            Container container;
            if (EntityPlayer.INSTANCE.openContainer == null) {
                System.err.println("ChestStealer2 Error: openContainer == null!");
                return;
            }
          //  LogUtil.addChatMessage("3");
            if (EntityPlayer.INSTANCE.openContainer instanceof ContainerFurnace && ((Boolean)this.furnace.getBoolean())) {
                container = EntityPlayer.INSTANCE.openContainer;
                if (this.isFurnaceEmpty((ContainerFurnace)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                    mc.thePlayer.closeScreen();
                    return;
                }
                for (i = 0; i < ((ContainerFurnace)container).tileFurnace.getSizeInventory(); ++i) {
                    if (((ContainerFurnace)container).tileFurnace.getStackInSlot(i) == null || !timer.delay(this.nextDelay)) continue;
                    if (((Boolean)this.sendPostC0FFix.getBoolean()) && ((Boolean)this.post.getBoolean())) {
                        
                        PacketUtil.sendPacketC0F();
                    }
                    ChestStealer2.mc.playerController.windowClick(((ContainerFurnace)container).windowId, i, 0, 1, mc.thePlayer);
                    this.nextDelay = (int)(this.delay.getValue() * MathHelper.getRandomDoubleInRange(this.rand,0.75, 1.25));
                    timer.reset();
                }
            }
            if (EntityPlayer.INSTANCE.openContainer instanceof ContainerBrewingStand && ((Boolean)this.brewingStand.getBoolean())) {
                container = EntityPlayer.INSTANCE.openContainer;
                if (this.isBrewingStandEmpty((ContainerBrewingStand)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                    mc.thePlayer.closeScreen();
                    return;
                }
                for (i = 0; i < ((ContainerBrewingStand)container).tileBrewingStand.getSizeInventory(); ++i) {
                    if (((ContainerBrewingStand)container).tileBrewingStand.getStackInSlot(i) == null || !timer.delay(this.nextDelay)) continue;
                    if (((Boolean)this.sendPostC0FFix.getBoolean()) && ((Boolean)this.post.getBoolean())) {
                        
                        PacketUtil.sendPacketC0F();
                    }
                    ChestStealer2.mc.playerController.windowClick(((ContainerBrewingStand)container).windowId, i, 0, 1, mc.thePlayer);
                    this.nextDelay = (int)((Double)this.delay.getValue() * MathHelper.getRandomDoubleInRange(this.rand,0.75, 1.25));
                    timer.reset();
                }
            }
            if (EntityPlayer.INSTANCE.openContainer instanceof ContainerChest && this.chest.getBoolean() ) {
              //  LogUtil.addChatMessage("4");
                container = EntityPlayer.INSTANCE.openContainer;
                if (this.isChestEmpty((ContainerChest)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                    mc.thePlayer.closeScreen();
                    return;
                }
                for (i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                    if (((ContainerChest)container).getLowerChestInventory().getStackInSlot(i) == null || !timer.delay(this.nextDelay) || !this.isItemUseful((ContainerChest)container, i) && !((Boolean)this.trash.getBoolean())) continue;
                    if (((Boolean)this.sendPostC0FFix.getBoolean()) && ((Boolean)this.post.getBoolean())) {
                        
                        PacketUtil.sendPacketC0F();
                    }
                    ChestStealer2.mc.playerController.windowClick(((ContainerChest)container).windowId, i, 0, 1, mc.thePlayer);
                    this.nextDelay = (int)((Double)this.delay.getValue() * MathHelper.getRandomDoubleInRange(this.rand,0.75, 1.25));
                    timer.reset();
                }
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || !this.isItemUseful(c, i) && !((Boolean)this.trash.getBoolean())) continue;
            return false;
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return true;
        }
        if (item instanceof ItemFood) {
            return true;
        }
        if (item instanceof ItemBow || item == Items.arrow) {
            return true;
        }
        if (item instanceof ItemPotion && !ItemUtils.isPotionNegative(itemStack)) {
            return true;
        }
        if (item instanceof ItemSword && ItemUtils.isBestSword(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemArmor && ItemUtils.isBestArmor(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            return true;
        }
        return item instanceof ItemEnderPearl;
    }
}
