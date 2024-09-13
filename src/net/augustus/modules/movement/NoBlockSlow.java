package net.augustus.modules.movement;

import java.awt.Color;
import java.util.Map;

import net.augustus.events.EventPreMotion;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.augustus.utils.skid.xylitol.XylitolBlockUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;



public class NoBlockSlow extends Module {

   private final BooleanValue web = new BooleanValue(11079, "NoWeb", this, true);



   public NoBlockSlow() {
      super("NoBlockSlow", new Color(179, 252, 255), Categorys.MOVEMENT);
   }

   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      if (mc.thePlayer.isInWeb() && this.web.getBoolean()) {
         final Map<BlockPos, Block> searchBlock = XylitolBlockUtil.searchBlocks(5);
         for (final Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            if (NoBlockSlow.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb) {
               PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), NoBlockSlow.mc.objectMouseOver.sideHit));
               PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), NoBlockSlow.mc.objectMouseOver.sideHit));
            }
         }
         NoBlockSlow.mc.thePlayer.setInWeb(false);
      }
      //NoWeb from Xylitol.
   }
}
