package net.augustus.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.render.ClickGUI;
import net.augustus.notify.GeneralNotifyManager;
import net.augustus.notify.NotificationType;
import net.augustus.utils.AnimationUtil;
import net.augustus.utils.EventHandler;
import net.augustus.utils.interfaces.MC;
import net.augustus.utils.interfaces.MM;
import net.augustus.utils.interfaces.SM;
import net.augustus.utils.skid.lorious.anims.Animation;
import net.augustus.utils.skid.lorious.anims.Easings;
import net.augustus.utils.sound.SoundUtil;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Objects;

public class Module implements MC, MM, SM, Comparable<Module> {
   public Animation yPos = new Animation();
   public Animation xPos = new Animation();
   @Expose
   @SerializedName("Name")
   private String name;
   private String displayName = "";
   @Getter
   @Expose
   @SerializedName("Toggled")
   private boolean toggled;
   @Setter
   @Getter
   @Expose
   @SerializedName("Key")
   private int key;
   @Getter
   @Setter
   @Expose
   @SerializedName("Category")
   private Categorys category;
   private Color color;
   @Getter
   @Setter
   private float x;
   @Setter
   @Getter
   private float y;
   private float minX;
   private float maxX;
   private float moduleWidth = 0.0F;

   public AnimationUtil animationUtil;
   protected ScaledResolution sr = new ScaledResolution(mc);
   public SecureRandom RANDOM = new SecureRandom();

   public Module(String name, Color color, Categorys category) {
      this.name = name;
      this.color = color;
      this.category = category;
      this.displayName = name;
      Augustus.getInstance().getManager().getModules().add(this);
   }

   public final void toggle() {
      if (this.toggled) {
         if (mc.theWorld != null && mm.arrayList.toggleSound.getBoolean() && !this.equals(mm.clickGUI)) {
            EventToggleSound eventToggleSound = new EventToggleSound(this.name,false);
            EventHandler.call(eventToggleSound);
            if (!(eventToggleSound.isCancelled())) {
               SoundUtil.play(SoundUtil.toggleOffSound);
            }
         }
         //NotificationsManager.addNotification(new Notification("Toggled " + name + " off", Notification.Type.ToggleOff));
         GeneralNotifyManager.addNotification("Toggled " + name + " Off", NotificationType.ToggleOff);
         this.toggled = false;
         mm.getActiveModules().remove(this);
         this.onPreDisable();
         EventManager.unregister(this);
         this.onDisable();
      } else {
         if (mc.theWorld != null && mm.arrayList.toggleSound.getBoolean() && !this.equals(mm.clickGUI)) {
            EventToggleSound eventToggleSound = new EventToggleSound(this.name,true);
            EventHandler.call(eventToggleSound);
            if (!(eventToggleSound.isCancelled())) {
               SoundUtil.play(SoundUtil.toggleOnSound);
            }
         }
         //NotificationsManager.addNotification(new Notification("Toggled " + name + " on", Notification.Type.ToggleOn));
         GeneralNotifyManager.addNotification("Toggled " + name + " On", NotificationType.ToggleOn);
         this.toggled = true;
         if (!(this instanceof ClickGUI)) {
            mm.getActiveModules().add(this);
         }

         this.onEnable();
         EventManager.register(this);
      }

      mm.arrayList.updateSorting();
   }
   public final void toggleNoNoti() {
      if (this.toggled) {
         if (mc.theWorld != null && mm.arrayList.toggleSound.getBoolean() && !this.equals(mm.clickGUI)) {
            this.toggled = false;
            mm.getActiveModules().remove(this);
            this.onPreDisable();
            EventManager.unregister(this);
            this.onDisable();
         }
      } else {
         this.toggled = true;
         if (!(this instanceof ClickGUI)) {
            mm.getActiveModules().add(this);
         }

         this.onEnable();
         EventManager.register(this);
      }

      mm.arrayList.updateSorting();
   }

