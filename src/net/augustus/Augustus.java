package net.augustus;

import lombok.Getter;
import lombok.Setter;
import me.jDev.augustusx.files.Converter;
import net.augustus.cleanGui.CleanClickGui;
import net.augustus.clickgui.clickguis.ClickGui;
import net.augustus.clickgui.SettingSorter;
import net.augustus.commands.CommandManager;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.modules.Manager;
import net.augustus.modules.Module;
import net.augustus.modules.ModuleManager;
import net.augustus.notify.rise5.NotificationManager;
import net.augustus.settings.Setting;
import net.augustus.settings.SettingsManager;
import net.augustus.ui.augustusmanager.AugustusSounds;
import net.augustus.utils.BlockUtil;
import net.augustus.utils.ColorUtil;
import net.augustus.utils.YawPitchHelper;
import net.augustus.utils.shader.BackgroundShaderUtil;
import net.augustus.utils.skid.lorious.font.Fonts;
import net.augustus.utils.spoof.SlotSpoofer;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.lwjgl.Sys;
import viamcp.ViaMCP;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Augustus {


   @Getter
   public static final Augustus instance = new Augustus();
   public static Minecraft mc= Minecraft.getMinecraft();
   public String name = "AugustusX";
   public String version = "13.24.37.43";
   public String codeversion = "0";
   public String release = "Leak";
   public String dev = "ESound(Aug2.6) + [Cookie + SaFeBaum + Johnny-Online-YT](Xenza) + [ABitofWither + Fun + Genius + Wxcer + Gelory + QingKong + MuFeng](Modifier)";
   public String srcgiver = "Xylitol+Actinium+LiquidBounceNextGen+Gotaj+Atani+FDPClient+ChatGPT+KiMiAI";
   public final Color clientColor = new Color(41, 146, 222);
   public List<String> lastAlts = new ArrayList<>();
   public final Manager manager = new Manager();
   public ModuleManager moduleManager;
   public SettingsManager settingsManager;
   public CommandManager commandManager;
   public SlotSpoofer slotSpoofer;
   public CleanClickGui cleanClickGui;
   public ClickGui clickGui;
   public Converter converter;
   public BackgroundShaderUtil backgroundShaderUtil;
   public float shaderSpeed = 1800.0F;
   public SettingSorter settingSorter;
   public YawPitchHelper yawPitchHelper;
   public String uid = "";
   public BlockUtil blockUtil;
   public Fonts loriousFontService;
   public String user = "User";
   public Proxy proxy;
   public NotificationManager rise5notifyManager;



   public ColorUtil colorUtil;


   public void preStart() {



      Path dir = Paths.get("AugustusX/configs");
      Path dir2 = Paths.get("AugustusX/capes");
      if (!Files.exists(dir)) {
         try {
            Files.createDirectories(dir);
         } catch (IOException var2) {
            var2.printStackTrace();
         }
      }
      if (!Files.exists(dir2)) {
         try {
            Files.createDirectories(dir2);
         } catch (IOException var2) {
            var2.printStackTrace();
         }
      }

   }
   public boolean checkBetaVersionInJar() {
      // 获取当前类加载器
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      // 获取"betaVersion"文件的URL
      URL betaVersionUrl = classLoader.getResource("betaVersion");
      // 如果URL不为空，则表示文件存在
      return betaVersionUrl != null;
   }
   public void start() {
      name = "AugustusX";
      System.out.println("Starting Client...");
      FontUtil.bootstrap();
      this.loriousFontService = new Fonts();
      this.loriousFontService.bootstrap();
      this.colorUtil = new ColorUtil();
      this.yawPitchHelper = new YawPitchHelper();
      this.slotSpoofer = new SlotSpoofer();
      this.settingsManager = new SettingsManager();
      this.moduleManager = new ModuleManager();
      this.commandManager = new CommandManager();
      this.rise5notifyManager = new NotificationManager();
      this.clickGui = new ClickGui("ClickGui");
      this.converter = new Converter();
      this.converter.settingReader(this.settingsManager.getStgs());
      this.converter.moduleReader(this.moduleManager.getModules());
      this.converter.readLastAlts();
      this.converter.clickGuiLoader(this.clickGui.getCategoryButtons());
      this.backgroundShaderUtil = new BackgroundShaderUtil();
      this.settingSorter = new SettingSorter();
      this.cleanClickGui = new CleanClickGui();

      AugustusSounds.currentSound = this.converter.readSound();
      this.blockUtil = new BlockUtil();
      EventManager.register(this.settingSorter);
      EventManager.register(this);

      try {
         ViaMCP.getInstance().start();

      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }
}
