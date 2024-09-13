package net.augustus.modules.misc;

import net.augustus.Augustus;
import net.augustus.events.EventReadPacket;
import net.augustus.events.EventRender2D;
import net.augustus.events.EventTick;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.*;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.world.World;

import java.awt.*;

public class SettingsSave extends Module {


   public SettingsSave() {
      super("SettingsSave", Color.red, Categorys.MISC);
   }
   public BooleanValue saveOnGameMenu = new BooleanValue(1,"SaveOnGameMenu",this,true);
   public BooleanValue saveOnDisconnect = new BooleanValue(11,"SaveOnLeftGame",this,true);
   public BooleanValue saveOnChat = new BooleanValue(122,"SaveOnChat",this,true);
   public BooleanValue saveOnWorld = new BooleanValue(112,"SaveOnWorld",this,true);
   private boolean saved1 = false;
   private boolean saved2 = false;
   private boolean saved3 = false;
   private World lastWorld = null;
   private void resetAll() {
      saved1 = false;
      saved2 = false;
      saved3 = false;
      lastWorld = null;
   }
   @Override
   public void onDisable() {
      super.onDisable();
      resetAll();
   }
   @Override
   public void onEnable() {
      super.onEnable();
      resetAll();
   }

   @EventTarget
   public void onEventRender2D(EventRender2D event) {
      if (mc.currentScreen instanceof GuiChat && saveOnChat.getBoolean() && !saved2) {
         Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
         Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
         saved2 = true;
      }
      else if (!(mc.currentScreen instanceof GuiChat && saveOnChat.getBoolean())) saved2 = false;
      if (mc.currentScreen instanceof GuiIngameMenu && saveOnGameMenu.getBoolean() && !saved1) {
         Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
         Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
         saved1 = true;
      }
      else if (!(mc.currentScreen instanceof GuiIngameMenu && saveOnGameMenu.getBoolean())) saved1 = false;
      if (mc.currentScreen instanceof GuiDisconnected && saveOnDisconnect.getBoolean() && !saved3) {
         Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
         Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
         saved3 = true;
      }
      else if (!(mc.currentScreen instanceof GuiDisconnected && saveOnDisconnect.getBoolean())) saved3 = false;
      if (saveOnDisconnect.getBoolean()) {
         if (mc.theWorld != null && lastWorld == null) {
            lastWorld = mc.theWorld;
         }
         if (lastWorld != null && (mc.currentScreen instanceof GuiScreenServerList || mc.currentScreen instanceof GuiSelectWorld) && !saved3) {
            Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
            Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
            lastWorld = null;
            saved3 = true;

         }
      }

   }
   @EventTarget
   public void onEventReadPacket(EventReadPacket eventReadPacket) {
      if (eventReadPacket.getPacket() instanceof S40PacketDisconnect || eventReadPacket.getPacket() instanceof S00PacketDisconnect) {
         if (saveOnDisconnect.getBoolean() && !saved3) {
            Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
            Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
            saved3 = true;
         }

      }
      else saved3 = false;
   }
   @EventTarget
   public void onEventWorld(EventWorld eventWorld) {
      if (saveOnWorld.getBoolean()) {
         Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
         Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
      }
   }

}
