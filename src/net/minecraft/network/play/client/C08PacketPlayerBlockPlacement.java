package net.minecraft.network.play.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import viamcp.ViaMCP;
import viamcp.gui.GuiProtocolSelector;

import java.io.IOException;

import static net.augustus.utils.interfaces.MM.mm;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
   private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
   private BlockPos position;
   private int placedBlockDirection;
   private ItemStack stack;
   public float facingX;
   public float facingY;
   public float facingZ;

   public C08PacketPlayerBlockPlacement() {
   }

   public C08PacketPlayerBlockPlacement(ItemStack stackIn) {
      this(field_179726_a, 255, stackIn, 0.0F, 0.0F, 0.0F);
   }

   public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn) {
      this.position = positionIn;
      this.placedBlockDirection = placedBlockDirectionIn;
      this.stack = stackIn != null ? stackIn.copy() : null;
      this.facingX = facingXIn;
      this.facingY = facingYIn;
      this.facingZ = facingZIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      if (ViaMCP.getInstance().getVersion() <= 47 ) {
         this.position = buf.readBlockPos();
         this.placedBlockDirection = buf.readUnsignedByte();
         this.stack = buf.readItemStackFromBuffer();
         this.facingX = (float) buf.readUnsignedByte() / 16.0F;
         this.facingY = (float) buf.readUnsignedByte() / 16.0F;
         this.facingZ = (float) buf.readUnsignedByte() / 16.0F;
      }
      else {
         this.position = buf.readBlockPos();
         this.placedBlockDirection = buf.readUnsignedByte();
         this.stack = buf.readItemStackFromBuffer();
         this.facingX = (float) buf.readUnsignedByte();
         this.facingY = (float) buf.readUnsignedByte();
         this.facingZ = (float) buf.readUnsignedByte();
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      if (ViaMCP.getInstance().getVersion() <= 47 ) {

         if (!mm.disabler.grimplace.getBoolean()|| !mm.disabler.isToggled()) {

            buf.writeBlockPos(this.position);
            buf.writeByte(this.placedBlockDirection);
            buf.writeItemStackToBuffer(this.stack);
            buf.writeByte((int) (this.facingX * 16.0F));
            buf.writeByte((int) (this.facingY * 16.0F));
            buf.writeByte((int) (this.facingZ * 16.0F));
         }
         else {
            buf.writeBlockPos(this.position);
            buf.writeByte(6 + (this.placedBlockDirection * 7));
            buf.writeItemStackToBuffer(this.stack);
            buf.writeByte((int) (this.facingX * 16.0F));
            buf.writeByte((int) (this.facingY * 16.0F));
            buf.writeByte((int) (this.facingZ * 16.0F));
         }
      }
      else {

         if (!mm.disabler.grimplace.getBoolean() || !mm.disabler.isToggled()) {
            buf.writeBlockPos(this.position);
            buf.writeByte(this.placedBlockDirection);
            buf.writeItemStackToBuffer(this.stack);
            buf.writeByte((int)(this.facingX * 0.5F));
            buf.writeByte((int)(this.facingY * 0.5F));
            buf.writeByte((int)(this.facingZ * 0.5F));
         }
         else {
            buf.writeBlockPos(this.position);
            buf.writeByte(6 + (this.placedBlockDirection * 7));
            buf.writeItemStackToBuffer(this.stack);
            buf.writeByte((int)(this.facingX * 0.5F));
            buf.writeByte((int)(this.facingY * 0.5F));
            buf.writeByte((int)(this.facingZ * 0.5F));
         }
      }
   }


   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPlayerBlockPlacement(this);
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getPlacedBlockDirection() {
      return this.placedBlockDirection;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public float getPlacedBlockOffsetX() {
      return this.facingX;
   }

   public float getPlacedBlockOffsetY() {
      return this.facingY;
   }

   public float getPlacedBlockOffsetZ() {
      return this.facingZ;
   }
}
