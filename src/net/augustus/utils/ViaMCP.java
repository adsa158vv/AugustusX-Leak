package net.augustus.utils;

import viamcp.gui.GuiProtocolSelector;

public class ViaMCP {
   public static boolean isActive() {
      return viamcp.ViaMCP.getInstance().getVersion() > 47 ;
   }
}
