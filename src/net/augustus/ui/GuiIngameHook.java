package net.augustus.ui;

import com.sun.javafx.geom.Vec2d;
import net.augustus.Augustus;
import net.augustus.events.EventRender2D;
import net.augustus.font.CustomFontUtil;
import net.augustus.font.UnicodeFontRenderer;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.modules.Module;
import net.augustus.notify.augx.AugXNotifyManager;
import net.augustus.notify.double_.DoubleNotificationManager;
import net.augustus.notify.reborn.CleanNotificationManager;
import net.augustus.notify.rise5.Notification;
import net.augustus.notify.rise5.NotificationManager;
import net.augustus.notify.stable.StableNotifyManager;
import net.augustus.notify.custom.CustomNotifyManager;
import net.augustus.notify.xenza.NotificationsManager;
import net.augustus.utils.*;
import net.augustus.utils.RenderUtil;
import net.augustus.utils.interfaces.MC;
import net.augustus.utils.interfaces.MM;
import net.augustus.utils.skid.gelory.BlendUtils;
import net.augustus.utils.skid.gelory.EXRenderUtils;
import net.augustus.utils.skid.lorious.ColorUtils;
import net.augustus.utils.skid.lorious.RectUtils;
import net.augustus.utils.skid.lorious.anims.Easings;
import net.augustus.utils.skid.tenacity.*;
import net.augustus.utils.skid.tenacity.animations.ContinualAnimation;
import net.augustus.utils.skid.vestige.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.stonks.IntegrityChecks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static net.augustus.modules.combat.KillAura.target;

public class GuiIngameHook extends GuiIngame implements MM, MC {
   private final boolean leftSide = false;
   private final int lastWidth = 0;
   private float yAdd = 0.0F;
   public static float lastArrayListY;
   private final RainbowUtil rainbowUtil = new RainbowUtil();
   public static CustomFontUtil customFont;
   private ResourceLocation augustusResourceLocation = null;
   private ResourceLocation amogusResourceLocation = null;
   private static UnicodeFontRenderer ryuFontRenderer;
   private static UnicodeFontRenderer oldFontRenderer;
   private static UnicodeFontRenderer vegaFontRenderer;
   public static float alpha = 4.0F;
   public float rectSize = 1.0F;
   public float saturationDelay = 1F;
   public boolean underLine = true;

   private final ContinualAnimation animation = new ContinualAnimation();


