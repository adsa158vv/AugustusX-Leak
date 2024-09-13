package net.minecraft.client.gui;

import net.augustus.Augustus;
import net.augustus.changeLog.ChangeLogType;
import net.augustus.utils.skid.lorious.ColorUtils;
import net.augustus.utils.skid.lorious.LoriousButton;
import net.augustus.utils.skid.lorious.Timer;
import net.augustus.utils.skid.lorious.anims.Animation;
import net.augustus.utils.skid.lorious.anims.Easings;
import net.augustus.utils.skid.ohare.font.Fonts;
import net.minecraft.client.main.Main;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GuiMainMenu
        extends GuiScreen {
   public Animation xPos = new Animation();
   public Animation yPos = new Animation();
   public Timer timer = new Timer();
   public boolean initialized;
   public List<LoriousButton> loriousButtons = new ArrayList<>();

   public void initGui() {
      ScaledResolution sr = new ScaledResolution(this.mc);
      this.xPos.animate(sr.getScaledWidth(), 0, Easings.QUINT_OUT);
      this.yPos.animate(sr.getScaledHeight(), 0, Easings.QUINT_OUT);
      if (!this.initialized) {
         this.timer.startTimer();
         this.initialized = true;
      }
      loriousButtons.clear();
      this.loriousButtons.add(new LoriousButton(0, 0, 25, "SinglePlayer"));
      this.loriousButtons.add(new LoriousButton(1, 0, 47, "MultiPlayer"));
      this.loriousButtons.add(new LoriousButton(2, 0, 69, "Options"));
      this.loriousButtons.add(new LoriousButton(666, 0, 91, "ClientManager"));
      this.loriousButtons.add(new LoriousButton(420, 0, 113, "AltManager"));
      this.loriousButtons.add(new LoriousButton(3, 0, 135, "Exit"));
   }


   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution sr = new ScaledResolution(this.mc);
      final HashMap<String, ChangeLogType> changes = new HashMap();
      Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(25, 25, 25).getRGB());

      String ez = "Welcome "+Augustus.getInstance().getUser();

      if (sr.getScaledWidth() > 600 && sr.getScaledHeight() > 300) {
         Color color = new Color(200, 200, 200, 150);
         Fonts.hudfont.drawString("Build"+" "+Augustus.getInstance().getVersion()+" Code-" +Augustus.getInstance().getCodeversion()+ " "+Augustus.getInstance().getRelease(),5, 5, color.hashCode());
         Fonts.hudfont.drawString("Developers:"+" "+Augustus.getInstance().getDev(), 5, 15, color.hashCode());
         Fonts.hudfont.drawString("Some Bypasses From"+" "+Augustus.getInstance().getSrcgiver(), 5, 25, color.hashCode());
         Fonts.hudfont.drawString("此版本应该是倒数第三个版本了，出于对最近购买了客户端的人的抱歉，我还会继续更新三个版本。", 5, 35, color.hashCode());
         int i = 0;
         for (Map.Entry<String, ChangeLogType> str : changes.entrySet()) {
            Fonts.hudfont.drawString(str.getKey(), 5, 16 + i * 12, str.getValue().getColor().getRGB() );
            i++;
         }
      }
      Fonts.hudfont.drawString(ez, sr.getScaledWidth() - Fonts.hudfont.getStringWidth(ez) - 5F, sr.getScaledHeight() - 10f, new Color(255,255,255,128).getRGB());
      Fonts.hudfont.drawString("AugustusX" + " b" + Augustus.getInstance().getVersion() + " " + Augustus.getInstance().getRelease(),5, sr.getScaledHeight() - 10f, new Color(255,255,255,128).getRGB());
      Augustus.getInstance().getLoriousFontService().getComfortaa38().drawCenteredString("AugustusX", (float)(this.xPos.getValue() / 2.0), (float)(this.yPos.getValue() / 2.0 - 20.0 - (sr.getScaledHeight() / 8)), -1, true, ColorUtils.getRainbow(4.0f, 0.5f, 1.0f), new Color(255, 255, 255));
      for (LoriousButton loriousButton : this.loriousButtons) {
         loriousButton.draw(mouseX, mouseY, (int)(this.xPos.getValue() / 2.0 - 100.0), (int)(this.yPos.getValue() / 2.0 - (sr.getScaledHeight() / 8)));
      }
      if (this.xPos.getTarget() != (double)sr.getScaledWidth()) {
         this.xPos.animate(sr.getScaledWidth(), 0, Easings.QUINT_OUT);
      }
      if (this.yPos.getTarget() != (double)sr.getScaledHeight()) {
         this.yPos.animate(sr.getScaledHeight(), 0, Easings.QUINT_OUT);
      }
      this.xPos.updateAnimation();
      this.yPos.updateAnimation();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      for (LoriousButton loriousButton : this.loriousButtons) {
         loriousButton.onClick(mouseX, mouseY, mouseButton);
      }
   }
}
