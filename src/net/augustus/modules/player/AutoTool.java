package net.augustus.modules.player;

import java.awt.Color;

import lombok.Getter;
import net.augustus.events.EventAttackEntity;
import net.augustus.events.EventClick;
import net.augustus.events.EventPostMouseOver;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.KillAura;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.MovingObjectPosition;



public class AutoTool extends Module {
   private final TimeHelper timeHelper = new TimeHelper();
   public BooleanValue switchBack = new BooleanValue(8702,"SwitchBack",this,true);
   public BooleanValue slientSwitch = new BooleanValue(1634,"SlientSwitch",this,false);

   @Getter
   private int slot = 0;
   private boolean shouldReturn = false;
   public AutoTool() {
      super("AutoTool", new Color(237, 237, 0), Categorys.PLAYER);
   }

   private int lastslot = -3;
   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.thePlayer != null && mc.theWorld != null) {
         this.slot = -1;
      }
      shouldReturn = false;
   }


   @Override
   public void onDisable() {
      super.onDisable();
      shouldReturn = false;
   }
   @EventTarget
   public void onEventAttackEntity(EventAttackEntity eventAttackEntity) {
       shouldReturn = eventAttackEntity.getTarget() != null;
   }


   @EventTarget
   public void onEventPostMouseOver(EventPostMouseOver eventPostMouseOver) {
      if (shouldReturn) {
         shouldReturn = false;
         return;
      }
      double lastDamage = 0.0;
      this.slot = -1;
      MovingObjectPosition objectPosition = mc.objectMouseOver;
      //答辩写法
      if (objectPosition != null) {
         for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null) {
               Item item = itemStack.getItem();


               if (objectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.gameSettings.keyBindAttack.isKeyDown() && (item instanceof ItemTool || item instanceof ItemShears)) {
                  double toolSpeed = this.getToolSpeed(itemStack);
                  double currentSpeed = this.getToolSpeed(mc.thePlayer.getHeldItem());
                  if (toolSpeed > 1.0 && toolSpeed > currentSpeed && toolSpeed > lastDamage) {
                     this.slot = i - 36;
                     lastDamage = toolSpeed;
                     this.timeHelper.reset();
                  }
               }
            }
         }
      }

      if (BlockUtil.isScaffoldToggled()) {
         this.slot = -1;
      }

      mc.playerController.syncCurrentPlayItem();
   }

   @EventTarget
   public void onEventClick(EventClick eventClick) {
      if (shouldReturn) {
         return;
      }
      if (!slientSwitch.getBoolean()) return;
      if (!BlockUtil.isScaffoldToggled() && this.slot != -1) {
         eventClick.setSlot(this.slot);

      }
   }
   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {

      if (shouldReturn) {

         return;
      }


      if (slientSwitch.getBoolean()) return;
      if (this.slot >=0 ) {




         if (!BlockUtil.isScaffoldToggled() && this.slot != -1 && this.slot >= 0) {
            lastslot = mc.thePlayer.inventory.currentItem;

            mc.thePlayer.inventory.currentItem = slot;
         }

      }
      if (lastslot >= 0 && !mc.thePlayer.isSwingInProgress || GuiOverlayDebug.lookingAtBlock instanceof BlockAir || (KillAura.target != null)) {
         if (lastslot <0 ) return;
         if (!switchBack.getBoolean() || slientSwitch.getBoolean()) return;
         mc.thePlayer.inventory.currentItem = lastslot;
         lastslot = -2;
      }
   }

   private double getToolSpeed(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack != null && (itemStack.getItem() instanceof ItemTool || itemStack.getItem() instanceof ItemShears)) {
         if (itemStack.getItem() instanceof ItemAxe) {
            damage += itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
               + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         } else if (itemStack.getItem() instanceof ItemPickaxe) {
            damage += itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
               + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         } else if (itemStack.getItem() instanceof ItemSpade) {
            damage += itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock())
               + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         } else if (itemStack.getItem() instanceof ItemShears) {
            System.out.println(itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()));
            damage += itemStack.getItem().getStrVsBlock(itemStack, mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
         }

         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) / 33.0;
         return damage - (double)itemStack.getItemDamage() / 10000.0;
      } else {
         return 0.0;
      }
   }

   private double getItemDamage(ItemStack itemStack) {
      double damage = 0.0;
      if (itemStack != null && itemStack.getItem() instanceof ItemTool) {
         damage += ((ItemTool)itemStack.getItem()).getDamageVsEntity();
         damage += (float)itemStack.getItem().getMaxDamage() + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemStack) / 11.0;
         damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) / 11.0;
         damage -= (double)itemStack.getItemDamage() / 10000.0;
      }

      return damage;
   }

}