   static {
      IntegrityChecks.XENZAISFREE();
      try {
         ryuFontRenderer = new UnicodeFontRenderer(Font.createFont(0, GuiIngameHook.class.getResourceAsStream("/resources/psr.ttf")).deriveFont(20F));
      } catch (Exception e) {
         e.printStackTrace();
      }
      try {
         oldFontRenderer = new UnicodeFontRenderer(Font.createFont(0, GuiIngameHook.class.getResourceAsStream("/resources/Comfortaa-Bold.ttf")).deriveFont(56F));
      } catch (Exception e) {
         e.printStackTrace();
      }
      try {
         vegaFontRenderer = new UnicodeFontRenderer(Font.createFont(0, GuiIngameHook.class.getResourceAsStream("/resources/roboto.ttf")).deriveFont(18F));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private int yOff;


   public static Color getChroma() {
      Color color = new Color(Color.HSBtoRGB(((float) System.nanoTime() + -0.0F) / 2.0F / 1.0E9F, 1.0F, 1.0F));
      return new Color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
   }

   public GuiIngameHook(Minecraft client) {
      super(client);
      augustusResourceLocation = ResourceUtil.loadResourceLocation("pictures/augustus.png", "augustus");
      amogusResourceLocation = ResourceUtil.loadResourceLocation("pictures/amogus.png", "amogus");
      customFont = new CustomFontUtil("Verdana", 16);
   }

   @Override
   public void renderGameOverlay(float partialTicks) {
      super.renderGameOverlay(partialTicks);
      ScaledResolution sr = new ScaledResolution(mc);

      EventRender2D eventRender2D=new EventRender2D(sr.getScaledWidth(),sr.getScaledHeight());
      EventHandler.call(eventRender2D);
      if (mm.notifications.isToggled()) {
         switch (mm.notifications.mode.getSelected()) {
            case "Custom": {
               CustomNotifyManager.renderNotifications();
               CustomNotifyManager.update();
               break;
            }
            case "Stable": {
               StableNotifyManager.renderNotifications();
               StableNotifyManager.update();
               break;
            }
            case "AugustusX": {
               AugXNotifyManager.renderNotifications();;
               AugXNotifyManager.update();
               break;
            }
            case "Xenza": {
               NotificationsManager.renderNotifications();
               NotificationsManager.update();
               break;
            }
            case "Double": {
               DoubleNotificationManager.renderCleanNotifications();
               DoubleNotificationManager.update();
               break;
            }
            case "New": {
               CleanNotificationManager.renderCleanNotifications();
               CleanNotificationManager.update();
               break;
            }
            case "Rise5": {
               if (!NotificationManager.notifications.isEmpty()) {
                  if (NotificationManager.notifications.getFirst().getEnd() > System.currentTimeMillis()) {
                     NotificationManager.notifications.getFirst().y = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 50;
                     NotificationManager.notifications.getFirst().render();
                  } else {
                     NotificationManager.notifications.removeFirst();
                  }
               }

               if (!NotificationManager.notifications.isEmpty()) {
                  int i = 0;
                  try {
                     for (final Notification notification : NotificationManager.notifications) {
                        if (i == 0) {
                           i++;
                           continue;
                        }

                        notification.y = (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 18) - (35 * (i + 1));
                        notification.render();
                        i++;
                     }
                  } catch (final
                  ConcurrentModificationException ignored) {
                  }
               }
               if (!NotificationManager.notifications.isEmpty()) {
                  int i = 0;
                  try {
                     for (final Notification notification : NotificationManager.notifications) {
                        if (i == 0) {
                           i++;
                           continue;
                        }

                        notification.y = (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 18) - (35 * (i + 1));
                        notification.render();
                        i++;
                     }
                  } catch (final
                  ConcurrentModificationException ignored) {
                  }
               }
               break;
            }
         }
      }
      if (mm.arrayList.isToggled()) {
         switch (mm.arrayList.mode.getSelected()) {
            case "New": {
               double y = 0.0;
               mm.getModules().sort(Comparator.comparingDouble(module -> -Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(module.getDisplayName())));
               int index = 0;
               for (Module module2 : mm.getModules()) {
                  module2.getXPos().updateAnimation();
                  module2.getYPos().updateAnimation();
                  module2.updatePos();
                  if (module2.getYPos().getTarget() != 5.0 + y) {
                     module2.getYPos().animate(5.0 + y, 350.0, Easings.QUAD_BOTH);
                  }
                  if (module2.getXPos().getValue() <= -5.0) continue;
                  Color clr = ColorUtils.getGradientOffset(new Color(255, 255, 255), ColorUtils.getRainbow(4.0f, 0.5f, 1.0f), (double) Math.abs(System.currentTimeMillis() / 16L) / 50.0 + (double) index * 0.25 - 0.25);
                  Color clr1 = ColorUtils.getGradientOffset(new Color(255, 255, 255), ColorUtils.getRainbow(4.0f, 0.5f, 1.0f), (double) Math.abs(System.currentTimeMillis() / 16L) / 50.0 + (double) index * 0.25);
                  RectUtils.drawRoundedRect((double) sr.getScaledWidth() - module2.getXPos().getValue() - 8.0, module2.getYPos().getValue(), (double) sr.getScaledWidth() + Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(module2.getDisplayName()) - module2.getXPos().getValue(), module2.getYPos().getValue() + 4.0 + (double) Augustus.getInstance().getLoriousFontService().getComfortaa18().getHeight() + 2.0, 2.0, new Color(0, 0, 0, 125).getRGB());
                  RectUtils.drawGradientRect((double) sr.getScaledWidth() + Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(module2.getDisplayName()) - module2.getXPos().getValue() - 2.0, module2.getYPos().getValue(), (double) sr.getScaledWidth() + Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(module2.getDisplayName()) - module2.getXPos().getValue(), module2.getYPos().getValue() + 4.0 + (double) Augustus.getInstance().getLoriousFontService().getComfortaa18().getHeight() + 2.0, clr.getRGB(), clr1.getRGB());
                  Augustus.getInstance().getLoriousFontService().getComfortaa18().drawStringWithShadow(module2.getName(), (double) (sr.getScaledWidth() - 5) - module2.getXPos().getValue(), (float) (module2.getYPos().getValue() + 2.0 + (double) (Augustus.getInstance().getLoriousFontService().getComfortaa18().getHeight() / 2)), -1, true, clr, clr1);
                  Augustus.getInstance().getLoriousFontService().getComfortaa18().drawStringWithShadow(module2.getSuffix(), (double) (sr.getScaledWidth() - 5) - module2.getXPos().getValue() + Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(module2.getName()) + 2.0, (float) (module2.getYPos().getValue() + 2.0 + (double) (Augustus.getInstance().getLoriousFontService().getComfortaa18().getHeight() / 2)), new Color(200, 200, 200).getRGB(), false, clr, clr1);
                  y += Augustus.getInstance().getLoriousFontService().getComfortaa18().getHeight() + 6;
                  ++index;
               }

               break;
            }
            case "Vega": {
               byte b1 = 0;



               if (mm.arrayList.sideOption.getSelected().equals("Left") && mm.arrayList.vegaFontRenderer != null) {
                  switch (mm.hud.mode.getSelected()) {
                     case "Ryu": {
                        b1 = (byte) (mm.arrayList.vegaFontRenderer.getStringHeight("ALLAH") + 5F);
                        break;
                     }
                     case "Old": {
                        b1 = (byte) 48F;
                        break;
                     }
                     case "Other": {
                        b1 = (byte) ((double) ((float) mm.arrayList.vegaFontRenderer.getStringHeight("ALLAH") * 1.75F + 4.0F + 1.0F) * 1.5);
                        break;
                     }
                     case "Basic": {
                        b1 = (byte) (mm.arrayList.vegaFontRenderer.getStringHeight("ALLAH") + 1);
                        break;
                     }
                  }
               }
               byte b2;

               //ArrayList<Module> arrayList = (ArrayList<Module>) mm.getModules().stream().filter(Module::isToggled).sorted(Comparator.comparingDouble(paramModule -> -mm.arrayList.vegaFontRenderer.getStringWidth(paramModule.getDisplayName()))).collect(Collectors.toList());
               java.util.ArrayList<Module> arrayList = (java.util.ArrayList<Module>) mm.getModules().stream().filter(Module::isToggled).sorted(Comparator.comparingDouble(paramModule -> -mm.arrayList.vegaFontRenderer.getStringWidth(paramModule.getName()))).collect(Collectors.toList());
               for (b2 = 0; b2 < arrayList.size(); b2++) {
                  Module module1 = arrayList.get(b2);
                  Module module2 = arrayList.get(b2 + ((b2 == arrayList.size() - 1) ? 0 : 1));
                  //int i = Augustus.getInstance().getColorUtil().getSaturationFadeColor(color, (int)(b1 * this.saturationDelay), 100.0F).getRGB();
                  int i = ColorUtil.getColor(mm.arrayList.vegaColor1.getColor(), mm.arrayList.vegaColor2.getColor(), 1000, 100 * b2);
                  Gui.drawRect(sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - 5, b1 * 13, sr.getScaledWidth(), b1 * 13 + 13, (new Color(0.0F, 0.0F, 0.0F, 4.0F / 10.0F)).getRGB());//
                  //BlurUtil.blur(sr.getScaledWidth() - (int)mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - 5, b1 * 13, sr.getScaledWidth(), b1 * 13 + 13);
                  Gui.drawRect((sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - 6), (mm.arrayList.underline.getBoolean()) ? (b1 * 13 + 1) : (b1 * 13), (sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - 5), b1 * 13 + 13, i);
                  Gui.drawRect(sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - 6, b1 * 13 + 13, (module2 != module1) ? (sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module2.getName()) - 5) : sr.getScaledWidth(), b1 * 13 + 14, i);
                  mm.arrayList.vegaFontRenderer.drawString(module1.getName(), sr.getScaledWidth() - mm.arrayList.vegaFontRenderer.getStringWidth(module1.getName()) - (1), (1 + b1 * 13), i);
                  b1++;
               }
               break;
            }
         }
      }
       if (mm.hud.isToggled()) {
         if (mm.hud.targethud.getBoolean()) {
            drawTargetHud();
         }
         hud();
      } else {
         yAdd = 0.0F;
      }

      if (mm.hud.isToggled() && mm.hud.armor.getBoolean()) {
         drawArmorHud();
      }
   }

   @Override
   public void updateTick() {
      super.updateTick();
      if (mm.arrayList.isToggled()) {
      }
   }

   private void hud() {
      ScaledResolution sr = new ScaledResolution(mc);
      String var2 = mm.hud.mode.getSelected();
      switch (var2) {
         case "AugX": {
            switch (mm.hud.side.getSelected()) {
               case "Left": {
                  net.augustus.utils.skid.tomorrow.RenderUtil.drawCustomImageAlpha(5, 5, 30, 40,ResourceUtil.loadResourceLocation("resources/pictures/logo.png", "AugustusX_Logo"), -1, 255);
                  break;
               }
               case "Right": {
                  net.augustus.utils.skid.tomorrow.RenderUtil.drawCustomImageAlpha((int) (sr.getScaledWidth() - 5.0F), 5, 30, 40,ResourceUtil.loadResourceLocation("resources/pictures/logo.png", "AugustusX_Logo"), -1, 255);
                  break;
               }
            }
            break;
         }
         case "CSGO": {
            switch (mm.hud.side.getSelected()) {
               case "Left": {
                  Augustus.getInstance().getLoriousFontService().getArialbold24().drawString("A", 5D, 5D, Color.BLACK.getRGB(), true, Color.BLUE, Color.GREEN);
                  //oldFontRenderer.drawString("ENZA", 5.0F + oldFontRenderer.getStringWidth("A"), 5.0F, -1);
                  break;
               }
               case "Right":
                  oldFontRenderer.drawString("A", sr.getScaledWidth() - 5.0F - oldFontRenderer.getStringWidth("AugustusX"), 5.0F, getChroma().getRGB());
                  oldFontRenderer.drawString("ENZA", sr.getScaledWidth() - 5.0F - oldFontRenderer.getStringWidth("ugustusX"), 5.0F, -1);
                  break;
            }
            break;
         }
         case "Old": {
            String var11 = mm.hud.side.getSelected();
            switch (var11) {
               case "Left": {
                  oldFontRenderer.drawString("A", 5.0F, 5.0F, getChroma().getRGB());
                  oldFontRenderer.drawString("ugustusX", 5.0F + oldFontRenderer.getStringWidth("A"), 5.0F, -1);
                  break;
               }
               case "Right":
                  oldFontRenderer.drawString("A", sr.getScaledWidth() - 5.0F - oldFontRenderer.getStringWidth("AugustusX"), 5.0F, getChroma().getRGB());
                  oldFontRenderer.drawString("ugustusX", sr.getScaledWidth() - 5.0F - oldFontRenderer.getStringWidth("ugustusX"), 5.0F, -1);
                  break;
            }
            break;
         }
         case "Ryu": {

            String name = "Ryu  ";
            float width = (float) getFontRenderer().getStringWidth(name);
            yAdd = (float) (getFontRenderer().FONT_HEIGHT + 1);
            String var11 = mm.hud.side.getSelected();
            switch (var11) {
               case "Left":
                  width = (float) ryuFontRenderer.getStringWidth(name);
                  ryuFontRenderer.drawStringWithShadow(name, 1.0F, 1.0F, mm.hud.color.getColor().getRGB());
                  ryuFontRenderer.drawStringWithShadow("# ", width, 1.0F, new Color(162, 162, 162, 200).getRGB());
                  //System.out.println(getFontRenderer().FONT_HEIGHT);
                  ryuFontRenderer.drawStringWithShadow(Minecraft.getDebugFPS() + "fps", width + ryuFontRenderer.getStringWidth("# "), 1.0F, Color.white.getRGB());
                  break;
               case "Right":
                  PlayerUtil.sendChat("Ryu == Left");
                  mm.hud.side.setString("Left");
            }
            break;
         }
         case "Other":
            GL11.glPushMatrix();
            GL11.glScaled(1.5, 1.5, 0.0);
            yAdd = (float) ((int) ((double) ((float) getFontRenderer().FONT_HEIGHT * 1.75F + 4.0F + 1.0F) * 1.5));
            String var13 = mm.hud.side.getSelected();
            switch (var13) {
               case "Left":
                  if (mm.hud.backGround.getBoolean()) {
                     drawRect(
                             0,
                             0,
                             (int) ((float) getFontRenderer().getStringWidth("A") * 1.75F + (float) getFontRenderer().getStringWidth("ugustusX") + 4.0F),
                             (int) ((float) getFontRenderer().FONT_HEIGHT * 1.75F + 3.0F),
                             mm.hud.backGroundColor.getColor().getRGB()
                     );
                  }

                  GL11.glPushMatrix();
                  GL11.glScaled(1.75, 1.75, 0.0);
                  getFontRenderer().drawString("A", 1.0F, 1.0F, mm.hud.color.getColor().getRGB(), true, 0.5F);
                  GL11.glScaled(1.0, 1.0, 0.0);
                  GL11.glPopMatrix();
                  getFontRenderer()
                          .drawString(
                                  "ugustusX",
                                  (float) getFontRenderer().getStringWidth("A") * 1.75F + 2.0F,
                                  (float) (2 + getFontRenderer().FONT_HEIGHT) - 4.0F,
                                  mm.hud.color.getColor().getRGB(),
                                  true,
                                  0.5F
                          );
                  break;
               case "Right":
                  if (mm.hud.backGround.getBoolean()) {
                     drawRect(
                             (int) (
                                     (float) sr.getScaledWidth() / 1.5F
                                             - (float) (
                                             (int) (
                                                     (float) getFontRenderer().getStringWidth("A") * 1.75F + (float) getFontRenderer().getStringWidth("ugustusX") + 4.0F
                                             )
                                     )
                             ),
                             0,
                             (int) ((float) sr.getScaledWidth() / 1.5F) + 1,
                             (int) ((float) getFontRenderer().FONT_HEIGHT * 1.75F + 3.0F),
                             mm.hud.backGroundColor.getColor().getRGB()
                     );
                  }

                  GL11.glPushMatrix();
                  GL11.glScaled(1.75, 1.75, 0.0);
                  getFontRenderer()
                          .drawString(
                                  "A",
                                  (float) sr.getScaledWidth() / 1.5F / 1.75F
                                          - (float) getFontRenderer().getStringWidth("A")
                                          - (float) getFontRenderer().getStringWidth("ugustusX") / 1.75F
                                          - 0.25F,
                                  1.0F,
                                  mm.hud.color.getColor().getRGB(),
                                  true,
                                  0.5F
                          );
                  GL11.glScaled(1.0, 1.0, 0.0);
                  GL11.glPopMatrix();
                  getFontRenderer()
                          .drawString(
                                  "ugustusX",
                                  (float) sr.getScaledWidth() / 1.5F
                                          - 1.75F
                                          - (float) getFontRenderer().getStringWidth("A") * 1.5F * 1.75F
                                          - (float) getFontRenderer().getStringWidth("ugustusX") / 1.5F
                                          + 4.0F,
                                  (float) (2 + getFontRenderer().FONT_HEIGHT) - 4.0F,
                                  mm.hud.color.getColor().getRGB(),
                                  true,
                                  0.5F
                          );
            }

            GL11.glScaled(1.0, 1.0, 0.0);
            GL11.glPopMatrix();
            break;
         case "Basic":
            GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
            Date time = new Date();
            cal.setTime(time);
            int h = mm.protector.isToggled() && mm.protector.protectTime.getBoolean() ? mm.protector.getRandomHour() : cal.get(10);
            String m = cal.get(12) <= 9 ? "0" + cal.get(12) : String.valueOf(cal.get(12));
            m = mm.protector.isToggled() && mm.protector.protectTime.getBoolean() ? String.valueOf(mm.protector.getRandomMinute()) : m;
            String printTime;
            if (cal.get(9) == 0) {
               printTime = h < 9 ? " (0" + h + ":" + m + " AM)" : " (" + h + ":" + m + " AM)";
            } else {
               printTime = h < 9 ? " (0" + h + ":" + m + " PM)" : " (" + h + ":" + m + " PM)";
            }

            String name = Augustus.getInstance().getName() + " | " + "b" + Augustus.getInstance().getVersion() + " | " + Augustus.getInstance().getRelease();
            float width = (float) getFontRenderer().getStringWidth(name);
            yAdd = (float) (getFontRenderer().FONT_HEIGHT + 1);
            String var11 = mm.hud.side.getSelected();
            switch (var11) {
               case "Left":
                  if (mm.hud.backGround.getBoolean()) {
                     RenderUtil.drawFloatRect(
                             0.0F,
                             0.0F,
                             (float) (getFontRenderer().getStringWidth(printTime) + 3) + width,
                             (float) (getFontRenderer().FONT_HEIGHT + 1),
                             mm.hud.backGroundColor.getColor().getRGB()
                     );
                  }

                  getFontRenderer().drawString(name, 1.0F, 1.0F, mm.hud.color.getColor().getRGB(), true, 0.5F);
                  getFontRenderer().drawString(printTime, width, 1.0F, new Color(182, 186, 189).getRGB(), true, 0.5F);
                  break;
               case "Right":
                  if (mm.hud.backGround.getBoolean()) {
                     RenderUtil.drawFloatRect(
                             (float) (sr.getScaledWidth() - getFontRenderer().getStringWidth(printTime) - 3) - width,
                             0.0F,
                             (float) sr.getScaledWidth(),
                             (float) (getFontRenderer().FONT_HEIGHT + 1),
                             mm.hud.backGroundColor.getColor().getRGB()
                     );
                  }


                  getFontRenderer()
                          .drawString(
                                  name,
                                  (float) sr.getScaledWidth() - width - (float) getFontRenderer().getStringWidth(printTime),
                                  1.0F,
                                  mm.hud.color.getColor().getRGB(),
                                  true,
                                  0.5F
                          );
                  getFontRenderer()
                          .drawString(
                                  printTime,
                                  (float) (sr.getScaledWidth() - getFontRenderer().getStringWidth(printTime) - 1),
                                  1.0F,
                                  new Color(182, 186, 189).getRGB(),
                                  true,
                                  0.5F
                          );
               default:
                  break;
            }
         default:
            yAdd = 0.0F;
      }

      if (!mm.arrayList.sideOption.getSelected().equals(mm.hud.side.getSelected())) {
         yAdd = 0.0F;
      }
   }

   private static float animated;
   public static Color color = new Color(0, 0, 0, 100);


   public void drawTargetHud() {
      if (target instanceof EntityPlayer) {
         int x = (int) mm.hud.targethud_x.getValue();
         int y = (int) mm.hud.targethud_y.getValue();
         switch (mm.hud.targetMode.getSelected()) {
            case "Exhibition": {
               CustomFontUtil font = FontUtil.tahomabold18;
               float minWidth = Math.max(126F, 47F + font.getStringWidth(target.getName()));

               EXRenderUtils.drawExhiRect(x, y, minWidth + x  , 45F + y, 1F);

               EXRenderUtils.drawRect(x +2.5F, y + 2.5F, 42.5F + x , 42.5F + y, new Color(59, 59, 59).getRGB());
               EXRenderUtils.drawRect(x +3F, y + 3F, 42F + x , 42F + y, new Color(19, 19, 19).getRGB());

               GL11.glColor4f(1f, 1f, 1f, 1f);
               EXRenderUtils.drawEntityOnScreen(x + 22, y + 40, 16, target);

               font.drawString(target.getName(), x + 44.5, y + 2.5, new Color(-1).getRGB());

               float barLength = 70F * Math.min(Math.max(target.getHealth() / target.getMaxHealth(), 0F), 1F);
               EXRenderUtils.drawRect(x + 45F, y + 14F,  45F + 70F + x ,  18F + y, new Color(BlendUtils.getHealthColor(target.getHealth(), target.getMaxHealth()).darker().getRGB()));
               EXRenderUtils.drawRect(x + 45F, y + 14F,  45F + barLength + x ,  18F + y, new Color(BlendUtils.getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB()));

               for (int i = 0; i <= 9; i++) {
                  EXRenderUtils.drawRectBasedBorder(x + 45F + i * 7F, y + 14F,  45F + x  + (i + 1) * 7F ,  18F + y , 0.5F, Color.black.getRGB());
               }

               FontUtil.tahoma12.drawString("HP:" + (int)target.getHealth() + " | Dist:" + (int)mc.thePlayer.getDistanceToEntity(target), x + 45F,y +  19F, new Color(-1).getRGB());

               GlStateManager.resetColor();
               GL11.glPushMatrix();
               GL11.glColor4f(1f, 1f, 1f, 1f);
               GlStateManager.enableRescaleNormal();
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

               RenderItem renderItem = mc.getRenderItem();
               int x1 = x + 45;
               int y1 = y + 28;

               for (int index = 3; index >= 0; index--) {
                  ItemStack stack = target.getInventory()[index];
                  if (stack != null && stack.getItem() != null) {
                     renderItem.renderItemIntoGUI(stack, x1, y1);
                     renderItem.renderItemOverlays(mc.fontRendererObj, stack, x1, y1);
                     EXRenderUtils.drawExhiEnchants(stack, (float)x1, (float)y1);
                  }
                  x1 += 16;
               }

               ItemStack mainStack = target.getHeldItem();
               if (mainStack != null && mainStack.getItem() != null) {
                  renderItem.renderItemIntoGUI(mainStack, x1, y1);
                  renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x1, y1);
                  EXRenderUtils.drawExhiEnchants(mainStack, (float)x1, (float)y1);
               }

               net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
               GlStateManager.disableRescaleNormal();
               GlStateManager.enableAlpha();
               GlStateManager.disableBlend();
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GL11.glPopMatrix();
               break;
            }
            case "AugustusX": {
               Color color1 = mm.hud.color1.getColor();
               Color healthColor = getHealthColor(target.getHealth(), target.getMaxHealth());
               String playerName;
               float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

               float f1 = 133.0F / Minecraft.getDebugFPS() * 1.05F;
               float f2 = 140.0F / target.getMaxHealth() * Math.min(target.getHealth(), target.getMaxHealth());

               if (f2 < animated || f2 > animated)
                  if (Math.abs(f2 - animated) <= f1) {
                     animated = f2;
                  } else {
                     animated += (animated < f2) ? (f1 * 3.0F) : -f1;
                  }

               playerName = "Name: " + StringUtils.stripControlCodes(target.getName());
               GuiUtils.drawRect1(x, y, 160.0D, 60.0D, color.getRGB());
               //renderTargetPlayer2D(x, y - 10, 31, 31, (AbstractClientPlayer) target);
               net.augustus.utils.skid.tenacity.RenderUtil.drawBorderedRect(x, y, 160, 60, .75f, color.getRGB(), new Color(114, 122, 114).getRGB());

               drawHead(((AbstractClientPlayer) target).getLocationSkin(), x + 7, y + 7, 40, 40);
               FontUtil.tenacity_blod16.drawString(playerName, (int) (x + 55.0F), (int) (y + 4.0F), -1);
               float health = (target.getHealth() / target.getMaxHealth()) * 100;
               FontUtil.tenacity_blod16.drawString("Health: " + health + "%", (int) (x + 55.0F), (int) (y + 14.0F), -1);
               drawArmor((EntityPlayer) target, (int) (x + 130.0F), (int) (y + 24.0F));
               drawHeld((EntityPlayer) target, (int) (x + 130.0F), (int) (y + 24.0F));
               RoundedUtil.drawRound(x + 8, y + 53, 138, 3, 1.5f, new Color(26, 26, 26));
               RoundedUtil.drawGradientHorizontal(x + 8, y + 53, animated, 3, 1.5f, healthColor, color1);
               break;
            }
            case "AugustusX_Round": {
               Color color1 = mm.hud.color1.getColor();
               Color healthColor = getHealthColor(target.getHealth(), target.getMaxHealth());
               float radius = (float) mm.hud.radius.getValue();
               boolean blur = mm.hud.blur.getBoolean();
               float f1 = 133.0F / Minecraft.getDebugFPS() * 1.05F;
               float f2 = 140.0F / target.getMaxHealth() * Math.min(target.getHealth(), target.getMaxHealth());

               if (f2 < animated || f2 > animated)
                  if (Math.abs(f2 - animated) <= f1) {
                     animated = f2;
                  } else {
                     animated += (animated < f2) ? (f1 * 3.0F) : -f1;
                  }

               String playerName = "Name: " + StringUtils.stripControlCodes(target.getName());
               RoundedUtil.drawRound(x, y, 185, 40, radius, blur, color);
               RoundedUtil.drawRound(x, y, 185, 40, radius, blur,color);
               drawHead(((AbstractClientPlayer) target).getLocationSkin(), x + 4, y + 4, 31, 31);
               FontUtil.tenacity_blod16.drawString(playerName, (int) (x + 40.0F), (int) (y + 6.0F), -1);
               float health = (target.getHealth() / target.getMaxHealth()) * 100;
               FontUtil.tenacity_blod16.drawString("Health: " + health + "%", (int) (x + 40.0F), (int) (y + 16.0F), -1);
               RoundedUtil.drawRound(x + 40, y + 30, 138, 3, 1.5f, new Color(26, 26, 26));
               RoundedUtil.drawGradientHorizontal(x + 40, y + 30, animated, 3, 1.5f, healthColor, color1);
               break;
            }
            case "Basic": {
               Color healthColor;
               String playerName;
               int distance;
               float f1;
               float f2;
               EntityPlayer currentTarget = (EntityPlayer) target;
               healthColor = new Color(getHealthColor(currentTarget));
               playerName = "Name: " + StringUtils.stripControlCodes(currentTarget.getName());
               distance = (int) mc.thePlayer.getDistanceToEntity(currentTarget);
               f1 = 133.0F / Minecraft.getDebugFPS() * 1.05F;
               f2 = 140.0F / currentTarget.getMaxHealth() * Math.min(currentTarget.getHealth(), currentTarget.getMaxHealth());
               GuiUtils.drawRect1((x - 1.0F), (y - 1.0F), 142.0D, 44.0D, (new Color(0, 0, 0, 100)).getRGB());
               GuiUtils.drawRect1(x, y, 140.0D, 40.0D, (new Color(0, 0, 0, 75)).getRGB());
               GuiUtils.drawRect1(x, (y + 40.0F), 140.0D, 2.0D, (new Color(0, 0, 0)).getRGB());
               mc.fontRendererObj.drawString(playerName, (int) (x + 25.0F), (int) (y + 4.0F), -1);
               mc.fontRendererObj.drawString("Distance: " + distance + "m", (int) (x + 25.0F), (int) (y + 15.0F), -1);
               mc.fontRendererObj.drawString("Armor: " + Math.round(currentTarget.getTotalArmorValue()), (int) (x + 25.0F), (int) (y + 25.0F), -1);
               if (mc.currentScreen == null)
                  GuiInventory.drawEntityOnScreen(x + 12, y + 31, 13, currentTarget.rotationYaw, -currentTarget.rotationPitch, currentTarget);
               if (f2 < animated || f2 > animated)
                  if (Math.abs(f2 - animated) <= f1) {
                     animated = f2;
                  } else {
                     animated += (animated < f2) ? (f1 * 3.0F) : -f1;
                  }
               GuiUtils.drawRect1(x, (y + 40.0F), animated, 2.0D, healthColor.getRGB());
               break;
            }
         }
      }
   }

