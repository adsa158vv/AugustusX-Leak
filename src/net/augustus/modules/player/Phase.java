package net.augustus.modules.player;

import java.awt.Color;

import net.augustus.events.EventBlockBoundingBox;
import net.augustus.events.EventPreMotion;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.StringValue;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.rise6.PlayerUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.augustus.utils.skid.xylitol.XylitolBlockUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Phase extends Module {
   private final TimeHelper tickTimeHelper = new TimeHelper();
   public StringValue mode =new StringValue(100123,"Mode",this,"Vanilla",new String[]{"Vanilla","Normal","FastFall"});
   private boolean phasing;
   public Phase() {
      super("Phase", new Color(45, 196, 148), Categorys.PLAYER);
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (mode.getSelected().equals("Vanilla")) {
         mc.thePlayer.motionY = 0.0;
         mc.thePlayer.onGround = true;
      }
      if (mode.getSelected().equals("FastFall")){
         mc.thePlayer.noClip = true;
         mc.thePlayer.motionY -= 10.0;
         mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ);
         mc.thePlayer.onGround = XylitolBlockUtil.collideBlockIntersects(mc.thePlayer.getEntityBoundingBox(),Block::isCollidable);
      }
   }
   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      if (mode.getSelected().equals("Normal")) {
         this.phasing = false;

         final double rotation = Math.toRadians(mc.thePlayer.rotationYaw);

         final double x = Math.sin(rotation);
         final double z = Math.cos(rotation);

         if (mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.setPosition(mc.thePlayer.posX - x * 0.005, mc.thePlayer.posY, mc.thePlayer.posZ + z * 0.005);
            this.phasing = true;
         } else if (PlayerUtil.insideBlock()) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - x * 1.5, mc.thePlayer.posY, mc.thePlayer.posZ + z * 1.5, false));

            mc.thePlayer.motionX *= 0.3D;
            mc.thePlayer.motionZ *= 0.3D;

            this.phasing = true;
         }
      }
   }
   @EventTarget
   public void BlockBB(EventBlockBoundingBox eventBlockBoundingBox) {
      if (mode.getSelected().equals("Normal")) {
         // Sets The Bounding Box To The Players Y Position.
         if (eventBlockBoundingBox.getBlock() instanceof BlockAir && phasing) {
            final double x = eventBlockBoundingBox.getBlockPos().getX(), y = eventBlockBoundingBox.getBlockPos().getY(), z = eventBlockBoundingBox.getBlockPos().getZ();

            if (y < mc.thePlayer.posY) {
               eventBlockBoundingBox.setAxisAlignedBB(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
         }
      }
   }
}
