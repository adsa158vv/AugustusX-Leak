package net.augustus.modules.player;

import lombok.Getter;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.KillAura;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.gotaj.InventoryUtils;
import net.augustus.utils.skid.gotaj.Timer;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;

import java.awt.*;


public class AutoWeapon extends Module {
   public AutoWeapon() {
      super("AutoWeapon", new Color(237, 237, 0), Categorys.PLAYER);
   }
   private BooleanValue switchBack = new BooleanValue(4104,"SwitchBack",this,true);
   public Timer timer = new Timer();
   private boolean isAttacking = false;
   private int lastSlot = -3;

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {

      if(isAttacking) {


          if (mc.thePlayer.getHeldItem() != bestWeapon()) lastSlot = mc.thePlayer.inventory.currentItem;
          for (int i = 36; i < 45; i++) {


              if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && mc.thePlayer.inventoryContainer.getSlot(i).getStack() == bestWeapon() && mc.thePlayer.inventory.currentItem != i - 36) {
                  if (mc.thePlayer.inventory.currentItem != i - 36) mc.thePlayer.inventory.currentItem = i - 36;
                  InventoryUtils.timer.reset();
                  break;
              }

          }
          isAttacking = false;
      } else {
         if (lastSlot >= 0 && !mc.thePlayer.isSwingInProgress || !(GuiOverlayDebug.lookingAtBlock instanceof BlockAir || GuiOverlayDebug.lookingAtBlock == null)) {
            if (lastSlot >=0 && switchBack.getBoolean()) {
               mc.thePlayer.inventory.currentItem = lastSlot;
               lastSlot = -2;
            }
         }
      }
   }
   @EventTarget
   public void onEventAttackEntity(EventAttackEntity eventAttackEntity) {


         isAttacking = true;



   }

   public ItemStack bestWeapon()
   {
      ItemStack bestWeapon = null;
      float itemDamage = -1;

      for (int i = 36; i < 45; i++)
      {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
         {
            final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe)
            {
               float toolDamage = getItemDamage(is);

               if (toolDamage >= itemDamage)
               {
                  itemDamage = getItemDamage(is);
                  bestWeapon = is;
               }
            }
         }
      }

      return bestWeapon;
   }

   public float getItemDamage(ItemStack itemStack)
   {
      float damage = InventoryUtils.getToolMaterialRating(itemStack, true);
      damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
      damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.50F;
      damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
      damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;

      if (itemStack.getItem() instanceof ItemSword)
      {
         damage += 0.2F;
      }

      return damage;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      isAttacking = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      isAttacking = false;
   }
}
