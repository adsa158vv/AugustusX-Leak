package me.jDev.augustusx.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.jDev.augustusx.files.parts.ClickGuiPart;
import me.jDev.augustusx.files.parts.ConfigPart;
import me.jDev.augustusx.files.parts.SettingPart;
import net.augustus.Augustus;
import net.augustus.clickgui.buttons.CategoryButton;
import net.augustus.modules.Module;
import net.augustus.settings.*;
import net.augustus.utils.PlayerUtil;
import net.augustus.utils.interfaces.MC;
import net.augustus.utils.interfaces.MM;
import net.augustus.utils.interfaces.SM;
import net.augustus.utils.shader.ShaderUtil;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Converter implements MC, MM, SM {
   private final String path = "AugustusX";
   public static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
   public static final Gson NORMALGSON = new GsonBuilder().setPrettyPrinting().create();

   public void moduleSaver(List<Module> moduleList) {
      FileManager<Module> fileManager = new FileManager<>();
      fileManager.saveFile(this.path + "/settings", "module.json", moduleList);
   }

   public void moduleReader(List<Module> moduleList) {
      File file = new File(this.path + "/settings/module.json");
      if (!file.exists()) {
         this.moduleSaver(moduleList);
      }

      ArrayList<Module> loadedModules = new ArrayList<>();

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/module.json"));
         loadedModules = GSON.fromJson(reader, (new TypeToken<List<Module>>() {}).getType());
         reader.close();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      for(Module module : moduleList) {
         for(Module loadedModule : loadedModules) {
            if (loadedModule != null && module != null && loadedModule.getName().equalsIgnoreCase(module.getName())) {
               module.readModule(loadedModule);
            }
         }
      }
   }

   public void saveBackground(ShaderUtil shaderUtil) {
      FileManager<String> fileManager = new FileManager<>();
      fileManager.saveFile(this.path + "/settings", "background.json", shaderUtil.getName());
   }

   public String readBackground() {
      File file = new File(this.path + "/settings/background.json");
      if (!file.exists()) {
         ShaderUtil shaderUtil = new ShaderUtil();
         shaderUtil.setName("Trinity");
         shaderUtil.createBackgroundShader(this.path + "/shaders/trinity.frag");
         this.saveBackground(shaderUtil);
      }

      String shaderName = null;

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/background.json"));
         shaderName = new Gson().fromJson(reader, (new TypeToken<String>() {
         }).getType());
         reader.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return shaderName;
   }

   public void saveLastAlts(List<String> lastAlts) {
      FileManager<List<String>> fileManager = new FileManager<>();
      fileManager.saveFile(this.path + "/alts", "lastAlts.json", lastAlts);
   }

   public void readLastAlts() {
      File file = new File(this.path + "/alts/lastAlts.json");
      if (!file.exists()) {
         this.saveLastAlts(new ArrayList<>());
      }

      ArrayList<String> altList = new ArrayList<>();

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/alts/lastAlts.json"));
         altList = new Gson().fromJson(reader, (new TypeToken<List<String>>() {
         }).getType());
         reader.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      if (!altList.isEmpty()) {
         Augustus.getInstance().setLastAlts(altList);
      } else {
         System.err.println("No last alts found!");
      }
   }

   public void settingSaver(ArrayList<Setting> settingList) {
      try {
         FileManager<Setting> fileManager = new FileManager<>();
         fileManager.saveFile(this.path + "/settings", "settings.json", settingList);
         System.out.println("[AugustusX] Saved Settings at "+path+"/settings/settings.json !");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void settingReader(ArrayList<Setting> settingList) {
      File file = new File(this.path + "/settings/settings.json");
      if (!file.exists()) {
         this.settingSaver(settingList);
         System.out.println("[AugustusX] Created and Saved Settings at " + path + "/settings/settings.json !");
      }

      ArrayList<SettingPart> settings = new ArrayList<>();
      try (Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/settings.json"), StandardCharsets.UTF_8)) {
         settings = GSON.fromJson(reader, new TypeToken<ArrayList<SettingPart>>() {}.getType());
      } catch (Exception e) {
         e.printStackTrace();
      }


      for(SettingPart loadedSetting : settings) {
         for(Setting setting : settingList) {

            //System.out.println(loadedSetting.getName() +" "+loadedSetting.getParentName() + loadedSetting.isaBoolean());
            //System.out.println(loadedSetting.getId()+" "+setting.getId());
            if (setting != null
               && loadedSetting != null
               && loadedSetting.getId() == setting.getId()
               && loadedSetting.getName().equalsIgnoreCase(setting.getName())
               && loadedSetting.getParentName().equalsIgnoreCase(setting.getParentName())) {
               if (setting instanceof BooleanValue) {
                  BooleanValue booleanValue = (BooleanValue)setting;
                  booleanValue.readSetting(loadedSetting);
               } else if (setting instanceof ColorSetting) {
                  ColorSetting colorSetting = (ColorSetting)setting;
                  colorSetting.readSetting(loadedSetting);
               } else if (setting instanceof DoubleValue) {
                  DoubleValue doubleValue = (DoubleValue)setting;
                  doubleValue.readSetting(loadedSetting);
               } else if (setting instanceof StringValue) {
                  StringValue stringValue = (StringValue)setting;
                  stringValue.readSetting(loadedSetting);
               }
            }
         }
      }
      System.out.println("[AugustusX] Read Settings at "+path+"/settings/settings.json !");
   }

   public void clickGuiSaver(List<CategoryButton> categoryButtons) {
      List<ClickGuiPart> clickGuiParts = new ArrayList<>();

      for(CategoryButton categoryButton : categoryButtons) {
         clickGuiParts.add(new ClickGuiPart(categoryButton.xPosition, categoryButton.yPosition, categoryButton.isUnfolded(), categoryButton.getCategory()));
      }

      FileManager<ClickGuiPart> fileManager = new FileManager<>();
      fileManager.saveAllFile(this.path + "/settings", "clickGui.json", clickGuiParts);
      System.out.println("[AugustusX] Saved ClickGui at "+path+"/settings/clickGui.json !");
   }

   public void clickGuiLoader(List<CategoryButton> categoryButtons) {
      File file = new File(this.path + "/settings/clickGui.json");
      if (!file.exists()) {
         this.clickGuiSaver(categoryButtons);
      }

      ArrayList<ClickGuiPart> clickGuiParts = new ArrayList<>();

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/clickGui.json"));
         clickGuiParts = new Gson().fromJson(reader, (new TypeToken<List<ClickGuiPart>>() {
         }).getType());
         reader.close();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      for(CategoryButton categoryButton : Augustus.getInstance().getClickGui().getCategoryButtons()) {
         for(ClickGuiPart clickGuiPart : clickGuiParts) {
            if (categoryButton.getCategory() == clickGuiPart.getCategory()) {
               if (clickGuiPart.getX() >= 0 && clickGuiPart.getY() >= 0) {
                  categoryButton.xPosition = clickGuiPart.getX();
                  categoryButton.yPosition = clickGuiPart.getY();
               }

               categoryButton.setUnfolded(clickGuiPart.isOpen());
            }
         }
      }
   }

   public void configSaver(String name) {
      try {

         GregorianCalendar now = new GregorianCalendar();
         DateFormat df = DateFormat.getDateInstance(2);
         DateFormat df2 = DateFormat.getTimeInstance(3);
         ArrayList<SettingPart> settingParts = new ArrayList<>();

         for (Setting setting : sm.getStgs()) {
            if (setting instanceof BooleanValue) {
               settingParts.add(new SettingPart(setting.getId(), setting.getName(), setting.getParent(), ((BooleanValue) setting).getBoolean()));
            } else if (setting instanceof ColorSetting) {
               ColorSetting colorSetting = (ColorSetting) setting;
               settingParts.add(
                       new SettingPart(
                               colorSetting.getId(),
                               colorSetting.getName(),
                               colorSetting.getParent(),
                               colorSetting.getColor().getRed(),
                               colorSetting.getColor().getGreen(),
                               colorSetting.getColor().getBlue(),
                               colorSetting.getColor().getAlpha()
                       )
               );
            } else if (setting instanceof StringValue) {
               settingParts.add(
                       new SettingPart(
                               setting.getId(), setting.getName(), setting.getParent(), ((StringValue) setting).getSelected(), ((StringValue) setting).getStringList()
                       )
               );
            } else if (setting instanceof DoubleValue) {
               settingParts.add(
                       new SettingPart(
                               setting.getId(),
                               setting.getName(),
                               setting.getParent(),
                               ((DoubleValue) setting).getValue(),
                               ((DoubleValue) setting).getMinValue(),
                               ((DoubleValue) setting).getMaxValue(),
                               ((DoubleValue) setting).getDecimalPlaces()
                       )
               );
            }
         }

         FileManager<ConfigPart> fileManager = new FileManager<>();
         fileManager.saveFile(
                 this.path + "/configs",
                 name + ".json",
                 new ConfigPart(name, df.format(now.getTime()), df2.format(now.getTime()), (ArrayList<Module>) mm.getModules(), settingParts)
         );
         if (mc.thePlayer != null && mc.theWorld != null) {
            PlayerUtil.sendChatNoSpace("§f[§6Config§f] §2Save §8"+name+".json§2 Successfully !");
         }
         System.out.println("[AugustusX][Config] Save "+name+" Successfully !");
      }catch (Exception e) {
         System.err.println("[AugustusX][Config] Failed to Save Config §2"+name+".json§4 !");
         if (mc.thePlayer != null && mc.theWorld != null) {
            PlayerUtil.sendChatNoSpace("§4[Config][ERROR] Failed to Save Config §2"+name+".json§4 !");
         }
         e.printStackTrace();
      }
   }

   public String[] configReader(String name) {
      File file = new File(this.path + "/configs/" + name + ".json");
      if (!file.exists()) {
         try {
            this.configSaver(name);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      ConfigPart configPart = null;
      String[] result = null;

      try (Reader reader = Files.newBufferedReader(Paths.get(this.path + "/configs/" + name + ".json"), StandardCharsets.UTF_8)) {
         configPart = GSON.fromJson(reader, ConfigPart.class);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (JsonSyntaxException e) {
         System.err.println("[ConfigReader] JSON Syntax Exception: " + e.getMessage());
         e.printStackTrace();
         if (mc.thePlayer != null && mc.theWorld != null) {
            PlayerUtil.sendChatNoSpace("§6[Config-Reader]§4[ERROR] Wrong Json Syntax! Please Check §2"+name+".json§4 !");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (configPart != null) {
         result = new String[]{configPart.getName(), configPart.getDate(), configPart.getTime()};
      }
      return result;
   }

   public void configLoader(String name) {
      File file = new File(this.path + "/configs/" + name + ".json");
      if (!file.exists()) {
         this.configSaver(name);
      }

      ConfigPart configPart = null;

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/configs/" + name + ".json"));
         configPart = new Gson().fromJson(reader, (new TypeToken<ConfigPart>() {
         }).getType());
         reader.close();
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      if (configPart == null) {
         if (mc.thePlayer != null && mc.theWorld != null) {
            PlayerUtil.sendChatNoSpace("§f[§6Config§f] §4Incorrect Config §8"+file.getName()+" §4!");
         }
         System.err.println("[Config] Incorrect Config "+file.getName()+" !");
      } else {
         for(Module module : mm.getModules()) {
            for(Module loadedModule : configPart.getModules()) {
               if (loadedModule.getName().equalsIgnoreCase(module.getName())) {
                  module.readConfig(loadedModule);
               }
            }
         }

         for(Setting setting : sm.getStgs()) {
            for(SettingPart loadedSetting : configPart.getSettingParts()) {
               if (loadedSetting.getId() == setting.getId()
                  && loadedSetting.getName().equalsIgnoreCase(setting.getName())
                  && loadedSetting.getParentName().equalsIgnoreCase(setting.getParent().getName())) {
                  if (setting instanceof BooleanValue) {
                     BooleanValue booleanValue = (BooleanValue)setting;
                     booleanValue.readConfigSetting(loadedSetting);
                  } else if (setting instanceof ColorSetting) {
                     ColorSetting colorSetting = (ColorSetting)setting;
                     colorSetting.readConfigSetting(loadedSetting);
                  } else if (setting instanceof DoubleValue) {
                     DoubleValue doubleValue = (DoubleValue)setting;
                     doubleValue.readConfigSetting(loadedSetting);
                  } else if (setting instanceof StringValue) {
                     StringValue stringValue = (StringValue)setting;
                     stringValue.readConfigSetting(loadedSetting);
                  }
               }
            }
         }
         if (mc.thePlayer != null && mc.theWorld != null) {
            PlayerUtil.sendChatNoSpace("§f[§6Config§f] §2Load §8"+file.getName()+"§2 Successfully !");
         }
         System.out.println("[Config] Load "+file.getName()+" Successfully !");
      }
   }

   public void soundSaver(String soundName) {
      FileManager<String> fileManager = new FileManager<>();
      fileManager.saveAllFile(this.path + "/settings", "sound.json", soundName);
   }

   public String readSound() {
      File file = new File(this.path + "/settings/sound.json");
      if (!file.exists()) {
         this.soundSaver("Vanilla");
      }

      String soundName = null;

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/sound.json"));
         soundName = new Gson().fromJson(reader, (new TypeToken<String>() {
         }).getType());
         reader.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return soundName;
   }

   public void apiSaver(String apiKey) {
      FileManager<String> fileManager = new FileManager<>();
      fileManager.saveAllFile(this.path + "/settings", "api.json", apiKey);
   }

   public String apiLoader() {
      File file = new File(this.path + "/settings/api.json");
      if (!file.exists()) {
         this.apiSaver("");
      }

      String apiKey = null;

      try {
         Reader reader = Files.newBufferedReader(Paths.get(this.path + "/settings/api.json"));
         apiKey = new Gson().fromJson(reader, (new TypeToken<String>() {
         }).getType());
         reader.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return apiKey;
   }
}
