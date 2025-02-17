package net.minecraft.client.renderer;

import net.augustus.events.EventItemRenderer;
import net.augustus.modules.render.BlockAnimation;
import net.augustus.utils.EventHandler;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.interfaces.MM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import optifine.Config;
import optifine.DynamicLights;
import optifine.Reflector;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class ItemRenderer implements MM {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft mc;
   private float delay = 0;
   private long lastUpdateTime = System.currentTimeMillis();
   private final TimeHelper rotateTimer = new TimeHelper();

   private ItemStack itemToRender;
   private float equippedProgress;
   private float prevEquippedProgress;
   private final RenderManager renderManager;
   private final RenderItem itemRenderer;
   private int equippedItemSlot = -1;
   private static final String __OBFID = "CL_00000953";
   public static boolean fakeBlocking = false;
   private int spinCounter = 0;

   public ItemRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.itemRenderer = mcIn.getRenderItem();
   }

   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
      if (mc.thePlayer == null || mc.theWorld == null) {
         return;
      }

      if (heldStack != null) {
         Item item = heldStack.getItem();
         Block block = Block.getBlockFromItem(item);
         GlStateManager.pushMatrix();
         if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
               GlStateManager.depthMask(false);
            }
         }

         this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
         if (this.isBlockTranslucent(block)) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   private boolean isBlockTranslucent(Block blockIn) {
      return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   private void func_178101_a(float angle, float p_178101_2_) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void func_178109_a(AbstractClientPlayer clientPlayer) {
      int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
      if (Config.isDynamicLights()) {
         i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
      }

      float f = (float)(i & 65535);
      float f1 = (float)(i >> 16);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
   }

   private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
      float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
      float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
      GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float func_178100_c(float p_178100_1_) {
      float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderRightArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.25F, -0.85F, 0.75F);
      renderPlayerIn.renderRightArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }
   private void avatar(float equipProgress, float swingProgress) {
      GlStateManager.translate(0.56F, -0.52F, -0.72F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
      float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
      GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }
   private void renderLeftArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.3F, -1.1F, 0.45F);
      renderPlayerIn.renderLeftArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      RenderPlayer renderplayer = (RenderPlayer)render;
      if (!clientPlayer.isInvisible()) {
         GlStateManager.disableCull();
         this.renderRightArm(renderplayer);
         this.renderLeftArm(renderplayer);
         GlStateManager.enableCull();
      }
   }

   private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
      float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI * 2.0F);
      float f2 = -0.2F * MathHelper.sin(p_178097_4_ * (float) Math.PI);
      GlStateManager.translate(f, f1, f2);
      float f3 = this.func_178100_c(p_178097_2_);
      GlStateManager.translate(0.0F, 0.04F, -0.72F);
      GlStateManager.translate(0.0F, p_178097_3_ * -1.2F, 0.0F);
      GlStateManager.translate(0.0F, f3 * -0.5F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3 * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderPlayerArms(clientPlayer);
      float f4 = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float) Math.PI);
      float f5 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
      GlStateManager.rotate(f4 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f5 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.38F, 0.38F, 0.38F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-1.0F, -1.0F, 0.0F);
      GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
      worldrenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
      worldrenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
      worldrenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
      tessellator.draw();
      MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
      if (mapdata != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
      }
   }

   private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
      float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
      float f1 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI * 2.0F);
      float f2 = -0.4F * MathHelper.sin(p_178095_3_ * (float) Math.PI);
      GlStateManager.translate(f, f1, f2);
      GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translate(0.0F, p_178095_2_ * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f3 = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float) Math.PI);
      float f4 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
      GlStateManager.rotate(f4 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      GlStateManager.translate(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.translate(5.6F, 0.0F, 0.0F);
      Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      GlStateManager.disableCull();
      RenderPlayer renderplayer = (RenderPlayer)render;
      renderplayer.renderRightArm(this.mc.thePlayer);
      GlStateManager.enableCull();
   }

   private void func_178105_d(float p_178105_1_) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
      float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
      float f2 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
      GlStateManager.translate(f, f1, f2);
   }

   private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_) {
      float f = (float)clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0F;
      float f1 = f / (float)this.itemToRender.getMaxItemUseDuration();
      float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
      if (f1 >= 0.8F) {
         f2 = 0.0F;
      }

      GlStateManager.translate(0.0F, f2, 0.0F);
      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0);
      GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
      GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void transformFirstPersonItem(float equipProgress, float swingProgress) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float f = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0F);
      float f1 = f / 20.0F;
      f1 = (f1 * f1 + f1 * 2.0F) / 3.0F;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      if (f1 > 0.1F) {
         float f2 = MathHelper.sin((f - 0.1F) * 1.3F);
         float f3 = f1 - 0.1F;
         float f4 = f2 * f3;
         GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
      }

      GlStateManager.translate(f1 * 0.0F, f1 * 0.0F, f1 * 0.1F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F + f1 * 0.2F);
   }

   private void func_178103_d(float qq) {
      GlStateManager.translate(-0.5F, qq, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   public void renderItemInFirstPerson(float partialTicks) {
      try {
         if (mc.thePlayer == null || mc.theWorld == null) {
            return;
         }


         float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
         EntityPlayerSP entityplayersp = this.mc.thePlayer;
         float f1 = entityplayersp.getSwingProgress(partialTicks);
         float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83f);
         float f2 = entityplayersp.prevRotationPitch + (entityplayersp.rotationPitch - entityplayersp.prevRotationPitch) * partialTicks;
         float f3 = entityplayersp.prevRotationYaw + (entityplayersp.rotationYaw - entityplayersp.prevRotationYaw) * partialTicks;
         this.func_178101_a(f2, f3);
         this.func_178109_a(entityplayersp);
         this.func_178110_a(entityplayersp, partialTicks);
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         ++this.spinCounter;
         if (this.itemToRender != null) {
            EventItemRenderer eventItemRenderer = new EventItemRenderer(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0);
            EventHandler.call(eventItemRenderer);
            boolean b = entityplayersp.getItemInUseCount() > 0;
            ItemStack currentItem = this.mc.thePlayer.getCurrentEquippedItem();
            if (currentItem != null
                    && mm.blockAnimation.isToggled()
                    && mm.blockAnimation.fakeBlock.getBoolean()
                    && f1 > 0.0F
                    && currentItem.getItem() instanceof ItemSword) {
               b = true;
            }
            if (currentItem != null && fakeBlocking && mc.thePlayer != null && mc.theWorld != null && currentItem.getItem() instanceof ItemSword) {
               b = true;
            }
            GlStateManager.translate(eventItemRenderer.getX(), eventItemRenderer.getY(), eventItemRenderer.getZ());
            if (this.itemToRender.getItem() instanceof ItemMap) {
               this.renderItemMap(entityplayersp, f2, f, f1);
            } else if (!b) {
               this.func_178105_d(f1);
               this.transformFirstPersonItem(f, f1);
            } else {
               GlStateManager.translate(-eventItemRenderer.getX(), -eventItemRenderer.getY(), -eventItemRenderer.getZ());
               GlStateManager.translate(-eventItemRenderer.getBlockX(), -eventItemRenderer.getBlockY(), -eventItemRenderer.getBlockZ());
               EnumAction enumaction = this.itemToRender.getItemUseAction();
               switch (ItemRenderer.ItemRenderer$1.field_178094_a[enumaction.ordinal()]) {
                  case 1:
                     this.transformFirstPersonItem(f, 0.0F);
                     break;
                  case 2:
                  case 3:
                     this.func_178104_a(entityplayersp, partialTicks);
                     this.transformFirstPersonItem(f, f1);
                     break;
                  case 4:
                     if (mm.blockAnimation.isToggled()) {
                        GlStateManager.translate(0.0, mm.blockAnimation.height.getValue(), 0.0);
                     }
                     if (mm.blockAnimation.isToggled()) {
                        switch (mm.blockAnimation.animation.getSelected()) {
                           case "1.7": {
                              this.transformFirstPersonItem(f, f1);
                              break;
                           }
                           case "Exhibition": {
                              transformFirstPersonItem(f, 0.83F);
                              float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                              GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                              GlStateManager.rotate(-f4 * 0.0F, 0.0F, 0.0F, 0.0F);
                              GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                              break;
                           }
                           case "Slide": {
                              transformFirstPersonItem(f1 != 0 ? Math.max(1 - (f1 * 2), 0) * 0.7F : 0, 1F);
                              break;
                           }
                           case "Spin": {
                              rotateSword(f1);
                              break;
                           }
                           case "Spin2": {
                              GL11.glTranslated(-0.04, 0.1, 0.0);
                              this.transformFirstPersonItem(equippedProgress / 2.5F, 0.0F);
                              GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.2F);
                              GlStateManager.rotate((float) this.spinCounter, 0.0F, -1.0F, 0.0F);
                              break;
                           }
                           case "Smooth": {
                              transformFirstPersonItem(f , 0.0f);
                              GlStateManager.translate(-0.05f, 0.3f, 0.3f);
                              GlStateManager.rotate(-var9 * 0.0f, 10.0f, 0.0f, 10.0f);
                              GlStateManager.rotate(var9 * 10.0f, 30.0f, 0.0f, 30.0f);
                              break;
                           }
                           case "Push": {
                              this.transformFirstPersonItem(f, 0.0F);
                              this.func_178103_d(0.2f);
                              float var11 = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                              GlStateManager.translate(-0.05f, 0.2f, 0.0f);
                              GlStateManager.rotate(-var11 * 280.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                              GlStateManager.rotate(-var11 * 100.0f, 1.0f, -0.4f, -0.0f);
                              break;
                           }
                           case "Liquid": {
                              this.transformFirstPersonItem(f + 0.1F, f1);
                              GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                              break;
                           }
                           case "Astolfo": {
                              var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                              GL11.glTranslated(0.0D, 0.0D, 0.0D);
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 2.5f, 0.0f);
                              GlStateManager.rotate(-var9 * 58.0F / 2.0F, var9 / 2.0F, 1.0F, 0.5F);
                              GlStateManager.rotate(-var9 * 43.0F, 1.0F, var9 / 3.0F, -0.0F);
                              break;
                           }
                           case "AstolfoSpin": {
                              GlStateManager.rotate(this.delay, 0.0F, 0.0F, -0.1F);
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 1.5F, 0.0F);
                              long currentTime = System.currentTimeMillis();
                              long elapsedTime = currentTime - lastUpdateTime;
                              if (this.rotateTimer.reached(1L)) {
                                 this.delay += (float) (elapsedTime * 360.0 / 850.0);
                                 this.rotateTimer.reset();
                              }
                              lastUpdateTime = currentTime;
                              if (this.delay > 360.0F)
                                 this.delay = 0.0F;
                              break;
                           }
                           case "Moon": {
                              var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                              GL11.glTranslated(0.0D, 0.0D, 0.0D);
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 1.4f, 0.0f);
                              GlStateManager.rotate(-var9 * 65.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                              GlStateManager.rotate(-var9 * 60.0F, 1.0F, var9 / 3.0F, -0.0F);
                              break;
                           }
                           case "MoonPush": {
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else transformFirstPersonItem(f / 1.5f, 0.0f);
                              final float sin = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                              GlStateManager.scale(1.0F, 1.0F, 1.0F);
                              GlStateManager.translate(-0.2F, 0.45F, 0.25F);
                              GlStateManager.rotate(-sin * 20.0F, -5.0F, -5.0F, 9.0F);
                              break;
                           }
                           case "Jigsaw": {
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, f1);
                              else transformFirstPersonItem(f, f1);
                              GlStateManager.translate(-0.5D, 0.0D, 0.0D);
                              break;
                           }
                           case "Invent": {
                              final float table = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                              GlStateManager.rotate(-table * 30.0F, -8.0F, -0.2F, 9.0F);
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 1.8f, 0.0f);
                              break;
                           }
                           case "Leaked": {
                              final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 transformFirstPersonItem(0.0F, 0.0f);
                              else transformFirstPersonItem(f / 1.4F, 0.0f);
                              GlStateManager.rotate(-var * 41F, 1.1F, 0.8F, -0.3F);
                              break;
                           }
                           case "Aqua": {
                              var9 = MathHelper.sin(MathHelper.sqrt_float(this.mc.thePlayer.getSwingProgress(partialTicks)) * 3.1415927F);
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 6.0f, 0.0f);
                              GlStateManager.rotate(-var9 * 17.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                              GlStateManager.rotate(-var9 * 6.0F, 1.0F, var9 / 3.0F, -0.0F);
                              break;
                           }
                           case "Astro": {
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, f1);
                              else this.transformFirstPersonItem(f / 2.3F, f1);
                              var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                              GlStateManager.rotate(var9 * 50.0F / 9.0F, -var9, -0.0F, 90.0F);
                              GlStateManager.rotate(var9 * 50.0F, 200.0F, -var9 / 2.0F, -0.0F);
                              break;
                           }
                           case "Float": {
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else this.transformFirstPersonItem(f / 2.0f, 0.0f);
                              GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                              GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                              break;
                           }
                           case "Remix": {
                              final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 1.0F);
                              else transformFirstPersonItem(f / 1.4f, 1.0f);
                              GlStateManager.rotate(0.0F, -2.0F, 0.0F, 10.0F);
                              GlStateManager.rotate(-var * 25.0F, 0.5F, 0F, 1F);
                              break;
                           }
                           case "Avatar": {
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.avatar(0.0F, f1);
                              else this.avatar(f / 2.5f, f1);
                              break;
                           }
                           case "Xiv": {
                              final float var = MathHelper.sin((float) (MathHelper.sqrt_float(f1) * Math.PI));
                              if (mm.blockAnimation.cancleE.getBoolean())
                                 this.transformFirstPersonItem(0.0F, 0.0F);
                              else transformFirstPersonItem(f / 1.5f, 0.0f);
                              final float var16 = MathHelper.sin((float) (f1 * f1 * Math.PI));
                              GlStateManager.rotate(-var16 * 20.0f, 0.0f, 1.0f, 0.0f);
                              GlStateManager.rotate(-var * 20.0f, 0.0f, 0.0f, 1.0f);
                              GlStateManager.rotate(-var * 80.0f, 1.0f, 0.0f, 0.0f);
                              break;
                           }
                        }
                     } else {
                        this.transformFirstPersonItem(f, 0.0F);
                     }
                  /*
                  if (mm.blockAnimation.isToggled() && mm.blockAnimation.oldBlock.getBoolean()) {
                     this.transformFirstPersonItem(f, f1);
                  } else {
                     this.transformFirstPersonItem(f, 0.0F);
                  }
                  */
                     this.func_178103_d(0.2f);
                     break;
                  case 5:
                     this.transformFirstPersonItem(f, f1);
                     this.func_178098_a(partialTicks, entityplayersp);
               }
            }

            GlStateManager.scale(eventItemRenderer.getScale(), eventItemRenderer.getScale(), eventItemRenderer.getScale());
            this.renderItem(entityplayersp, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
         } else if (!entityplayersp.isInvisible()) {
            this.func_178095_a(entityplayersp, f, f1);
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
      } catch (NullPointerException nullPointerException) {
         nullPointerException.getCause().printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   private void rotateSword(float f1){
      genCustom();
      //func_178103_d();
      GlStateManager.translate(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotate(MathHelper.sqrt_float(f1) * 10.0F * 40.0F, 1.0F, -0.0F, 2.0F);
   }

   private void genCustom() {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, (float) 0.0 * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float var3 = MathHelper.sin((float) 0.0 * (float) 0.0 * 3.1415927F);
      float var4 = MathHelper.sin(MathHelper.sqrt_float((float) 0.0) * 3.1415927F);
      GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
      GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
      GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }
   public void renderOverlays(float partialTicks) {
      GlStateManager.disableAlpha();
      if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
         IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
         BlockPos blockpos = new BlockPos(this.mc.thePlayer);
         EntityPlayerSP entityplayersp = this.mc.thePlayer;

         for(int i = 0; i < 8; ++i) {
            double d0 = entityplayersp.posX + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayersp.width * 0.8F);
            double d1 = entityplayersp.posY + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = entityplayersp.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayersp.width * 0.8F);
            BlockPos blockpos1 = new BlockPos(d0, d1 + (double)entityplayersp.getEyeHeight(), d2);
            IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);
            if (iblockstate1.getBlock().isVisuallyOpaque()) {
               iblockstate = iblockstate1;
               blockpos = blockpos1;
            }
         }

         if (iblockstate.getBlock().getRenderType() != -1) {
            Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
            if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, partialTicks, object, iblockstate, blockpos)) {
               this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
            }
         }
      }

      if (!this.mc.thePlayer.isSpectator()) {
         if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
                 && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, partialTicks)) {
            this.renderWaterOverlayTexture(partialTicks);
         }

         if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, partialTicks)) {
            this.renderFireInFirstPerson(partialTicks);
         }
      }

      GlStateManager.enableAlpha();
   }

   private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_) {
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      float f = 0.1F;
      GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = p_178108_2_.getMinU();
      float f7 = p_178108_2_.getMaxU();
      float f8 = p_178108_2_.getMinV();
      float f9 = p_178108_2_.getMaxV();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos(-1.0, -1.0, -0.5).tex((double)f7, (double)f9).endVertex();
      worldrenderer.pos(1.0, -1.0, -0.5).tex((double)f6, (double)f9).endVertex();
      worldrenderer.pos(1.0, 1.0, -0.5).tex((double)f6, (double)f8).endVertex();
      worldrenderer.pos(-1.0, 1.0, -0.5).tex((double)f7, (double)f8).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderWaterOverlayTexture(float p_78448_1_) {
      if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
         this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         float f = this.mc.thePlayer.getBrightness(p_78448_1_);
         GlStateManager.color(f, f, f, 0.5F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         float f1 = 4.0F;
         float f2 = -1.0F;
         float f3 = 1.0F;
         float f4 = -1.0F;
         float f5 = 1.0F;
         float f6 = -0.5F;
         float f7 = -this.mc.thePlayer.rotationYaw / 64.0F;
         float f8 = this.mc.thePlayer.rotationPitch / 64.0F;
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldrenderer.pos(-1.0, -1.0, -0.5).tex((double)(4.0F + f7), (double)(4.0F + f8)).endVertex();
         worldrenderer.pos(1.0, -1.0, -0.5).tex((double)(0.0F + f7), (double)(4.0F + f8)).endVertex();
         worldrenderer.pos(1.0, 1.0, -0.5).tex((double)(0.0F + f7), (double)(0.0F + f8)).endVertex();
         worldrenderer.pos(-1.0, 1.0, -0.5).tex((double)(4.0F + f7), (double)(0.0F + f8)).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }
   }

   private void renderFireInFirstPerson(float p_78442_1_) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      float f = 1.0F;

      for(int i = 0; i < 2; ++i) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         float f1 = textureatlassprite.getMinU();
         float f2 = textureatlassprite.getMaxU();
         float f3 = textureatlassprite.getMinV();
         float f4 = textureatlassprite.getMaxV();
         float f5 = (0.0F - f) / 2.0F;
         float f6 = f5 + f;
         float f7 = 0.0F - f / 2.0F;
         float f8 = f7 + f;
         float f9 = -0.5F;
         GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldrenderer.pos((double)f5, (double)f7, (double)f9).tex((double)f2, (double)f4).endVertex();
         worldrenderer.pos((double)f6, (double)f7, (double)f9).tex((double)f1, (double)f4).endVertex();
         worldrenderer.pos((double)f6, (double)f8, (double)f9).tex((double)f1, (double)f3).endVertex();
         worldrenderer.pos((double)f5, (double)f8, (double)f9).tex((double)f2, (double)f3).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void updateEquippedItem() {
      if (mc.thePlayer == null || mc.theWorld == null) {
         return;
      }

      this.prevEquippedProgress = this.equippedProgress;
      EntityPlayerSP entityplayersp = this.mc.thePlayer;
      ItemStack itemstack = entityplayersp.inventory.getCurrentItem();
      boolean flag = false;
      if (this.itemToRender != null && itemstack != null) {
         if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
            if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists()) {
               boolean flag1 = Reflector.callBoolean(
                       this.itemToRender.getItem(),
                       Reflector.ForgeItem_shouldCauseReequipAnimation,
                       this.itemToRender,
                       itemstack,
                       this.equippedItemSlot != entityplayersp.inventory.currentItem
               );
               if (!flag1) {
                  this.itemToRender = itemstack;
                  this.equippedItemSlot = entityplayersp.inventory.currentItem;
                  return;
               }
            }

            flag = true;
         }
      } else if (this.itemToRender == null && itemstack == null) {
         flag = false;
      } else {
         flag = true;
      }

      float f2 = 0.4F;
      float f = flag ? 0.0F : 1.0F;
      float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
      this.equippedProgress += f1;
      if (this.equippedProgress < 0.1F) {
         if (Config.isShaders()) {
            Shaders.setItemToRenderMain(itemstack);
         }

         this.itemToRender = itemstack;
         this.equippedItemSlot = entityplayersp.inventory.currentItem;
      }
   }

   public void resetEquippedProgress() {
      this.equippedProgress = 0.0F;
   }

   public void resetEquippedProgress2() {
      this.equippedProgress = 0.0F;
   }

   static final class ItemRenderer$1 {
      static final int[] field_178094_a = new int[EnumAction.values().length];
      private static final String __OBFID = "CL_00002537";

      static {
         try {
            field_178094_a[EnumAction.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            field_178094_a[EnumAction.EAT.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            field_178094_a[EnumAction.DRINK.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            field_178094_a[EnumAction.BLOCK.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            field_178094_a[EnumAction.BOW.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
