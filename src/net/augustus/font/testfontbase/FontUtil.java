package net.augustus.font.testfontbase;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import net.augustus.font.CustomFontUtil;

public class FontUtil {
   public static volatile int completed;
   public static CustomFontUtil espHotbar;
   public static CustomFontUtil esp;

   public static CustomFontUtil espTitle;
   public static CustomFontUtil verdana;
   public static CustomFontUtil arial;
   public static CustomFontUtil arial18;
   public static CustomFontUtil roboto;
   public static CustomFontUtil comfortaa;

   public static CustomFontUtil tahoma12;
   public static CustomFontUtil tahomabold18;


   public static CustomFontUtil tenacity_blod16;
   public static CustomFontUtil tenacity_blod18;
   public static CustomFontUtil tenacity_blod20;
   public static CustomFontUtil tenacity_blod24;

   public static CustomFontUtil icon;
   public static CustomFontUtil icon36;
   private static final HashMap<String, InputStream> fontInputs = new HashMap<>();

   private static InputStream getInputSteam(String location) {
      InputStream is = null;
      if (!fontInputs.containsKey(location)) {
         is = FontUtil.class.getClassLoader().getResourceAsStream(location);
         fontInputs.put(location, is);
      } else {
         is = fontInputs.get(location);
      }

      return is;
   }

   public static Font getFont(String location, int size) {
      Font font = null;

      try {
         InputStream is = FontUtil.class.getClassLoader().getResourceAsStream(location);
         if (is != null) {
            font = Font.createFont(0, is);
         }
         if (font != null) {
            font = font.deriveFont(Font.PLAIN, (float)size);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         System.err.println("Error loading font");
         font = new Font("default", Font.PLAIN, 10);
      }

      return font;
   }

   public static void bootstrap() {
      espHotbar = new CustomFontUtil("Esp", getFont("esp.ttf", 44));
      esp = new CustomFontUtil("Esp", getFont("esp.ttf", 16));

      tahoma12 = new CustomFontUtil("Tahoma", getFont("tahoma.ttf", 12));
      tahomabold18 = new CustomFontUtil("Tahombold", getFont("tahoma-bold.ttf", 18));

      verdana = new CustomFontUtil("Verdana", getFont("verdana.ttf", 16));
      arial = new CustomFontUtil("Arial", getFont("arial.ttf", 16));
      arial18 = new CustomFontUtil("Arial", getFont("arial.ttf", 18));
      roboto = new CustomFontUtil("Roboto", getFont("roboto.ttf", 16));
      espTitle = new CustomFontUtil("Esp", getFont("esp.ttf", 60));
      comfortaa = new CustomFontUtil("Comfortaa", getFont("comfortaa.ttf", 16));
      tenacity_blod16 = new CustomFontUtil("Tenacity", getFont("tenacity-bold.ttf", 16));
      tenacity_blod18 = new CustomFontUtil("Tenacity", getFont("tenacity-bold.ttf", 18));
      tenacity_blod20 = new CustomFontUtil("Tenacity", getFont("tenacity-bold.ttf", 20));
      tenacity_blod24 = new CustomFontUtil("Tenacity", getFont("tenacity-bold.ttf", 24));
      icon = new CustomFontUtil("icon", getFont("icon.ttf",16));
      icon36 = new CustomFontUtil("icon", getFont("icon.ttf",36));
   }
}
