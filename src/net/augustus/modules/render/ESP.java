package net.augustus.modules.render;

import java.awt.Color;

import lombok.Getter;
import net.augustus.events.EventRender2D;
import net.augustus.events.EventRender3D;
import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.AntiBot;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.BooleansSetting;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.Setting;
import net.augustus.utils.RainbowUtil;
import net.augustus.utils.RenderUtil;
import net.augustus.utils.skid.idek.Esp2DUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
   public final java.util.ArrayList<EntityLivingBase> livingBases = new java.util.ArrayList<>();
   @Getter
   private final RainbowUtil rainbowUtil = new RainbowUtil();
   //public StringValue mode = new StringValue(8343, "Mode", this, "Box", new String[]{"Box", "FakeCorner", "Fake2D", "Vanilla", "Other2D", "Hase", "HealthLine"});
   public BooleanValue box = new BooleanValue(12, "Box", this, false);
   public BooleanValue esp2D = new BooleanValue(14879, "ESP2D", this, false);
   public BooleanValue fakeC = new BooleanValue(312, "FakeCorner", this, false);
   public BooleanValue f2D = new BooleanValue(122, "Fake2D", this, false);
   public BooleanValue vanilla = new BooleanValue(12218, "Vanilla", this, false);
   public BooleanValue other2D = new BooleanValue(1225, "Other2D", this, false);
   public BooleanValue hase = new BooleanValue(1192, "Hase", this, false);
   public BooleanValue healthLine = new BooleanValue(1234, "HealthLine", this, false);
   public BooleansSetting mode = new BooleansSetting(12488, "Mode", this, new Setting[]{this.box, this.esp2D, this.fakeC, this.f2D, this.vanilla, this.other2D, this.hase, this.healthLine});
   public ColorSetting color = new ColorSetting(9617, "Color", this, new Color(21, 121, 230, 65));
   public ColorSetting outlineColor = new ColorSetting(4135, "Color", this, new Color(21, 121, 230, 65));
   public BooleanValue rainbow = new BooleanValue(11208, "Rainbow", this, false);
   public DoubleValue rainbowSpeed = new DoubleValue(2157, "RainbowSpeed", this, 55.0, 0.0, 1000.0, 0);
   public DoubleValue rainbowAlpha = new DoubleValue(7473, "RainbowAlpha", this, 80.0, 0.0, 255.0, 0);
   public BooleanValue teamColor = new BooleanValue(13817, "TeamColor", this, true);

   public BooleanValue otherColorOnHit = new BooleanValue(5259, "HitColor", this, true);
   public ColorSetting hitColor = new ColorSetting(14104, "HitColor", this, new Color(255, 0, 0));
   public BooleanValue player = new BooleanValue(1039, "Player", this, true);
   public BooleanValue mob = new BooleanValue(14550, "Mob", this, true);
   public BooleanValue animal = new BooleanValue(9650, "Animal", this, false);
   public BooleanValue invisible = new BooleanValue(11541, "Invisible", this, false);
   public DoubleValue lineWidth = new DoubleValue(9781, "LineWidth", this, 6.0, 0.0, 15.0, 0);
   public BooleansSetting entities = new BooleansSetting(1678, "Entities", this, new Setting[]{this.player, this.mob, this.animal, this.invisible});

   public ESP() {
      super("ESP", new Color(47, 134, 124, 255), Categorys.RENDER);
   }

   @EventTarget
   public void onEventTick(EventTick eventTick) {
      if (mc.theWorld != null) {
         this.livingBases.clear();
         if (!this.vanilla.getBoolean()) {
            for (Object object : mc.theWorld.loadedEntityList) {
               if (object instanceof EntityLivingBase) {
                  EntityLivingBase entity = (EntityLivingBase) object;
                  if (entity.isInvisible()) {
                     if (this.invisible.getBoolean()) {
                        if (entity instanceof EntityPlayer && this.player.getBoolean() && mc.thePlayer != entity) {
                           if (mm.antiBot.isToggled()) {
                              if (!AntiBot.isServerBot((EntityPlayer) entity)) {
                                 this.livingBases.add(entity);
                              }
                           } else {
                              this.livingBases.add(entity);
                           }
                        }

                        if (entity instanceof EntityMob && this.mob.getBoolean()) {
                           this.livingBases.add(entity);
                        }

                        if (entity instanceof EntityAnimal && this.animal.getBoolean()) {
                           this.livingBases.add(entity);
                        }
                     }
                  } else {
                     if (entity instanceof EntityPlayer && this.player.getBoolean() && mc.thePlayer != entity) {
                        if (mm.antiBot.isToggled()) {
                           if (!AntiBot.isServerBot((EntityPlayer) entity)) {
                              this.livingBases.add(entity);
                           }
                        } else {
                           this.livingBases.add(entity);
                        }
                     }

                     if (entity instanceof EntityMob && this.mob.getBoolean()) {
                        this.livingBases.add(entity);
                     }

                     if (entity instanceof EntityAnimal && this.animal.getBoolean()) {
                        this.livingBases.add(entity);
                     }
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void onEventRender3D(EventRender3D eventRender3D) {
      if (this.rainbow.getBoolean()) {
         this.rainbowUtil
                 .updateRainbow(
                         this.rainbowSpeed.getValue() == 1000.0 ? (float) (this.rainbowSpeed.getValue() * 1.0E-5F) : (float) (this.rainbowSpeed.getValue() * 1.0E-6F),
                         (int) this.rainbowAlpha.getValue()
                 );
      }

      if (!this.livingBases.isEmpty()) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glDisable(2929);
         GL11.glDisable(3553);
         GlStateManager.disableCull();
         GL11.glDepthMask(false);
         float red = this.rainbow.getBoolean() ? (float) this.rainbowUtil.getColor().getRed() / 255.0F : (float) this.color.getColor().getRed() / 255.0F;
         float green = this.rainbow.getBoolean() ? (float) this.rainbowUtil.getColor().getGreen() / 255.0F : (float) this.color.getColor().getGreen() / 255.0F;
         float blue = this.rainbow.getBoolean() ? (float) this.rainbowUtil.getColor().getBlue() / 255.0F : (float) this.color.getColor().getBlue() / 255.0F;
         float alpha = this.rainbow.getBoolean() ? (float) this.rainbowUtil.getColor().getAlpha() / 255.0F : (float) this.color.getColor().getAlpha() / 255.0F;

         for (EntityLivingBase livingBase : this.livingBases) {
            this.render(livingBase, red, green, blue, alpha);
         }

         GL11.glDepthMask(true);
         GlStateManager.enableCull();
         GL11.glEnable(3553);
         GL11.glEnable(2929);
         GL11.glDisable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2848);
      }
   }

   private void render(EntityLivingBase entity, float red, float green, float blue, float alpha) {
      float lineWidth = (float) this.lineWidth.getValue() / 2.0F;
      int i = 16777215;
      if (this.teamColor.getBoolean() && entity instanceof EntityPlayer) {
         ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) entity.getTeam();
         if (scoreplayerteam != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
            if (s.length() >= 2) {
               i = mc.fontRendererObj.getColorCode(s.charAt(1));
            }
         }

         red = (float) (i >> 16 & 0xFF) / 255.0F;
         green = (float) (i >> 8 & 0xFF) / 255.0F;
         blue = (float) (i & 0xFF) / 255.0F;
      }

      if (entity.hurtTime > 0 && this.otherColorOnHit.getBoolean()) {
         red = (float) this.hitColor.getColor().getRed() / 255.0F;
         green = (float) this.hitColor.getColor().getGreen() / 255.0F;
         blue = (float) this.hitColor.getColor().getBlue() / 255.0F;
      }

      if (mc.thePlayer.getDistanceToEntity(entity) > 1.0F) {
         double d0 = (double) (1.0F - mc.thePlayer.getDistanceToEntity(entity) / 20.0F);
         if (d0 < 0.3) {
            d0 = 0.3;
         }

         lineWidth = (float) ((double) lineWidth * d0);
      }
      Color other2dcolor = new Color(red, green, blue, alpha);


      if (this.healthLine.getBoolean()) {
         float r = entity.getHealth() / entity.getMaxHealth();
         GL11.glPushMatrix();
         RenderManager renderManager = mc.getRenderManager();
         Timer timer = mc.timer;
         GL11.glTranslated(
                 entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
                 entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY - 0.2,
                 entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
         );
         GL11.glRotated((-mc.getRenderManager().playerViewY), 0.0, 1.0, 0.0);
         //RenderUtil.disableGlCap(GL11.GL_LIGHTING, GL11.GL_DEPTH_TEST);
         GL11.glScalef(0.03F, 0.03F, 0.03F);
         Gui.drawRect(21, -1, 26, 75, Color.black.getRGB());
         Gui.drawRect(22, (int) (74 * r), 25, 74, Color.darkGray.getRGB());
         Gui.drawRect(22, 0, 25, (int) (74 * r), getHealthColor(entity.getHealth(), entity.getMaxHealth()).getRGB());
         //RenderUtil.enableGlCap(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         RenderUtil.resetCaps();

         // Reset color
         GlStateManager.resetColor();
         GL11.glColor4f(1F, 1F, 1F, 1F);

         // Pop
         GL11.glPopMatrix();

      }
      if (this.hase.getBoolean()) {
         if (!entity.isDead && entity != mc.thePlayer && Esp2DUtil.isInViewFrustrum(entity)) {
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
            final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY);
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

            GL11.glPushMatrix();
            GL11.glTranslated(x, y - 0.2, z);
            GlStateManager.disableDepth();

            GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);

            final float width = 1.1f;
            final float height = 2.2f;

            //draw2DBox(width, height, lineWidth, 0.04f, new Color(0, 0, 0, 165));

            //draw2DBox(width, height, lineWidth, 0, tm.getTheme().getSecondary());
            Esp2DUtil.drawImage(new ResourceLocation("client   /IchHaseDich.png"), (int) width, (int) height, (int) (-width * 2), (int) -height);

            GlStateManager.enableDepth();
            GL11.glPopMatrix();
         }

      }
      if (this.box.getBoolean()) {
         RenderUtil.drawEntityESP(entity, red, green, blue, alpha, 1.0F, lineWidth);
      }
      if (this.fakeC.getBoolean()) {
         RenderUtil.drawCornerESP(entity, red, green, blue);
      }
      if (this.f2D.getBoolean()) {
         RenderUtil.drawFake2DESP(entity, red, green, blue);
      }
      if (this.other2D.getBoolean()) {
         double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
         double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
         double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
         GL11.glPushMatrix();
         GL11.glTranslated(x, y - 0.2D, z);
         GL11.glScalef(0.03F, 0.03F, 0.03F);
         GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
         GlStateManager.disableDepth();
         Gui.drawRect(-20, -1, -26, 75, Color.BLACK.hashCode());
         Gui.drawRect(-21, 0, -25, 74, other2dcolor.getRGB());
         Gui.drawRect(20, -1, 26, 75, Color.BLACK.hashCode());
         Gui.drawRect(21, 0, 25, 74, other2dcolor.getRGB());
         Gui.drawRect(-20, -1, 21, 5, Color.BLACK.hashCode());
         Gui.drawRect(-21, 0, 24, 4, other2dcolor.getRGB());
         Gui.drawRect(-20, 70, 21, 75, Color.BLACK.hashCode());
         Gui.drawRect(-21, 71, 25, 74, other2dcolor.getRGB());
         GlStateManager.enableDepth();
         GL11.glPopMatrix();
      }

   }

   public boolean canRender(EntityLivingBase entity) {
      if (entity.isInvisible()) {
         if (!this.invisible.getBoolean()) {
            return false;
         } else if (entity instanceof EntityPlayer && this.player.getBoolean() && mc.thePlayer != entity) {
            return true;
         } else if (entity instanceof EntityMob && this.mob.getBoolean()) {
            return true;
         } else {
            return entity instanceof EntityAnimal && this.animal.getBoolean();
         }
      } else if (entity instanceof EntityPlayer && this.player.getBoolean() && mc.thePlayer != entity) {
         return true;
      } else if (entity instanceof EntityMob && this.mob.getBoolean()) {
         return true;
      } else {
         return entity instanceof EntityAnimal && this.animal.getBoolean();
      }
   }

   public static Color getHealthColor(float health, float maxHealth) {
      float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
      Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
      float progress = health / maxHealth;
      return blendColors(fractions, colors, progress).brighter();
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