   public static int getHealthColor(EntityLivingBase entityLivingBase) {
      float percentage = 100.0F * entityLivingBase.getHealth() / 2.0F / entityLivingBase.getMaxHealth() / 2.0F;
      return (percentage > 75.0F) ? 1703705 : ((percentage > 50.0F) ? 16776960 : ((percentage > 25.0F) ? 16733440 : 16713984));
   }

   public static void drawCustomHotBar() {
      ScaledResolution sr = new ScaledResolution(mc);
      FontRenderer fr = mc.fontRendererObj;
      int i = mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()) != null
              ? mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime()
              : 0;
      int x = mm.protector.isToggled() ? (int) mc.thePlayer.posX + mm.protector.getRandomX() : (int) mc.thePlayer.posX;
      int y = (int) mc.thePlayer.posY;
      int z = mm.protector.isToggled() ? (int) mc.thePlayer.posZ + mm.protector.getRandomZ() : (int) mc.thePlayer.posZ;
      fr.drawString(
              "§fFPS: §7" + Minecraft.getDebugFPS() + "   §fPing: §7" + i,
              (int) (4.0F + FontUtil.espHotbar.getStringWidth("A")),
              sr.getScaledHeight() - 20,
              Color.white.getRGB()
      );
      fr.drawString(
              "§fX: §7" + x + "  §fY: §7" + y + "  §fZ: §7" + z,
              (int) (4.0F + FontUtil.espHotbar.getStringWidth("A")),
              sr.getScaledHeight() - 9,
              Color.white.getRGB()
      );
      FontUtil.espHotbar.drawStringWithShadow("A", 1.5, sr.getScaledHeight() - 19, Color.white.getRGB());
      if (!mm.protector.isToggled() || !mm.protector.protectTime.getBoolean()) {
         GregorianCalendar now = new GregorianCalendar();
         DateFormat df = DateFormat.getDateInstance(2);
         fr.drawString(
                 df.format(now.getTime()),
                 (float) (sr.getScaledWidth() - 10 - fr.getStringWidth(df.format(now.getTime()))),
                 (float) (sr.getScaledHeight() - 1 - fr.FONT_HEIGHT),
                 new Color(255, 255, 255, 221).getRGB(),
                 true,
                 0.5F
         );
         DateFormat df2 = DateFormat.getTimeInstance(3);
         fr.drawString(
                 df2.format(now.getTime()),
                 (float) (
                         (double) (sr.getScaledWidth() - 10)
                                 - (double) fr.getStringWidth(df2.format(now.getTime())) * 0.5
                                 - (double) fr.getStringWidth(df.format(now.getTime())) * 0.5
                 ),
                 (float) (sr.getScaledHeight() - 11 - fr.FONT_HEIGHT),
                 new Color(255, 255, 255, 221).getRGB(),
                 true,
                 0.5F
         );
      }
   }

   public static void drawCustomCrossHair() {
      ScaledResolution sr = new ScaledResolution(mc);
      double screenWidth = sr.getScaledWidth_double();
      double screenHeight = sr.getScaledHeight_double();
      Vec2d middle = new Vec2d(screenWidth / 2.0, screenHeight / 2.0);
      double crossHairWidth = mm.crossHair.width.getValue() / 2.0;
      double crossHairLength = mm.crossHair.length.getValue();
      double crossHairMargin = mm.crossHair.margin.getValue();
      int color = mm.crossHair.color.getColor().getRGB();
      if (mm.crossHair.rainbow.getBoolean()) {
         mm.crossHair
                 .rainbowUtil
                 .updateRainbow(
                         mm.crossHair.rainbowSpeed.getValue() == 1000.0
                                 ? (float) (mm.crossHair.rainbowSpeed.getValue() * 1.0E-5F)
                                 : (float) (mm.crossHair.rainbowSpeed.getValue() * 1.0E-6F),
                         255
                 );
         color = mm.crossHair.rainbowUtil.getColor().getRGB();
      }

      RenderUtil.drawRect(middle.x - crossHairWidth, middle.y + crossHairMargin + crossHairLength, middle.x + crossHairWidth, middle.y + crossHairMargin, color);
      if (!mm.crossHair.tStyle.getBoolean()) {
         RenderUtil.drawRect(
                 middle.x - crossHairWidth, middle.y - crossHairMargin - crossHairLength, middle.x + crossHairWidth, middle.y - crossHairMargin, color
         );
      }

      RenderUtil.drawRect(middle.x + crossHairMargin, middle.y + crossHairWidth, middle.x + crossHairMargin + crossHairLength, middle.y - crossHairWidth, color);
      RenderUtil.drawRect(middle.x - crossHairMargin - crossHairLength, middle.y + crossHairWidth, middle.x - crossHairMargin, middle.y - crossHairWidth, color);
      if (mm.crossHair.dot.getBoolean()) {
         RenderUtil.drawRect(middle.x - crossHairWidth, middle.y + crossHairWidth, middle.x + crossHairWidth, middle.y - crossHairWidth, color);
      }
   }

   private void drawArmorHud() {
      ScaledResolution sr = new ScaledResolution(mc);
      int height = sr.getScaledHeight() - 56;
      int width = sr.getScaledWidth() / 2 + 91;
      mc.getRenderItem().zLevel = -100.0F;
      if (mc.thePlayer.getCurrentArmor(0) != null) {
         mc.getRenderItem().renderItemIntoGUI(mc.thePlayer.getCurrentArmor(0), width - 81, height);
      }

      if (mc.thePlayer.getCurrentArmor(1) != null) {
         mc.getRenderItem().renderItemIntoGUI(mc.thePlayer.getCurrentArmor(1), width - 61, height);
      }

      if (mc.thePlayer.getCurrentArmor(2) != null) {
         mc.getRenderItem().renderItemIntoGUI(mc.thePlayer.getCurrentArmor(2), width - 41, height);
      }

      if (mc.thePlayer.getCurrentArmor(3) != null) {
         mc.getRenderItem().renderItemIntoGUI(mc.thePlayer.getCurrentArmor(3), width - 21, height);
      }
   }

   public static void drawArmor(EntityPlayer entity, int x, int y) {
      mc.getRenderItem().zLevel = -100.0F;
      if (entity.getCurrentArmor(0) != null) {
         mc.getRenderItem().renderItemIntoGUI(entity.getCurrentArmor(0), x, y);
      }

      if (entity.getCurrentArmor(1) != null) {
         mc.getRenderItem().renderItemIntoGUI(entity.getCurrentArmor(1),  x - 20, y);
      }

      if (entity.getCurrentArmor(2) != null) {
         mc.getRenderItem().renderItemIntoGUI(entity.getCurrentArmor(2), x - 40, y);
      }

      if (entity.getCurrentArmor(3) != null) {
         mc.getRenderItem().renderItemIntoGUI(entity.getCurrentArmor(3), x - 60, y);
      }
   }

   public static void drawHeld(EntityPlayer entityPlayer, int x, int y) {
      mc.getRenderItem().zLevel = -100.0F;
      if (entityPlayer.getHeldItem() != null && entityPlayer.getHeldItem().getItem() != null) {
         mc.getRenderItem().renderItemIntoGUI(entityPlayer.getHeldItem(), x - 80, y);
      }
   }

   public static boolean damaged = false;

   public static void drawHead(ResourceLocation skin, int x, int y, int width, int height) {
      try {
         mc.getTextureManager().bindTexture(skin);
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glColor4f(1,1,1,1);
         Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
         GL11.glDisable(GL11.GL_BLEND);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
      try {
         mc.getTextureManager().bindTexture(image);
         GL11.glEnable(GL11.GL_BLEND);
         Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
         GL11.glDisable(GL11.GL_BLEND);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static Color getHealthColor(float health, float maxHealth) {
      if (health != 0 && health > 0) {
         float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
         Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
         float progress = health / maxHealth;
         return blendColors(fractions, colors, progress).brighter();
      } else {
         return new Color(0,255,0);
      }
   }
   public static Color blendColors(float[] fractions, Color[] colors, float progress) {
      if (fractions.length == colors.length) {
         int[] indices = getFractionIndices(fractions, progress);
         float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
         Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
         float max = range[1] - range[0];
         float value = progress - range[0];
         float weight = value / max;
         Color color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
         return color;
      } else {
         throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
      }
   }

   public static int[] getFractionIndices(float[] fractions, float progress) {
      int[] range = new int[2];

      int startPoint;
      for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
      }

      if (startPoint >= fractions.length) {
         startPoint = fractions.length - 1;
      }

      range[0] = startPoint - 1;
      range[1] = startPoint;
      return range;
   }

   public static Color blend(Color color1, Color color2, double ratio) {
      float r = (float)ratio;
      float ir = 1.0F - r;
      float[] rgb1 = color1.getColorComponents(new float[3]);
      float[] rgb2 = color2.getColorComponents(new float[3]);
      float red = rgb1[0] * r + rgb2[0] * ir;
      float green = rgb1[1] * r + rgb2[1] * ir;
      float blue = rgb1[2] * r + rgb2[2] * ir;
      if (red < 0.0F) {
         red = 0.0F;
      } else if (red > 255.0F) {
         red = 255.0F;
      }

      if (green < 0.0F) {
         green = 0.0F;
      } else if (green > 255.0F) {
         green = 255.0F;
      }

      if (blue < 0.0F) {
         blue = 0.0F;
      } else if (blue > 255.0F) {
         blue = 255.0F;
      }

      Color color3 = null;

      try {
         color3 = new Color(red, green, blue);
      } catch (IllegalArgumentException ignored) {
      }

      return color3;
   }

}
