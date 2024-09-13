package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.Augustus;
import net.augustus.cleanGui.CleanClickGui;
import net.augustus.clickgui.clickguis.ClickGui;
import net.augustus.events.EventClickGui;
import net.augustus.events.EventTick;
import net.augustus.material.themes.Dark;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.StringValue;
import net.lenni0451.eventapi.manager.EventManager;
import net.lenni0451.eventapi.reflection.EventTarget;

public class ClickGUI extends Module {
   public StringValue sorting = new StringValue(4526, "Sorting", this, "Random", new String[]{"Random", "Length", "Alphabet"});
   public StringValue mode = new StringValue(12710, "Mode", this, "Default", new String[]{"Default", "Clean", "New"});

   public ClickGUI() {
      super("ClickGui", Color.BLACK, Categorys.RENDER);
   }

   @Override
   public void onEnable() {
      Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
      Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
      if(mode.getSelected().equalsIgnoreCase("Default")) {
         mc.displayGuiScreen(Augustus.getInstance().getClickGui());
      } else if (mode.getSelected().equalsIgnoreCase("Clean")) {
         EventManager.call(new EventClickGui());
         mc.displayGuiScreen(new CleanClickGui());
      } else if(mode.getSelected().equalsIgnoreCase("New")) {
         EventManager.call(new EventClickGui());
         mc.displayGuiScreen(new Dark());
      } else {
         mc.displayGuiScreen(Augustus.getInstance().getClickGui());
      }
   }

   @Override
   public void onDisable() {
      Augustus.getInstance().getConverter().moduleSaver(mm.getModules());
      Augustus.getInstance().getConverter().settingSaver(sm.getStgs());
   }

   @EventTarget
   public void onEventTick(EventTick eventTick) {
      if (!(mc.currentScreen instanceof ClickGui) && this.isToggled()) {
         this.toggle();
      }
   }
}
