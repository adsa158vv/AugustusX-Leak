package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.util.ChatComponentText;

public class GuiDownloadTerrain extends GuiScreen {
   private NetHandlerPlayClient netHandlerPlayClient;
   private int progress;
   private final GuiScreen previousGuiScreen;

   public GuiDownloadTerrain(GuiScreen previousGuiScreen , NetHandlerPlayClient netHandler) {
      this.netHandlerPlayClient = netHandler;
      this.previousGuiScreen = previousGuiScreen;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
   }

   @Override
   public void updateScreen() {
      ++this.progress;
      if (this.progress % 20 == 0) {
         this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      try {


         if (button.id == 0) {


            if (previousGuiScreen != null) {
               this.mc.displayGuiScreen(this.previousGuiScreen);
            } else {
               this.mc.displayGuiScreen(new GuiMultiplayer(this));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
