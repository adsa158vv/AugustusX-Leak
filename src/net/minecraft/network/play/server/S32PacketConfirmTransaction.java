package net.minecraft.network.play.server;

import java.io.IOException;

import net.augustus.modules.exploit.PostDis;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import static net.augustus.utils.interfaces.MM.mm;

public class S32PacketConfirmTransaction implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private short actionNumber;
   private boolean field_148893_c;

   public S32PacketConfirmTransaction() {
   }

   public S32PacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean p_i45182_3_) {
      this.windowId = windowIdIn;
      this.actionNumber = actionNumberIn;
      this.field_148893_c = p_i45182_3_;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      if (handler != null) {
         handler.handleConfirmTransaction(this);
      }
      else {
         System.err.println("Handler is null!");
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      if (mm.postDis.isToggled() && PostDis.getGrimPost()) {


         this.windowId = buf.readUnsignedByte();
         this.actionNumber = buf.readShort();
         this.field_148893_c = buf.readBoolean();
         if (mm.postDis.isToggled() && PostDis.getGrimPost() && this.actionNumber < 0) {
            PostDis.pingPackets.add((int) this.actionNumber);
         }
      }
      else {
         this.windowId = buf.readUnsignedByte();
         this.actionNumber = buf.readShort();
         this.field_148893_c = buf.readBoolean();
      }
   }


   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.actionNumber);
      buf.writeBoolean(this.field_148893_c);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public short getActionNumber() {
      return this.actionNumber;
   }

   public boolean func_148888_e() {
      return this.field_148893_c;
   }

   public void setActionNumber(short actionNumber) {
      this.actionNumber = actionNumber;
   }
}
