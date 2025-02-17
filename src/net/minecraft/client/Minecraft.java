package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import lombok.Getter;
import lombok.Setter;
import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Module;
import net.augustus.ui.GuiIngameHook;
import net.augustus.utils.EventHandler;
import net.augustus.utils.Logger;
import net.augustus.utils.NameProtectList;
import net.augustus.utils.ViaMCP;
import net.augustus.utils.animation.Delta;
import net.augustus.utils.interfaces.MM;
import net.lenni0451.eventapi.events.IEvent;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.stonks.IntegrityChecks;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import viamcp.utils.AttackOrder;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Minecraft implements IThreadListener, IPlayerUsage, MM {
   long lastFrame = getTime();
   long getTime() { return (Sys.getTime() * 1000) / Sys.getTimerResolution(); }
   public final NameProtectList nameProtectList = new NameProtectList();
   private static final Logger logger = new Logger();
   private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
   public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;
   public static byte[] memoryReserve = new byte[10485760];
   private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
   private final File fileResourcepacks;
   private final PropertyMap twitchDetails;
   private final PropertyMap field_181038_N;
   private ServerData currentServerData;
   private TextureManager renderEngine;
   private static Minecraft theMinecraft;
   public PlayerControllerMP playerController;
   private boolean fullscreen;
   private final boolean enableGLErrorChecking = true;
   private boolean hasCrashed;
   private CrashReport crashReporter;
   public int displayWidth;
   public int displayHeight;
   public long startTime = System.currentTimeMillis();
   private boolean field_181541_X = false;
   public Timer timer = new Timer(20.0F);
   private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
   public WorldClient theWorld;
   public RenderGlobal renderGlobal;
   private RenderManager renderManager;
   private RenderItem renderItem;
   private ItemRenderer itemRenderer;
   public EntityPlayerSP thePlayer;
   private Entity renderViewEntity;
   public Entity pointedEntity;
   public EffectRenderer effectRenderer;
   public Session session;
   private boolean isGamePaused;
   public FontRenderer fontRendererObj;
   public FontRenderer standardGalacticFontRenderer;
   public GuiScreen currentScreen;
   public LoadingScreenRenderer loadingScreen;
   public EntityRenderer entityRenderer;
   public int leftClickCounter;
   private final int tempDisplayWidth;
   private final int tempDisplayHeight;
   private IntegratedServer theIntegratedServer;
   public GuiAchievement guiAchievement;
   public GuiIngame ingameGUI;
   public boolean skipRenderWorld;
   public MovingObjectPosition objectMouseOver;
   public GameSettings gameSettings;
   public MouseHelper mouseHelper;
   public final File mcDataDir;
   private final File fileAssets;
   private final String launchedVersion;
   private final Proxy proxy;
   private ISaveFormat saveLoader;
   private static int debugFPS;
   @Getter
   @Setter
   private int rightClickDelayTimer;
   private String serverName;
   private int serverPort;
   public boolean inGameHasFocus;
   long systemTime = getSystemTime();
   private int joinPlayerCounter;
   public final FrameTimer field_181542_y = new FrameTimer();
   long field_181543_z = System.nanoTime();
   private final boolean jvm64bit;
   private final boolean isDemo;
   private NetworkManager myNetworkManager;
   private boolean integratedServerIsRunning;
   public final Profiler mcProfiler = new Profiler();
   private long debugCrashKeyPressTime = -1L;
   private IReloadableResourceManager mcResourceManager;
   private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
   private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
   private final DefaultResourcePack mcDefaultResourcePack;
   private ResourcePackRepository mcResourcePackRepository;
   private LanguageManager mcLanguageManager;
   private Framebuffer framebufferMc;
   private TextureMap textureMapBlocks;
   private SoundHandler mcSoundHandler;
   private MusicTicker mcMusicTicker;
   private ResourceLocation mojangLogo;
   private final MinecraftSessionService sessionService;
   private SkinManager skinManager;
   private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
   private final long field_175615_aJ = 0L;
   private final Thread mcThread = Thread.currentThread();
   private ModelManager modelManager;
   private BlockRendererDispatcher blockRenderDispatcher;
   volatile boolean running = true;

   public String debug = "";

   public boolean field_175613_B = false;
   public boolean field_175614_C = false;
   public boolean field_175611_D = false;
   public boolean renderChunksMany = true;
   long debugUpdateTime = getSystemTime();
   int fpsCounter;
   long prevFrameTime = -1L;
   private String debugProfilerName = "root";
   private boolean b;
   private boolean bb;

   public Minecraft(GameConfiguration gameConfig) {
      theMinecraft = this;
      this.mcDataDir = gameConfig.folderInfo.mcDataDir;
      this.fileAssets = gameConfig.folderInfo.assetsDir;
      this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
      this.launchedVersion = gameConfig.gameInfo.version;
      this.twitchDetails = gameConfig.userInfo.userProperties;
      this.field_181038_N = gameConfig.userInfo.field_181172_c;
      this.mcDefaultResourcePack = new DefaultResourcePack(
         new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap()
      );
      this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
      this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
      this.session = gameConfig.userInfo.session;
      logger.info("Setting user: " + this.session.getUsername());
      logger.info("(Session ID is " + this.session.getSessionID() + ")");
      this.isDemo = gameConfig.gameInfo.isDemo;
      this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
      this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
      this.tempDisplayWidth = gameConfig.displayInfo.width;
      this.tempDisplayHeight = gameConfig.displayInfo.height;
      this.fullscreen = gameConfig.displayInfo.fullscreen;
      this.jvm64bit = isJvm64bit();
      this.theIntegratedServer = new IntegratedServer(this);
      if (gameConfig.serverInfo.serverName != null) {
         this.serverName = gameConfig.serverInfo.serverName;
         this.serverPort = gameConfig.serverInfo.serverPort;
      }

      ImageIO.setUseCache(false);
      Bootstrap.register();
   }

   public void run() {
      this.running = true;

      try {
         this.startGame();
      } catch (Throwable var11) {
         CrashReport crashreport = CrashReport.makeCrashReport(var11, "Initializing game");
         crashreport.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
         return;
      }

      try {
         while(this.running) {
            if (this.hasCrashed && this.crashReporter != null) {
               this.displayCrashReport(this.crashReporter);
            } else {
               try {
                  this.runGameLoop();
               } catch (OutOfMemoryError var10) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
         }

      } catch (MinecraftError var12) {
      } catch (ReportedException var13) {
         this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
         this.freeMemory();
         logger.fatal("Reported exception thrown!", var13);
         this.displayCrashReport(var13.getCrashReport());
      } catch (Throwable var14) {
         CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
         this.freeMemory();
         logger.fatal("Unreported exception thrown!", var14);
         this.displayCrashReport(crashreport1);
      } finally {
         this.shutdownMinecraftApplet();
      }
   }

   private void startGame() throws LWJGLException, IOException {
      Augustus.getInstance().preStart();
      this.gameSettings = new GameSettings(this, this.mcDataDir);
      this.defaultResourcePacks.add(this.mcDefaultResourcePack);
      this.startTimerHackThread();
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         this.displayWidth = this.gameSettings.overrideWidth;
         this.displayHeight = this.gameSettings.overrideHeight;
      }

      logger.info("LWJGL Version: " + Sys.getVersion());
      this.setWindowIcon();
      this.setInitialDisplayMode();
      this.createDisplay();
      OpenGlHelper.initializeTextures();
      this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
      this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.registerMetadataSerializers();
      this.mcResourcePackRepository = new ResourcePackRepository(
         this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
      );
      this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
      this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
      this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
      this.refreshResources();
      this.renderEngine = new TextureManager(this.mcResourceManager);
      this.mcResourceManager.registerReloadListener(this.renderEngine);
      this.drawSplashScreen(this.renderEngine);
      this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
      this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
      this.mcMusicTicker = new MusicTicker(this);
      this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
      if (this.gameSettings.language != null) {
         this.fontRendererObj.setUnicodeFlag(this.isUnicode());
         this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
      }

      this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
      this.mcResourceManager.registerReloadListener(this.fontRendererObj);
      this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
      this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
      this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
      AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
         @Override
         public String formatString(String p_74535_1_) {
            try {
               return String.format(p_74535_1_, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception var3) {
               return "Error: " + var3.getLocalizedMessage();
            }
         }
      });
      this.mouseHelper = new MouseHelper();
      this.checkGLError("Pre startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0);
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(1029);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.checkGLError("Startup");
      this.textureMapBlocks = new TextureMap("textures");
      this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
      this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
      this.modelManager = new ModelManager(this.textureMapBlocks);
      this.mcResourceManager.registerReloadListener(this.modelManager);
      this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
      this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
      this.itemRenderer = new ItemRenderer(this);
      this.mcResourceManager.registerReloadListener(this.renderItem);
      this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
      this.mcResourceManager.registerReloadListener(this.entityRenderer);
      this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
      this.renderGlobal = new RenderGlobal(this);
      this.mcResourceManager.registerReloadListener(this.renderGlobal);
      this.guiAchievement = new GuiAchievement(this);
      GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
      this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
      this.checkGLError("Post startup");
      this.ingameGUI = new GuiIngameHook(this);
      if (this.serverName != null) {
         this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
      } else {
         this.displayGuiScreen(new GuiMainMenu());
      }

      this.renderEngine.deleteTexture(this.mojangLogo);
      this.mojangLogo = null;
      this.loadingScreen = new LoadingScreenRenderer(this);
      if (this.gameSettings.fullScreen && !this.fullscreen) {
         this.toggleFullscreen();
      }

      try {
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
      } catch (OpenGLException var2) {
         this.gameSettings.enableVsync = false;
         this.gameSettings.saveOptions();
      }

      this.renderGlobal.makeEntityOutlineShader();
      Augustus.getInstance().start();
   }

   private void registerMetadataSerializers() {
      this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
   }
   private void createDisplay() throws LWJGLException {
      Display.setResizable(true);
      Display.setTitle(Augustus.getInstance().getName() + " | " + "b"+Augustus.getInstance().getVersion()+" | "+Augustus.getInstance().getRelease());

      try {
         Display.create(new PixelFormat().withDepthBits(24));
      } catch (LWJGLException var4) {
         logger.error("Couldn't set pixel format", var4);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException ignored) {}

         if (this.fullscreen) {
            this.updateDisplayMode();
         }

         Display.create();
      }

   }


   private void setInitialDisplayMode() throws LWJGLException {
      if (this.fullscreen) {
         Display.setFullscreen(true);
         DisplayMode displaymode = Display.getDisplayMode();
         this.displayWidth = Math.max(1, displaymode.getWidth());
         this.displayHeight = Math.max(1, displaymode.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
      }
   }

   private void setWindowIcon() {
      Util.EnumOS util$enumos = Util.getOSType();
      if (util$enumos != Util.EnumOS.OSX) {
         InputStream inputstream = null;
         InputStream inputstream1 = null;

         try {
            inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
            inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
            if (inputstream != null && inputstream1 != null) {
               Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
            }
         } catch (IOException var8) {
            logger.error("Couldn't set icon", var8);
         } finally {
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(inputstream1);
         }
      }
   }

   private static boolean isJvm64bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      IntegrityChecks.XENZAISFREE();

      for(String s : astring) {
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getFramebuffer() {
      return this.framebufferMc;
   }

   public String getVersion() {
      return this.launchedVersion;
   }

   private void startTimerHackThread() {
      Thread thread = new Thread("Timer hack thread") {
         @Override
         public void run() {
            while(Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }
         }
      };
      thread.setDaemon(true);
      thread.start();
   }

   public void crashed(CrashReport crash) {
      this.hasCrashed = true;
      this.crashReporter = crash;
   }

   public void displayCrashReport(CrashReport crashReportIn) {
      File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
      File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
      Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
      if (crashReportIn.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
         System.exit(-1);
      } else if (crashReportIn.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }
   }

   public boolean isUnicode() {
      return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
   }

   public void refreshResources() {
      List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

      for(ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         list.add(resourcepackrepository$entry.getResourcePack());
      }

      if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
         list.add(this.mcResourcePackRepository.getResourcePackInstance());
      }

      try {
         this.mcResourceManager.reloadResources(list);
      } catch (RuntimeException var4) {
         logger.info("Caught error stitching, removing all assigned resourcepacks", var4);
         list.clear();
         list.addAll(this.defaultResourcePacks);
         this.mcResourcePackRepository.setRepositories(Collections.emptyList());
         this.mcResourceManager.reloadResources(list);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.field_183018_l.clear();
         this.gameSettings.saveOptions();
      }

      this.mcLanguageManager.parseLanguageMetadata(list);
      if (this.renderGlobal != null) {
         this.renderGlobal.loadRenderers();
      }
   }

   private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
      BufferedImage bufferedimage = ImageIO.read(imageStream);
      int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
      ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

      for(int i : aint) {
         bytebuffer.putInt(i << 8 | i >> 24 & 0xFF);
      }

      bytebuffer.flip();
      return bytebuffer;
   }

   private void updateDisplayMode() throws LWJGLException {
      Set<DisplayMode> set = Sets.newHashSet();
      Collections.addAll(set, Display.getAvailableDisplayModes());
      DisplayMode displaymode = Display.getDesktopDisplayMode();
      if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX) {
         for(DisplayMode displaymode1 : macDisplayModes) {
            boolean flag = true;

            for(DisplayMode displaymode2 : set) {
               if (displaymode2.getBitsPerPixel() == 32
                  && displaymode2.getWidth() == displaymode1.getWidth()
                  && displaymode2.getHeight() == displaymode1.getHeight()) {
                  flag = false;
                  break;
               }
            }

            if (!flag) {
               for(DisplayMode displaymode3 : set) {
                  if (displaymode3.getBitsPerPixel() == 32
                     && displaymode3.getWidth() == displaymode1.getWidth() / 2
                     && displaymode3.getHeight() == displaymode1.getHeight() / 2) {
                     displaymode = displaymode3;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(displaymode);
      this.displayWidth = displaymode.getWidth();
      this.displayHeight = displaymode.getHeight();
   }

   private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
      ScaledResolution scaledresolution = new ScaledResolution(this);
      int i = scaledresolution.getScaleFactor();
      Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
      framebuffer.bindFramebuffer(false);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      InputStream inputstream = null;

      try {
         inputstream = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
         this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
         textureManagerInstance.bindTexture(this.mojangLogo);
      } catch (IOException var12) {
         logger.error("Unable to load logo: " + locationMojangPng, var12);
      } finally {
         IOUtils.closeQuietly(inputstream);
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos(0.0, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
      worldrenderer.pos(this.displayWidth, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
      worldrenderer.pos(this.displayWidth, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
      worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
      tessellator.draw();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int j = 256;
      int k = 256;
      this.func_181536_a((scaledresolution.getScaledWidth() - j) / 2, (scaledresolution.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 255, 255, 255);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      framebuffer.unbindFramebuffer();
      framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.updateDisplay();
   }

   public void func_181536_a(
      int p_181536_1_,
      int p_181536_2_,
      int p_181536_3_,
      int p_181536_4_,
      int p_181536_5_,
      int p_181536_6_,
      int p_181536_7_,
      int p_181536_8_,
      int p_181536_9_,
      int p_181536_10_
   ) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos(p_181536_1_, p_181536_2_ + p_181536_6_, 0.0)
         .tex((float)p_181536_3_ * f, (float)(p_181536_4_ + p_181536_6_) * f1)
         .color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_)
         .endVertex();
      worldrenderer.pos(p_181536_1_ + p_181536_5_, p_181536_2_ + p_181536_6_, 0.0)
         .tex((float)(p_181536_3_ + p_181536_5_) * f, (float)(p_181536_4_ + p_181536_6_) * f1)
         .color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_)
         .endVertex();
      worldrenderer.pos(p_181536_1_ + p_181536_5_, p_181536_2_, 0.0)
         .tex((float)(p_181536_3_ + p_181536_5_) * f, (float)p_181536_4_ * f1)
         .color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_)
         .endVertex();
      worldrenderer.pos(p_181536_1_, p_181536_2_, 0.0)
         .tex((float)p_181536_3_ * f, (float)p_181536_4_ * f1)
         .color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_)
         .endVertex();
      Tessellator.getInstance().draw();
   }

   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   public void displayGuiScreen(GuiScreen guiScreenIn) {
      if (this.currentScreen != null) {
         this.currentScreen.onGuiClosed();
      }

      if (guiScreenIn == null && this.theWorld == null) {
         guiScreenIn = new GuiMainMenu();
      } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
         guiScreenIn = new GuiGameOver();
      }

      if (guiScreenIn instanceof GuiMainMenu) {
         this.gameSettings.showDebugInfo = false;
         this.ingameGUI.getChatGUI().clearChatMessages();
      }

      this.currentScreen = guiScreenIn;
      if (guiScreenIn != null) {
         this.setIngameNotInFocus();
         ScaledResolution scaledresolution = new ScaledResolution(this);
         int i = scaledresolution.getScaledWidth();
         int j = scaledresolution.getScaledHeight();
         guiScreenIn.setWorldAndResolution(this, i, j);
         this.skipRenderWorld = false;
      } else {
         this.mcSoundHandler.resumeSounds();
         this.setIngameFocus();
      }

      if (this.thePlayer != null) {
         this.thePlayer.setServerInv(guiScreenIn);
      }
   }

   private void checkGLError(String message) {
      if (this.enableGLErrorChecking) {
         int i = GL11.glGetError();
         if (i != 0) {
            String s = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(i + ": " + s);
         }
      }
   }

   public void shutdownMinecraftApplet() {
      try {
         logger.info("Stopping!");

         try {
            this.loadWorld(null);
         } catch (Throwable var5) {
         }

         this.mcSoundHandler.unloadSounds();
      } finally {
         Display.destroy();
         if (!this.hasCrashed) {
            System.exit(0);
         }
      }

      System.gc();
   }

   private void runGameLoop() throws IOException {
      //--Delta--//
      long currentTime = getTime();
      int deltaTime = (int) (currentTime - lastFrame);
      lastFrame = currentTime;
      Delta.DELTATIME = deltaTime;
      //--Delta--//
      long i = System.nanoTime();
      this.mcProfiler.startSection("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.shutdown();
      }

      if (this.isGamePaused && this.theWorld != null) {
         float f = this.timer.renderPartialTicks;
         this.timer.updateTimer();
         this.timer.renderPartialTicks = f;
      } else {
         this.timer.updateTimer();
      }

      this.mcProfiler.startSection("scheduledExecutables");
      synchronized(this.scheduledTasks) {
         while(!this.scheduledTasks.isEmpty()) {
            Util.func_181617_a(this.scheduledTasks.poll(), logger);
         }
      }

      this.mcProfiler.endSection();
      long l = System.nanoTime();
      this.mcProfiler.startSection("tick");

      /*for(int j = 0; j < this.timer.elapsedTicks; ++j) {
         this.runTick();
      }*/
      for (int j = 0; j < this.timer.elapsedTicks; ++j)
      {
          this.runTick();
      }


      this.mcProfiler.endStartSection("preRenderErrors");
      long i1 = System.nanoTime() - l;
      this.checkGLError("Pre render");
      this.mcProfiler.endStartSection("sound");
      this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
      this.mcProfiler.endSection();
      this.mcProfiler.startSection("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.framebufferMc.bindFramebuffer(true);
      this.mcProfiler.startSection("display");
      GlStateManager.enableTexture2D();
      if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
         this.gameSettings.thirdPersonView = 0;
      }

      this.mcProfiler.endSection();
      if (!this.skipRenderWorld) {
         this.mcProfiler.endStartSection("gameRenderer");
         this.entityRenderer.func_181560_a(this.timer.renderPartialTicks, i);
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         if (!this.mcProfiler.profilingEnabled) {
            this.mcProfiler.clearProfiling();
         }

         this.mcProfiler.profilingEnabled = true;
         this.displayDebugInfo(i1);
      } else {
         this.mcProfiler.profilingEnabled = false;
         this.prevFrameTime = System.nanoTime();
      }

      this.guiAchievement.updateAchievementWindow();
      this.framebufferMc.unbindFramebuffer();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
      GlStateManager.popMatrix();
      this.mcProfiler.startSection("root");
      this.updateDisplay();
      Thread.yield();
      this.mcProfiler.startSection("stream");
      this.mcProfiler.startSection("update");
      this.mcProfiler.endStartSection("submit");
      this.mcProfiler.endSection();
      this.mcProfiler.endSection();
      this.checkGLError("Post render");
      ++this.fpsCounter;
      this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
      long k = System.nanoTime();
      this.field_181542_y.func_181747_a(k - this.field_181543_z);
      this.field_181543_z = k;

      while(getSystemTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format(
            "%d fps (%d chunk update%s) T: %s%s%s%s%s",
            debugFPS,
            RenderChunk.renderChunksUpdated,
            RenderChunk.renderChunksUpdated != 1 ? "s" : "",
            (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate,
            this.gameSettings.enableVsync ? " vsync" : "",
            this.gameSettings.fancyGraphics ? "" : " fast",
            this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"),
            OpenGlHelper.useVbo() ? " vbo" : ""
         );
         RenderChunk.renderChunksUpdated = 0;
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.usageSnooper.addMemoryStatsToSnooper();
         if (!this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.startSnooper();
         }
      }

      if (this.isFramerateLimitBelowMax()) {
         this.mcProfiler.startSection("fpslimit_wait");
         Display.sync(this.getLimitFramerate());
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
   }

   public void updateDisplay() {
      this.mcProfiler.startSection("display_update");
      Display.update();
      this.mcProfiler.endSection();
      this.checkWindowResize();
   }

   protected void checkWindowResize() {
      if (!this.fullscreen && Display.wasResized()) {
         int i = this.displayWidth;
         int j = this.displayHeight;
         this.displayWidth = Display.getWidth();
         this.displayHeight = Display.getHeight();
         if (this.displayWidth != i || this.displayHeight != j) {
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }

            this.resize(this.displayWidth, this.displayHeight);
         }
      }
   }

   public int getLimitFramerate() {
      return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
   }

   public boolean isFramerateLimitBelowMax() {
      return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.deleteAllDisplayLists();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.loadWorld(null);
      } catch (Throwable var2) {
      }

      System.gc();
   }

   private void updateDebugProfilerName(int keyCount) {
      List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
      if (list != null && !list.isEmpty()) {
         Profiler.Result profiler$result = list.remove(0);
         if (keyCount == 0) {
            if (profiler$result.field_76331_c.length() > 0) {
               int i = this.debugProfilerName.lastIndexOf(".");
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            --keyCount;
            if (keyCount < list.size() && !list.get(keyCount).field_76331_c.equals("unspecified")) {
               if (this.debugProfilerName.length() > 0) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + list.get(keyCount).field_76331_c;
            }
         }
      }
   }

   private void displayDebugInfo(long elapsedTicksTime) {
      if (this.mcProfiler.profilingEnabled) {
         List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
         Profiler.Result profiler$result = list.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         int i = 160;
         int j = this.displayWidth - i - 10;
         int k = this.displayHeight - i * 2;
         GlStateManager.enableBlend();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldrenderer.pos((float)j - (float)i * 1.1F, (float)k - (float)i * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         worldrenderer.pos((float)j - (float)i * 1.1F, k + i * 2, 0.0).color(200, 0, 0, 0).endVertex();
         worldrenderer.pos((float)j + (float)i * 1.1F, k + i * 2, 0.0).color(200, 0, 0, 0).endVertex();
         worldrenderer.pos((float)j + (float)i * 1.1F, (float)k - (float)i * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         tessellator.draw();
         GlStateManager.disableBlend();
         double d0 = 0.0;

         for(int l = 0; l < list.size(); ++l) {
            Profiler.Result profiler$result1 = list.get(l);
            int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0) + 1;
            worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int j1 = profiler$result1.func_76329_a();
            int k1 = j1 >> 16 & 0xFF;
            int l1 = j1 >> 8 & 0xFF;
            int i2 = j1 & 0xFF;
            worldrenderer.pos(j, k, 0.0).color(k1, l1, i2, 255).endVertex();

            for(int j2 = i1; j2 >= 0; --j2) {
               float f = (float)((d0 + profiler$result1.field_76332_a * (double)j2 / (double)i1) * Math.PI * 2.0 / 100.0);
               float f1 = MathHelper.sin(f) * (float)i;
               float f2 = MathHelper.cos(f) * (float)i * 0.5F;
               worldrenderer.pos((float)j + f1, (float)k - f2, 0.0).color(k1, l1, i2, 255).endVertex();
            }

            tessellator.draw();
            worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for(int i3 = i1; i3 >= 0; --i3) {
               float f3 = (float)((d0 + profiler$result1.field_76332_a * (double)i3 / (double)i1) * Math.PI * 2.0 / 100.0);
               float f4 = MathHelper.sin(f3) * (float)i;
               float f5 = MathHelper.cos(f3) * (float)i * 0.5F;
               worldrenderer.pos((float)j + f4, (float)k - f5, 0.0).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
               worldrenderer.pos((float)j + f4, (float)k - f5 + 10.0F, 0.0).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
            }

            tessellator.draw();
            d0 += profiler$result1.field_76332_a;
         }

         DecimalFormat decimalformat = new DecimalFormat("##0.00");
         GlStateManager.enableTexture2D();
         String s = "";
         if (!profiler$result.field_76331_c.equals("unspecified")) {
            s = s + "[0] ";
         }

         if (profiler$result.field_76331_c.length() == 0) {
            s = s + "ROOT ";
         } else {
            s = s + profiler$result.field_76331_c + " ";
         }

         int l2 = 16777215;
         this.fontRendererObj.drawStringWithShadow(s, (float)(j - i), (float)(k - i / 2 - 16), l2);
         this.fontRendererObj
            .drawStringWithShadow(
               s = decimalformat.format(profiler$result.field_76330_b) + "%",
               (float)(j + i - this.fontRendererObj.getStringWidth(s)),
               (float)(k - i / 2 - 16),
               l2
            );

         for(int k2 = 0; k2 < list.size(); ++k2) {
            Profiler.Result profiler$result2 = list.get(k2);
            String s1 = "";
            if (profiler$result2.field_76331_c.equals("unspecified")) {
               s1 = s1 + "[?] ";
            } else {
               s1 = s1 + "[" + (k2 + 1) + "] ";
            }

            s1 = s1 + profiler$result2.field_76331_c;
            this.fontRendererObj.drawStringWithShadow(s1, (float)(j - i), (float)(k + i / 2 + k2 * 8 + 20), profiler$result2.func_76329_a());
            this.fontRendererObj
               .drawStringWithShadow(
                  s1 = decimalformat.format(profiler$result2.field_76332_a) + "%",
                  (float)(j + i - 50 - this.fontRendererObj.getStringWidth(s1)),
                  (float)(k + i / 2 + k2 * 8 + 20),
                  profiler$result2.func_76329_a()
               );
            this.fontRendererObj
               .drawStringWithShadow(
                  s1 = decimalformat.format(profiler$result2.field_76330_b) + "%",
                  (float)(j + i - this.fontRendererObj.getStringWidth(s1)),
                  (float)(k + i / 2 + k2 * 8 + 20),
                  profiler$result2.func_76329_a()
               );
         }
      }
   }

   public void shutdown() {
      this.running = false;
   }

   public void setIngameFocus() {
      if (Display.isActive() && !this.inGameHasFocus) {
         this.inGameHasFocus = true;
         this.mouseHelper.grabMouseCursor();
         this.displayGuiScreen(null);
         this.leftClickCounter = 10000;
      }
   }

   public void setIngameNotInFocus() {
      if (this.inGameHasFocus) {
         KeyBinding.unPressAllKeys();
         this.inGameHasFocus = false;
         this.mouseHelper.ungrabMouseCursor();
      }
   }

   public void displayInGameMenu() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
            this.mcSoundHandler.pauseSounds();
         }
      }
   }

   public void sendClickBlockToController(boolean leftClick) {
      if (!leftClick) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
         if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air
               && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
               this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
               AttackOrder.sendConditionalSwing(this.objectMouseOver);
            }
         } else {
            this.playerController.resetBlockRemoving();
         }
      }
   }

   public void sendClickBlockToControllerCustom(boolean leftClick, MovingObjectPosition movingObjectPosition, BlockPos blockpos, int id) {
      this.b = false;
      int i = this.thePlayer.inventory.currentItem;
      this.thePlayer.inventory.currentItem = id;
      if (!leftClick) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
         if (leftClick && movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air
               && this.playerController.onPlayerDamageBlockCustom(blockpos, movingObjectPosition.sideHit, id)) {
               this.effectRenderer.addBlockHitEffects(blockpos, movingObjectPosition.sideHit);
               AttackOrder.sendConditionalSwing(this.objectMouseOver);
               this.b = true;
            }
         } else {
            this.playerController.resetBlockRemoving(!this.b && this.bb);
            this.thePlayer.resetCooldown();
         }
      }

      this.thePlayer.inventory.currentItem = i;
      this.b = this.bb;
   }

   public void clickMouse() {
      if (Augustus.getInstance().getModuleManager().fixes.isToggled() && Augustus.getInstance().getModuleManager().fixes.hitDelayFix.getBoolean()) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0) {
         if (!ViaMCP.isActive()) {
            AttackOrder.sendConditionalSwing(this.objectMouseOver);
         }

         if (this.objectMouseOver == null) {
            logger.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }
         } else {
            switch(this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  AttackOrder.sendFixedAttack(this.thePlayer, MovingObjectPosition.entityHit);
                  break;
               case BLOCK:
                  BlockPos blockpos = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                     this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                     break;
                  }
               case MISS:
               default:
                  if (this.playerController.isNotCreative()) {
                     this.leftClickCounter = 10;
                  }

                  this.thePlayer.resetCooldown();
            }

            if (ViaMCP.isActive()) {
               AttackOrder.sendConditionalSwing(this.objectMouseOver);
            }
         }
      }
   }

   public void rightClickMouse() {
      if (!this.playerController.func_181040_m()) {
         this.rightClickDelayTimer = 4;
         boolean flag = true;
         ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();
         if (this.objectMouseOver == null) {
            logger.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch(this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (this.playerController.func_178894_a(this.thePlayer, MovingObjectPosition.entityHit, this.objectMouseOver)) {
                     flag = false;
                  } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, MovingObjectPosition.entityHit)) {
                     flag = false;
                  }
                  break;
               case BLOCK:
                  BlockPos blockpos = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                     int i = itemstack != null ? itemstack.stackSize : 0;
                     if (this.playerController
                        .onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                        flag = false;
                        AttackOrder.sendConditionalSwing(this.objectMouseOver);
                     }

                     if (itemstack == null) {
                        return;
                     }

                     if (itemstack.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                     } else if (itemstack.stackSize != i || this.playerController.isInCreativeMode()) {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                     }
                  }
            }
         }

         if (flag) {
            ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();
            if (itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
               this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
         }
      }
   }

   public void toggleFullscreen() {
      try {
         this.fullscreen = !this.fullscreen;
         this.gameSettings.fullScreen = this.fullscreen;
         if (this.fullscreen) {
            this.updateDisplayMode();
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         } else {
            Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
            this.displayWidth = this.tempDisplayWidth;
            this.displayHeight = this.tempDisplayHeight;
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         }

         if (this.currentScreen != null) {
            this.resize(this.displayWidth, this.displayHeight);
         } else {
            this.updateFramebufferSize();
         }

         Display.setFullscreen(this.fullscreen);
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
         this.updateDisplay();
      } catch (Exception var2) {
         logger.error("Couldn't toggle fullscreen", var2);
      }
   }

   private void resize(int width, int height) {
      this.displayWidth = Math.max(1, width);
      this.displayHeight = Math.max(1, height);
      if (this.currentScreen != null) {
         ScaledResolution scaledresolution = new ScaledResolution(this);
         this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
      }

      this.loadingScreen = new LoadingScreenRenderer(this);
      this.updateFramebufferSize();
   }

   private void updateFramebufferSize() {
      this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
      if (this.entityRenderer != null) {
         this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
      }
   }

   public MusicTicker func_181535_r() {
      return this.mcMusicTicker;
   }

   public void runTick() throws IOException {
      if (this.rightClickDelayTimer > 0) {
         --this.rightClickDelayTimer;
      }

      this.mcProfiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.updateTick();
      }

      this.mcProfiler.endSection();
      if (this.theWorld != null && this.thePlayer != null) {
         EventEarlyTick eventEarlyTick = new EventEarlyTick();
         EventHandler.call(eventEarlyTick);
      }

      this.entityRenderer.getMouseOver(1.0F);
      if (this.theWorld != null && this.thePlayer != null) {
         EventPostMouseOver eventPostMouseOver = new EventPostMouseOver();
         EventHandler.call(eventPostMouseOver);
      }

      this.mcProfiler.startSection("gameMode");
      if (!this.isGamePaused && this.theWorld != null) {
         this.playerController.updateController();
      }

      this.mcProfiler.endStartSection("textures");
      if (!this.isGamePaused) {
         this.renderEngine.tick();
      }

      if (this.currentScreen == null && this.thePlayer != null) {
         if (this.thePlayer.getHealth() <= 0.0F) {
            this.displayGuiScreen(null);
         } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
            this.displayGuiScreen(new GuiSleepMP());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
         this.displayGuiScreen(null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         try {
            this.currentScreen.handleInput();
         } catch (Throwable var7) {
            CrashReport crashreport = CrashReport.makeCrashReport(var7, "Updating screen events");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
            crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>() {
               public String call() throws Exception {
                  return Minecraft.this.currentScreen.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(crashreport);
         }

         if (this.currentScreen != null) {
            try {
               this.currentScreen.updateScreen();
            } catch (Throwable var6) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(var6, "Ticking screen");
               CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
               crashreportcategory1.addCrashSectionCallable("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return Minecraft.this.currentScreen.getClass().getCanonicalName();
                  }
               });
               throw new ReportedException(crashreport1);
            }
         }
      }
      EventHandleGui eventHandleGui = new EventHandleGui();
      EventManager.call(eventHandleGui);
      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.mcProfiler.endStartSection("mouse");

         while(Mouse.next()) {
            int i = Mouse.getEventButton();
            KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
               if (this.thePlayer.isSpectator() && i == 2) {
                  this.ingameGUI.getSpectatorGui().func_175261_b();
               } else {
                  KeyBinding.onTick(i - 100);
               }
            }

            long i1 = getSystemTime() - this.systemTime;
            if (i1 <= 200L) {
               int j = Mouse.getEventDWheel();
               if (j != 0) {
                  if (this.thePlayer.isSpectator()) {
                     j = j < 0 ? -1 : 1;
                     if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                        this.ingameGUI.getSpectatorGui().func_175259_b(-j);
                     } else {
                        float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float)j * 0.005F, 0.0F, 0.2F);
                        this.thePlayer.capabilities.setFlySpeed(f);
                     }
                  } else {
                     this.thePlayer.inventory.changeCurrentItem(j);
                  }
               }

               if (this.currentScreen == null) {
                  if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                     this.setIngameFocus();
                  }
               } else if (this.currentScreen != null) {
                  this.currentScreen.handleMouseInput();
               }
            }
         }

         if (this.leftClickCounter > 0) {
            --this.leftClickCounter;
         }

         this.mcProfiler.endStartSection("keyboard");

         while(Keyboard.next()) {
            int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());
            if (Keyboard.getEventKeyState()) {
               KeyBinding.onTick(k);
            }

            if (this.debugCrashKeyPressTime > 0L) {
               if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                  throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
               }

               if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                  this.debugCrashKeyPressTime = -1L;
               }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
               this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();
            if (Keyboard.getEventKeyState()) {
               if (k == 62 && this.entityRenderer != null) {
                  this.entityRenderer.switchUseShader();
               }

               if (this.currentScreen != null) {
                  this.currentScreen.handleKeyboardInput();
               } else {
                  EventKeyboard eventKeyboard = new EventKeyboard(k);
                  EventHandler.call(eventKeyboard);

                  for(Module module : Augustus.getInstance().getModuleManager().getModules()) {
                     if (module.getKey() == k) {
                        module.toggle();
                     }
                  }

                  if (k == 1) {
                     this.displayInGameMenu();
                  }

                  if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                     this.ingameGUI.getChatGUI().clearChatMessages();
                  }

                  if (k == 31 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (k == 17 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 18 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 47 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 38 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 22 && Keyboard.isKeyDown(61)) {
                  }

                  if (k == 20 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (k == 33 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                  }

                  if (k == 30 && Keyboard.isKeyDown(61)) {
                     this.renderGlobal.loadRenderers();
                  }

                  if (k == 35 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                     this.gameSettings.saveOptions();
                  }

                  if (k == 48 && Keyboard.isKeyDown(61)) {
                     this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                  }

                  if (k == 25 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                     this.gameSettings.saveOptions();
                  }

                  if (k == 59) {
                     this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                  }

                  if (k == 61) {
                     this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                     this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                     this.gameSettings.field_181657_aC = GuiScreen.isAltKeyDown();
                  }

                  if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                     ++this.gameSettings.thirdPersonView;
                     if (this.gameSettings.thirdPersonView > 2) {
                        this.gameSettings.thirdPersonView = 0;
                     }

                     if (this.gameSettings.thirdPersonView == 0) {
                        this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                     } else if (this.gameSettings.thirdPersonView == 1) {
                        this.entityRenderer.loadEntityShader(null);
                     }

                     this.renderGlobal.setDisplayListEntitiesDirty();
                  }

                  if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                     this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                  }
               }

               if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                  if (k == 11) {
                     this.updateDebugProfilerName(0);
                  }

                  for(int j1 = 0; j1 < 9; ++j1) {
                     if (k == 2 + j1) {
                        this.updateDebugProfilerName(j1 + 1);
                     }
                  }
               }
            }
         }

         for(int l = 0; l < 9; ++l) {
            if (this.gameSettings.keyBindsHotbar[l].isPressed()) {
               if (this.thePlayer.isSpectator()) {
                  this.ingameGUI.getSpectatorGui().func_175260_a(l);
               } else {
                  this.thePlayer.inventory.currentItem = l;
               }
            }
         }

         boolean flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

         while(this.gameSettings.keyBindInventory.isPressed()) {
            if (this.playerController.isRidingHorse()) {
               this.thePlayer.sendHorseInventory();
            } else {
               this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
               this.displayGuiScreen(new GuiInventory(this.thePlayer));
            }
         }

         while(this.gameSettings.keyBindDrop.isPressed()) {
            if (!this.thePlayer.isSpectator()) {
               this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }
         }

         while(this.gameSettings.keyBindChat.isPressed() && flag) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
            this.displayGuiScreen(new GuiChat("/"));
         }
         if (this.currentScreen == null && Keyboard.isKeyDown(Keyboard.KEY_PERIOD) && flag) {
            this.displayGuiScreen(new GuiChat("."));
         }


         EventClick eventClick = new EventClick(this.thePlayer.inventory.currentItem);
         int oldSlot = this.thePlayer.inventory.currentItem;
         eventClick.setShouldRightClick(true);
         EventHandler.call(eventClick);
         this.thePlayer.inventory.currentItem = eventClick.getSlot();
         if (!eventClick.isCancelled()) {
            if (this.thePlayer.isUsingItem()) {
               if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
                  this.playerController.onStoppedUsingItem(this.thePlayer);
               }

               while(this.gameSettings.keyBindAttack.isPressed()) {
               }

               while(this.gameSettings.keyBindUseItem.isPressed()) {
               }

               while(this.gameSettings.keyBindPickBlock.isPressed()) {
               }
            } else {
               while(this.gameSettings.keyBindAttack.isPressed()) {
                  this.clickMouse();
               }

               if (eventClick.isShouldRightClick()) {
                  while(this.gameSettings.keyBindUseItem.isPressed()) {
                     this.rightClickMouse();
                  }
               }

               while(this.gameSettings.keyBindPickBlock.isPressed()) {
                  this.middleClickMouse();
               }
            }

            if (eventClick.isShouldRightClick()
               && this.gameSettings.keyBindUseItem.isKeyDown()
               && this.rightClickDelayTimer == 0
               && !this.thePlayer.isUsingItem()) {
               this.rightClickMouse();
            }

            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
         }

         this.thePlayer.inventory.currentItem = oldSlot;
      }

      if (this.theWorld != null) {
         if (this.thePlayer != null) {
            EventTick eventTick = new EventTick();
            EventHandler.call(eventTick);
            ++this.joinPlayerCounter;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.theWorld.joinEntityInSurroundings(this.thePlayer);
            }
         }

         this.mcProfiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.entityRenderer.updateRenderer();
         }

         this.mcProfiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.renderGlobal.updateClouds();
         }

         this.mcProfiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.theWorld.getLastLightningBolt() > 0) {
               this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
            }

            this.theWorld.updateEntities();
         }
      } else if (this.entityRenderer.isShaderActive()) {
         this.entityRenderer.func_181022_b();
      }

      if (!this.isGamePaused) {
         this.mcMusicTicker.update();
         this.mcSoundHandler.update();
      }

      if (this.theWorld != null) {
         if (!this.isGamePaused) {
            this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

            try {
               this.theWorld.tick();
            } catch (Throwable var8) {
               CrashReport crashreport2 = CrashReport.makeCrashReport(var8, "Exception in world tick");
               if (this.theWorld == null) {
                  CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                  crashreportcategory2.addCrashSection("Problem", "Level is null!");
               } else {
                  this.theWorld.addWorldInfoToCrashReport(crashreport2);
               }

               throw new ReportedException(crashreport2);
            }
         }

         this.mcProfiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.theWorld != null) {
            this.theWorld
               .doVoidFogParticles(
                  MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)
               );
         }

         this.mcProfiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.effectRenderer.updateEffects();
         }
      } else if (this.myNetworkManager != null) {
         this.mcProfiler.endStartSection("pendingConnection");
         this.myNetworkManager.processReceivedPackets();
      }

      if (this.theWorld != null && this.thePlayer != null) {
         EventLateTick eventLateTick = new EventLateTick();
         EventHandler.call(eventLateTick);
      }

      this.mcProfiler.endSection();
      this.systemTime = getSystemTime();
   }

   public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
      this.loadWorld(null);
      System.gc();
      ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      if (worldinfo == null && worldSettingsIn != null) {
         worldinfo = new WorldInfo(worldSettingsIn, folderName);
         isavehandler.saveWorldInfo(worldinfo);
      }

      if (worldSettingsIn == null) {
         worldSettingsIn = new WorldSettings(worldinfo);
      }

      try {
         this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
         this.theIntegratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable var10) {
         CrashReport crashreport = CrashReport.makeCrashReport(var10, "Starting integrated server");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
         crashreportcategory.addCrashSection("Level ID", folderName);
         crashreportcategory.addCrashSection("Level Name", worldName);
         throw new ReportedException(crashreport);
      }

      this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

      while(!this.theIntegratedServer.serverIsInRunLoop()) {
         String s = this.theIntegratedServer.getUserMessage();
         if (s != null) {
            this.loadingScreen.displayLoadingString(I18n.format(s));
         } else {
            this.loadingScreen.displayLoadingString("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var9) {
         }
      }

      this.displayGuiScreen(null);
      SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
      networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null));
      networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
      networkmanager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
      this.myNetworkManager = networkmanager;
   }

   public void loadWorld(WorldClient worldClientIn) {
      this.loadWorld(worldClientIn, "");
   }

   public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
      EventWorld eventWorld = new EventWorld(worldClientIn);
      EventHandler.call(eventWorld);
      if (worldClientIn == null) {
         NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();
         if (nethandlerplayclient != null) {
            nethandlerplayclient.cleanup();
         }

         if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
            this.theIntegratedServer.initiateShutdown();
            this.theIntegratedServer.setStaticInstance();
         }

         this.theIntegratedServer = null;
         this.guiAchievement.clearAchievements();
         this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
      }

      this.renderViewEntity = null;
      this.myNetworkManager = null;
      if (this.loadingScreen != null) {
         this.loadingScreen.resetProgressAndMessage(loadingMessage);
         this.loadingScreen.displayLoadingString("");
      }

      if (worldClientIn == null && this.theWorld != null) {
         this.mcResourcePackRepository.func_148529_f();
         this.ingameGUI.func_181029_i();
         this.setServerData(null);
         this.integratedServerIsRunning = false;
      }

      this.mcSoundHandler.stopSounds();
      this.theWorld = worldClientIn;
      if (worldClientIn != null) {
         if (this.renderGlobal != null) {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
         }

         if (this.effectRenderer != null) {
            this.effectRenderer.clearEffects(worldClientIn);
         }

         if (this.thePlayer == null) {
            this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
            this.playerController.flipPlayer(this.thePlayer);
         }

         this.thePlayer.preparePlayerToSpawn();
         worldClientIn.spawnEntityInWorld(this.thePlayer);
         this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.thePlayer);
         this.renderViewEntity = this.thePlayer;
      } else {
         this.saveLoader.flushCache();
         this.thePlayer = null;
      }

      System.gc();
      this.systemTime = 0L;
   }

   public void setDimensionAndSpawnPlayer(int dimension) {
      this.theWorld.setInitialSpawnLocation();
      this.theWorld.removeAllEntities();
      int i = 0;
      String s = null;
      if (this.thePlayer != null) {
         i = this.thePlayer.getEntityId();
         this.theWorld.removeEntity(this.thePlayer);
         s = this.thePlayer.getClientBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP entityplayersp = this.thePlayer;
      this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
      this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
      this.thePlayer.dimension = dimension;
      this.renderViewEntity = this.thePlayer;
      this.thePlayer.preparePlayerToSpawn();
      this.thePlayer.setClientBrand(s);
      this.theWorld.spawnEntityInWorld(this.thePlayer);
      this.playerController.flipPlayer(this.thePlayer);
      this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.thePlayer.setEntityId(i);
      this.playerController.setPlayerCapabilities(this.thePlayer);
      this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen(null);
      }
   }

   public final boolean isDemo() {
      return this.isDemo;
   }

   public NetHandlerPlayClient getNetHandler() {
      return this.thePlayer != null ? this.thePlayer.sendQueue : null;
   }

   public static boolean isGuiEnabled() {
      return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
   }

   public static boolean isFancyGraphicsEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
   }

   public void middleClickMouse() {
      if (this.objectMouseOver != null) {
         EventMidClick eventMidClick = new EventMidClick();
         EventHandler.call(eventMidClick);
         boolean flag = this.thePlayer.capabilities.isCreativeMode;
         int i = 0;
         boolean flag1 = false;
         TileEntity tileentity = null;
         Item item;
         if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = this.objectMouseOver.getBlockPos();
            Block block = this.theWorld.getBlockState(blockpos).getBlock();
            if (block.getMaterial() == Material.air) {
               return;
            }

            item = block.getItem(this.theWorld, blockpos);
            if (item == null) {
               return;
            }

            if (flag && GuiScreen.isCtrlKeyDown()) {
               tileentity = this.theWorld.getTileEntity(blockpos);
            }

            Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
            i = block1.getDamageValue(this.theWorld, blockpos);
            flag1 = item.getHasSubtypes();
         } else {
            if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || MovingObjectPosition.entityHit == null || !flag) {
               return;
            }

            if (MovingObjectPosition.entityHit instanceof EntityPainting) {
               item = Items.painting;
            } else if (MovingObjectPosition.entityHit instanceof EntityLeashKnot) {
               item = Items.lead;
            } else if (MovingObjectPosition.entityHit instanceof EntityItemFrame) {
               EntityItemFrame entityitemframe = (EntityItemFrame) MovingObjectPosition.entityHit;
               ItemStack itemstack = entityitemframe.getDisplayedItem();
               if (itemstack == null) {
                  item = Items.item_frame;
               } else {
                  item = itemstack.getItem();
                  i = itemstack.getMetadata();
                  flag1 = true;
               }
            } else if (MovingObjectPosition.entityHit instanceof EntityMinecart) {
               EntityMinecart entityminecart = (EntityMinecart) MovingObjectPosition.entityHit;
               switch(entityminecart.getMinecartType()) {
                  case FURNACE:
                     item = Items.furnace_minecart;
                     break;
                  case CHEST:
                     item = Items.chest_minecart;
                     break;
                  case TNT:
                     item = Items.tnt_minecart;
                     break;
                  case HOPPER:
                     item = Items.hopper_minecart;
                     break;
                  case COMMAND_BLOCK:
                     item = Items.command_block_minecart;
                     break;
                  default:
                     item = Items.minecart;
               }
            } else if (MovingObjectPosition.entityHit instanceof EntityBoat) {
               item = Items.boat;
            } else if (MovingObjectPosition.entityHit instanceof EntityArmorStand) {
               item = Items.armor_stand;
            } else {
               item = Items.spawn_egg;
               i = EntityList.getEntityID(MovingObjectPosition.entityHit);
               flag1 = true;
               if (!EntityList.entityEggs.containsKey(i)) {
                  return;
               }
            }
         }

         InventoryPlayer inventoryplayer = this.thePlayer.inventory;
         if (tileentity == null) {
            inventoryplayer.setCurrentItem(item, i, flag1, flag);
         } else {
            ItemStack itemstack1 = this.func_181036_a(item, i, tileentity);
            inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
         }

         if (flag) {
            int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
            this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
         }
      }
   }

   private ItemStack func_181036_a(Item p_181036_1_, int p_181036_2_, TileEntity p_181036_3_) {
      ItemStack itemstack = new ItemStack(p_181036_1_, 1, p_181036_2_);
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      p_181036_3_.writeToNBT(nbttagcompound);
      if (p_181036_1_ == Items.skull && nbttagcompound.hasKey("Owner")) {
         NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
         NBTTagCompound nbttagcompound3 = new NBTTagCompound();
         nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
         itemstack.setTagCompound(nbttagcompound3);
         return itemstack;
      } else {
         itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         NBTTagList nbttaglist = new NBTTagList();
         nbttaglist.appendTag(new NBTTagString("(+NBT)"));
         nbttagcompound1.setTag("Lore", nbttaglist);
         itemstack.setTagInfo("display", nbttagcompound1);
         return itemstack;
      }
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
      theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.launchedVersion;
         }
      });
      theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
         public String call() {
            return Sys.getVersion();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }
      });
      theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getLogText();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
         public String call() {
            return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
         }
      });
      theCrash.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String s = Augustus.getInstance().getName();
                  return !s.equals("vanilla")
                     ? "Definitely; Client brand changed to '" + s + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and client brand is untouched."
                     );
               }
            }
         );
      theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Client (map_client.txt)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
         public String call() throws Exception {
            StringBuilder stringbuilder = new StringBuilder();

            for(Object s : Minecraft.this.gameSettings.resourcePacks) {
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(s);
               if (Minecraft.this.gameSettings.field_183018_l.contains(s)) {
                  stringbuilder.append(" (incompatible)");
               }
            }

            return stringbuilder.toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
         public String call() {
            return OpenGlHelper.func_183029_j();
         }
      });
      if (this.theWorld != null) {
         this.theWorld.addWorldInfoToCrashReport(theCrash);
      }

      return theCrash;
   }

   public static Minecraft getMinecraft() {
      return theMinecraft;
   }

   public ListenableFuture<Object> scheduleResourcesRefresh() {
      return this.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            Minecraft.this.refreshResources();
         }
      });
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addClientStat("fps", debugFPS);
      playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
      playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
      playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      playerSnooper.addClientStat("current_action", this.func_181538_aA());
      String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      playerSnooper.addClientStat("endianness", s);
      playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
      int i = 0;

      for(ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName());
      }

      if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
         playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
      }
   }

   private String func_181538_aA() {
      return this.theIntegratedServer != null
         ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer")
         : (this.currentServerData != null ? (this.currentServerData.func_181041_d() ? "playing_lan" : "multiplayer") : "out_of_game");
   }

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
      playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
      playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
      playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
      ContextCapabilities contextcapabilities = GLContext.getCapabilities();
      playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", contextcapabilities.GL_ARB_arrays_of_arrays);
      playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", contextcapabilities.GL_ARB_base_instance);
      playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", contextcapabilities.GL_ARB_blend_func_extended);
      playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", contextcapabilities.GL_ARB_clear_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", contextcapabilities.GL_ARB_color_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", contextcapabilities.GL_ARB_compatibility);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", contextcapabilities.GL_ARB_compressed_texture_pixel_storage);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
      playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", contextcapabilities.GL_ARB_depth_clamp);
      playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", contextcapabilities.GL_ARB_depth_texture);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", contextcapabilities.GL_ARB_draw_buffers);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", contextcapabilities.GL_ARB_draw_buffers_blend);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", contextcapabilities.GL_ARB_draw_elements_base_vertex);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", contextcapabilities.GL_ARB_draw_indirect);
      playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", contextcapabilities.GL_ARB_draw_instanced);
      playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", contextcapabilities.GL_ARB_explicit_attrib_location);
      playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", contextcapabilities.GL_ARB_explicit_uniform_location);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", contextcapabilities.GL_ARB_fragment_layer_viewport);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", contextcapabilities.GL_ARB_fragment_program);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", contextcapabilities.GL_ARB_fragment_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", contextcapabilities.GL_ARB_fragment_program_shadow);
      playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", contextcapabilities.GL_ARB_framebuffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", contextcapabilities.GL_ARB_framebuffer_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", contextcapabilities.GL_ARB_geometry_shader4);
      playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", contextcapabilities.GL_ARB_gpu_shader5);
      playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", contextcapabilities.GL_ARB_half_float_pixel);
      playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", contextcapabilities.GL_ARB_half_float_vertex);
      playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", contextcapabilities.GL_ARB_instanced_arrays);
      playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", contextcapabilities.GL_ARB_map_buffer_alignment);
      playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", contextcapabilities.GL_ARB_map_buffer_range);
      playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", contextcapabilities.GL_ARB_multisample);
      playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", contextcapabilities.GL_ARB_multitexture);
      playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", contextcapabilities.GL_ARB_occlusion_query2);
      playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", contextcapabilities.GL_ARB_pixel_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", contextcapabilities.GL_ARB_seamless_cube_map);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", contextcapabilities.GL_ARB_shader_objects);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", contextcapabilities.GL_ARB_shader_stencil_export);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", contextcapabilities.GL_ARB_shader_texture_lod);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", contextcapabilities.GL_ARB_shadow);
      playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", contextcapabilities.GL_ARB_shadow_ambient);
      playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", contextcapabilities.GL_ARB_stencil_texturing);
      playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", contextcapabilities.GL_ARB_sync);
      playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", contextcapabilities.GL_ARB_tessellation_shader);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", contextcapabilities.GL_ARB_texture_border_clamp);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", contextcapabilities.GL_ARB_texture_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", contextcapabilities.GL_ARB_texture_cube_map);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", contextcapabilities.GL_ARB_texture_cube_map_array);
      playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", contextcapabilities.GL_ARB_texture_non_power_of_two);
      playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", contextcapabilities.GL_ARB_uniform_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", contextcapabilities.GL_ARB_vertex_blend);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", contextcapabilities.GL_ARB_vertex_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", contextcapabilities.GL_ARB_vertex_program);
      playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", contextcapabilities.GL_ARB_vertex_shader);
      playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", contextcapabilities.GL_EXT_bindable_uniform);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", contextcapabilities.GL_EXT_blend_equation_separate);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", contextcapabilities.GL_EXT_blend_func_separate);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", contextcapabilities.GL_EXT_blend_minmax);
      playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", contextcapabilities.GL_EXT_blend_subtract);
      playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", contextcapabilities.GL_EXT_draw_instanced);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", contextcapabilities.GL_EXT_framebuffer_multisample);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", contextcapabilities.GL_EXT_framebuffer_object);
      playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", contextcapabilities.GL_EXT_framebuffer_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", contextcapabilities.GL_EXT_geometry_shader4);
      playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", contextcapabilities.GL_EXT_gpu_program_parameters);
      playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", contextcapabilities.GL_EXT_gpu_shader4);
      playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", contextcapabilities.GL_EXT_multi_draw_arrays);
      playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", contextcapabilities.GL_EXT_packed_depth_stencil);
      playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", contextcapabilities.GL_EXT_paletted_texture);
      playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", contextcapabilities.GL_EXT_rescale_normal);
      playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", contextcapabilities.GL_EXT_separate_shader_objects);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", contextcapabilities.GL_EXT_shader_image_load_store);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", contextcapabilities.GL_EXT_shadow_funcs);
      playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", contextcapabilities.GL_EXT_shared_texture_palette);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", contextcapabilities.GL_EXT_stencil_clear_tag);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", contextcapabilities.GL_EXT_stencil_two_side);
      playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", contextcapabilities.GL_EXT_stencil_wrap);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", contextcapabilities.GL_EXT_texture_3d);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", contextcapabilities.GL_EXT_texture_array);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", contextcapabilities.GL_EXT_texture_buffer_object);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", contextcapabilities.GL_EXT_texture_integer);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", contextcapabilities.GL_EXT_texture_lod_bias);
      playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", contextcapabilities.GL_EXT_texture_sRGB);
      playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", contextcapabilities.GL_EXT_vertex_shader);
      playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", contextcapabilities.GL_EXT_vertex_weighting);
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      playerSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
   }

   public static int getGLMaximumTextureSize() {
      for(int i = 16384; i > 0; i >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
         int j = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (j != 0) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.gameSettings.snooperEnabled;
   }

   public void setServerData(ServerData serverDataIn) {
      this.currentServerData = serverDataIn;
   }

   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.theIntegratedServer != null;
   }

   public IntegratedServer getIntegratedServer() {
      return this.theIntegratedServer;
   }

   public static void stopIntegratedServer() {
      if (theMinecraft != null) {
         IntegratedServer integratedserver = theMinecraft.getIntegratedServer();
         if (integratedserver != null) {
            integratedserver.stopServer();
         }
      }
   }

   public PlayerUsageSnooper getPlayerUsageSnooper() {
      return this.usageSnooper;
   }

   public static long getSystemTime() {
      return Sys.getTime() * 1000L / Sys.getTimerResolution();
   }

   public boolean isFullScreen() {
      return this.fullscreen;
   }

   public Session getSession() {
      return this.session;
   }

   public PropertyMap getTwitchDetails() {
      return this.twitchDetails;
   }

   public PropertyMap func_181037_M() {
      if (this.field_181038_N.isEmpty()) {
         GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.field_181038_N.putAll(gameprofile.getProperties());
      }

      return this.field_181038_N;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.renderEngine;
   }

   public IResourceManager getResourceManager() {
      return this.mcResourceManager;
   }

   public ResourcePackRepository getResourcePackRepository() {
      return this.mcResourcePackRepository;
   }

   public LanguageManager getLanguageManager() {
      return this.mcLanguageManager;
   }

   public TextureMap getTextureMapBlocks() {
      return this.textureMapBlocks;
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public SoundHandler getSoundHandler() {
      return this.mcSoundHandler;
   }

   public MusicTicker.MusicType getAmbientMusicType() {
      return this.thePlayer != null
              ? (
              this.thePlayer.worldObj.provider instanceof WorldProviderHell
                      ? MusicTicker.MusicType.NETHER
                      : (
                      this.thePlayer.worldObj.provider instanceof WorldProviderEnd
                              ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END)
                              : (
                              this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying
                                      ? MusicTicker.MusicType.CREATIVE
                                      : MusicTicker.MusicType.GAME
                      )
              )
      )
              : MusicTicker.MusicType.MENU;
   }
   public void dispatchKeypresses() {
      int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
      if (i != 0
         && !Keyboard.isRepeatEvent()
         && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)) {
         if (Keyboard.getEventKeyState()) {
            if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
               this.toggleFullscreen();
            } else if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
               this.ingameGUI
                  .getChatGUI()
                  .printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
            }
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public void setRenderViewEntity(Entity viewingEntity) {
      this.renderViewEntity = viewingEntity;
      this.entityRenderer.loadEntityShader(viewingEntity);
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
      Validate.notNull(callableToSchedule);
      if (!this.isCallingFromMinecraftThread()) {
         ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
         synchronized(this.scheduledTasks) {
            this.scheduledTasks.add(listenablefuturetask);
            return listenablefuturetask;
         }
      } else {
         try {
            return Futures.immediateFuture(callableToSchedule.call());
         } catch (Exception var6) {
            return Futures.immediateFailedFuture(var6);
         }
      }
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.addScheduledTask(Executors.callable(runnableToSchedule));
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.mcThread;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public RenderItem getRenderItem() {
      return this.renderItem;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public static int getDebugFPS() {
      return debugFPS;
   }

   public FrameTimer func_181539_aj() {
      return this.field_181542_y;
   }

   public static Map<String, String> getSessionInfo() {
      Map<String, String> map = Maps.newHashMap();
      map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
      map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
      map.put("X-Minecraft-Version", "1.8.8");
      return map;
   }

   public boolean func_181540_al() {
      return this.field_181541_X;
   }

   public void func_181537_a(boolean p_181537_1_) {
      this.field_181541_X = p_181537_1_;
   }

   public void setSession(Session session) {
      this.session = session;
   }

   public Timer getTimer() {
      return this.timer;
   }

}