   public void updatePos() {
      if (this.isToggled()) {
         if (this.getXPos().getTarget() != Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(this.getDisplayName()) + 5.0) {
            this.getXPos().animate(Augustus.getInstance().getLoriousFontService().getComfortaa18().getStringWidth(this.getDisplayName()) + 5.0, 350.0, Easings.QUAD_BOTH);
         }
      } else if (this.getXPos().getTarget() != -5.0) {
         this.getXPos().animate(-5.0, 350.0, Easings.QUAD_BOTH);
      }
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void onPreDisable() {
   }

   public Animation getYPos() {
      return this.yPos;
   }

   public Animation getXPos() {
      return this.xPos;
   }

   public Color getColor() {
      return this.color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setState(Boolean state){
      this.toggled = state;
   }

   public void setDisplayName(String displayName) {
      String lastName = this.displayName;
      if (mc.thePlayer != null) {
         if (mm.arrayList.isToggled() && mm.arrayList.suffix.getBoolean()) {
            if (Objects.equals(this.displayName, displayName)) return;
            this.displayName = displayName;
         } else {
            if (Objects.equals(this.displayName, this.name)) return;
            this.displayName = this.name;
         }
      } else {
         if (Objects.equals(this.displayName, displayName)) return;
         this.displayName = displayName;
      }

      if (!this.displayName.equalsIgnoreCase(lastName)) {
         mm.arrayList.updateSorting();
      }
   }

   public void setSuffix(String suffix,boolean shouldShow) {
      if (mm.arrayList.isToggled() && mm.arrayList.suffix.getBoolean() && shouldShow) {
         if (!Objects.equals(this.displayName, this.name + "§8" + suffix)) {
            this.setDisplayName(this.name + "§8<" + suffix+">");
            //LogUtil.addChatMessage("set");
         }
      } else {
         if (!Objects.equals(this.displayName, this.name)) {
            this.setDisplayName(this.name);
         }
      }
   }

   public void setToggled(boolean toggled) {
      if (this.toggled != toggled) {
         this.toggle();
      }
   }
   public void restart(boolean noNoti) {
      if (!noNoti) {
         this.toggle();
         this.toggle();
      } else {
         this.toggleNoNoti();
         this.toggleNoNoti();
      }
   }

   public float getMaxX() {
      return this.maxX;
   }

   public void setMaxX(float maxX) {
      this.maxX = maxX;
   }

   public float getMinX() {
      return this.minX;
   }

   public void setMinX(float minX) {
      this.minX = minX;
   }

   public AnimationUtil getAnimationUtil() {
      return this.animationUtil;
   }

   public float getModuleWidth() {
      return this.moduleWidth;
   }

   public void setModuleWidth(float moduleWidth) {
      this.moduleWidth = moduleWidth;
   }

   public void setAnimationUtil(AnimationUtil animationUtil) {
      this.animationUtil = animationUtil;
   }

   public void readModule(Module module) {
      this.setName(module.getName());
      if (!(this instanceof ClickGUI)) {
         this.setToggled(module.isToggled());
      }

      this.setCategory(module.getCategory());
      this.setKey(module.getKey());
   }

   public void readConfig(Module module) {
      if (!(this instanceof ClickGUI)) {
         this.setToggled(module.isToggled());
      }
   }

   public int compareTo(Module module) {
      if (mm.arrayList.sortOption.getSelected().equals("Alphabet")) {
         return -module.getName().compareTo(this.getName());
      } else {
         return mm.arrayList.font.getSelected().equals("Minecraft")
            ? mc.fontRendererObj.getStringWidth(module.getDisplayName()) - mc.fontRendererObj.getStringWidth(this.getDisplayName())
            : Math.round(
               mm.arrayList.getCustomFont().getStringWidth(module.getDisplayName())
                  - mm.arrayList.getCustomFont().getStringWidth(FontRenderer.getFormatFromString(module.getDisplayName()))
                  - (
                     mm.arrayList.getCustomFont().getStringWidth(this.getDisplayName())
                        - mm.arrayList.getCustomFont().getStringWidth(FontRenderer.getFormatFromString(this.getDisplayName()))
                  )
            );
      }
   }

   public String getSuffix() {
      return this.displayName.replace(this.name, "");
   }


    public void onWorld(EventWorld event) {

    }

    public void onUpdate(EventUpdate event) {

    }
   public boolean getState() {
      return this.toggled;
   }

    public void onAttack(EventAttackEntity event) {

    }

    public void onPacketReceive(EventReadPacket e) {

    }

    public void onPacketSend(EventSendPacket e) {

    }


}
