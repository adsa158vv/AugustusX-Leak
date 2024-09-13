package net.augustus.modules.world;

import net.augustus.events.EventSendPacket;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.awt.*;

public class FastBreakBypass extends Module {


   public FastBreakBypass() {
      super("FastBreakBypass", new Color(52, 152, 219), Categorys.WORLD);
   }


      private final DoubleValue speed = new DoubleValue(8320,"Speed",this,1.1,0,3,2);
      private final BooleanValue abortPacketSpoof = new BooleanValue(13171,"AbortC07Spoof",this,true);
      private final BooleanValue speedCheckBypass = new BooleanValue(10719,"VanillaCheckBypass",this,true);
      private EnumFacing facing;
      private BlockPos pos;
      private boolean boost = false;
      private float damage = 0.0f;


      @Override
      public void onDisable() {
         if (FastBreakBypass.mc.thePlayer == null) {
            return;
         }
         if (this.speedCheckBypass.getBoolean()) {
            FastBreakBypass.mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
         }
      }


   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
         if (eventSendPacket.getPacket() instanceof C07PacketPlayerDigging) {
            if (((C07PacketPlayerDigging)eventSendPacket.getPacket()).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               this.boost = true;
               this.pos = ((C07PacketPlayerDigging)eventSendPacket.getPacket()).getPosition();
               this.facing = ((C07PacketPlayerDigging)eventSendPacket.getPacket()).getFacing();
               this.damage = 0.0f;
            }
            else if (((C07PacketPlayerDigging)eventSendPacket.getPacket()).getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || ((C07PacketPlayerDigging)eventSendPacket.getPacket()).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
               this.boost = false;
               this.pos = null;
               this.facing = null;
            }
         }
      }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
         if (this.speedCheckBypass.getBoolean()) {
            FastBreakBypass.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 89640, 2));
         }
         if (FastBreakBypass.mc.playerController.extendedReach()) {
            FastBreakBypass.mc.playerController.blockHitDelay = 0;
         }
         else if (this.pos != null && this.boost) {
            final IBlockState blockState = FastBreakBypass.mc.theWorld.getBlockState(this.pos);
            this.damage += (float)(blockState.getBlock().getPlayerRelativeBlockHardness((EntityPlayer)FastBreakBypass.mc.thePlayer, (World)FastBreakBypass.mc.theWorld, this.pos) * this.speed.getValue());
            if (this.damage >= 1.0f) {
               FastBreakBypass.mc.theWorld.setBlockState(this.pos, Blocks.air.getDefaultState(), 11);
               if (this.abortPacketSpoof.getBoolean()) {
                  PacketUtil.sendPacketNoEvent((Packet<?>)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
               }
               PacketUtil.sendPacketNoEvent((Packet<?>)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, this.facing));
               this.damage = 0.0f;
               this.boost = false;
            }
         }
      }
   }


