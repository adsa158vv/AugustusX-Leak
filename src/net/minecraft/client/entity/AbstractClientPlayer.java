package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;


import net.augustus.utils.cape.LoadCape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import optifine.CapeUtils;
import optifine.Config;
import optifine.PlayerConfigurations;
import optifine.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {
   private NetworkPlayerInfo playerInfo;
   private ResourceLocation locationOfCape = null;
   private String nameClear = null;
   private static final String __OBFID = "CL_00000935";

   public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
      super(worldIn, playerProfile);
      this.nameClear = playerProfile.getName();
      if (this.nameClear != null && !this.nameClear.isEmpty()) {
         this.nameClear = StringUtils.stripControlCodes(this.nameClear);
      }

      CapeUtils.downloadCape(this);
      PlayerConfigurations.getPlayerConfiguration(this);
   }

   @Override
   public boolean isSpectator() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   public NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
      }

      if (this.playerInfo != null) {
         this.setProtectedName(this.playerInfo.getProtectedName());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
   }

   public ResourceLocation getLocationCape() {
      if (!Config.isShowCapes()) {
         return null;
      } else if (this.locationOfCape != null) {
         return mm.customCape.isToggled() ? LoadCape.getResourceLocation(mm.customCape.customCapes.getSelected()) : this.locationOfCape;
      } else {
         NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
         return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
      }
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
      Object object = texturemanager.getTexture(resourceLocationIn);
      if (object == null) {
         object = new ThreadDownloadImageData(
                 (File)null,
                 String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)),
                 DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)),
                 new ImageBufferDownload()
         );
         texturemanager.loadTexture(resourceLocationIn, (ITextureObject)object);
      }

      return (ThreadDownloadImageData)object;
   }

   public static ResourceLocation getLocationSkin(String username) {
      return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
   }

   public String getSkinType() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.capabilities.isFlying) {
         f *= 1.2F;
      }

      IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      f = (float)((double)f * ((iattributeinstance.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.2F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
         int i = this.getItemInUseDuration();
         float f1 = (float)i / 20.0F;
         if (f1 > 1.2F) {
            f1 = 1.2F;
         } else {
            f1 *= f1;
         }

         f *= 1.2F - f1 * 0.15F;
      }

      return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, f) : f;
   }

   public String getNameClear() {
      return this.nameClear;
   }

   public ResourceLocation getLocationOfCape() {
      return mm.customCape.isToggled() ? LoadCape.getResourceLocation(mm.customCape.customCapes.getSelected()) : this.locationOfCape;
   }

   public void setLocationOfCape(ResourceLocation p_setLocationOfCape_1_) {
      this.locationOfCape = mm.customCape.isToggled() ? LoadCape.getResourceLocation(mm.customCape.customCapes.getSelected()) : p_setLocationOfCape_1_;
   }
}
