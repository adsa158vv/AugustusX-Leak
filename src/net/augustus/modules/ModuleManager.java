package net.augustus.modules;

import net.augustus.modules.combat.*;
import net.augustus.modules.exploit.*;
import net.augustus.modules.misc.*;
import net.augustus.modules.movement.*;
import net.augustus.modules.player.*;

import net.augustus.modules.render.*;
import net.augustus.modules.special.IPNoSlow;
import net.augustus.modules.special.PostNoSlow;
import net.augustus.modules.special.WatchdogNoSlow;
import net.augustus.modules.world.*;
import net.augustus.utils.interfaces.MA;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager implements MA {
   //public final EntitySpeed entitySpeed = new EntitySpeed();
   //public final PolarSpeed polarSpeed = new PolarSpeed();

   public final Velocity velocity = new Velocity();
   public final KillAura killAura = new KillAura();
   public final AntiBot antiBot = new AntiBot();
   public final CombatHelper combatHelper = new CombatHelper();

   public final ItemPhysics itemPhysics = new ItemPhysics();
   public final Teams teams = new Teams();
   public final NoRotate noRotate = new NoRotate();
   public final FastFall fastFall = new FastFall();

   public final AutoSoup autoSoup = new AutoSoup();
   public final AutoWeapon autoWeapon = new AutoWeapon();
   public final HurtCam hurtCam = new HurtCam();
   public final Reach reach = new Reach();
   public final AutoClicker autoClicker = new AutoClicker();
   public final AntiFireBall antiFireBall = new AntiFireBall();
   public final SettingsSave settingsSave = new SettingsSave();
   public final TimerRange timerRange = new TimerRange();


   public final Backtrack backTrack = new Backtrack();
   public final Criticals criticals = new Criticals();
   public final MotionGraph motionGraph = new MotionGraph();
   public final MoreKB moreKB = new MoreKB();
   public final FakeForge fakeForge = new FakeForge();
   public final Stuck stuck = new Stuck();

   public final ViaPacketFix viaPacketFix = new ViaPacketFix();
   public final net.augustus.modules.render.ArrayList arrayList = new net.augustus.modules.render.ArrayList();
   public final ClickGUI clickGUI = new ClickGUI();
   public final FocusSound focusSound = new FocusSound();
   public final IPNoSlow ipNoSlow = new IPNoSlow();
   public final DumbMessageFucker dumbMessageFucker = new DumbMessageFucker();
   public final FastBow fastBow = new FastBow();
   public final AttackEffects attackEffects = new AttackEffects();
   public final ESP esp = new ESP();
   public final AntiDesync antiDesync = new AntiDesync();

   //public final LegitTargetStrafe legitTargetStrafe = new LegitTargetStrafe();
   public final StorageESP storageESP = new StorageESP();
   public final BlockAnimation blockAnimation = new BlockAnimation();
   public final FullBright fullBright = new FullBright();
   public final Protector protector = new Protector();
   public final ChinaHat chinaHat = new ChinaHat();
   public final NameTags nameTags = new NameTags();
   public final HUD hud = new HUD();

   public final PacketDebugger packetDebugger = new PacketDebugger();

   public final Tracers tracers = new Tracers();
  //public final  GrimAutoArmor grimAutoArmor = new GrimAutoArmor();
   public final ItemEsp itemEsp = new ItemEsp();

   public final BrightPlayers brightPlayers = new BrightPlayers();

   public final Scoreboard scoreboard = new Scoreboard();
   public final BlockESP blockESP = new BlockESP();
   public final Barriers barriers = new Barriers();
   public final Line line = new Line();
   public final Ambiance ambiance = new Ambiance();
   public final CrossHair crossHair = new CrossHair();
   public final Trajectories trajectories = new Trajectories();
   public final Projectiles projectiles = new Projectiles();
   public final CustomGlint customGlint = new CustomGlint();
   public final CustomItemPos customItemPos = new CustomItemPos();

   public final GrimNoXZ grimNoXZ = new GrimNoXZ();

   public final Sprint sprint = new Sprint();
   public final Jesus jesus = new Jesus();
   public final NoSlow noSlow = new NoSlow();

   public final ClientGrim clientgrim = new ClientGrim();
   public final Speed speed = new Speed();
   public final Timer timer = new Timer();
   public final Strafe strafe = new Strafe();
   public final Blink blink = new Blink();
   public final TestModule testModule = new TestModule();
   public final Fly fly = new Fly();
   public final BugUp bugUp = new BugUp();
   public final TargetStrafe targetStrafe = new TargetStrafe();
   public final FastLadder fastLadder = new FastLadder();
   public final Spider spider = new Spider();
   public final NoBlockSlow noBlockSlow = new NoBlockSlow();
   public final Step step = new Step();
   public final LongJump longJump = new LongJump();
   public final VClip vclip = new VClip();
   public final SafeWalk safeWalk = new SafeWalk();
   public final IntaveBoatFly intaveBoatFly = new IntaveBoatFly();
   public final NoFall noFall = new NoFall();
   public final Eagle eagle = new Eagle();
   public final GermMC germMC = new GermMC();
   public final C13C18Dis c13C18Dis = new C13C18Dis();
   public final TimerBalancer timerBalancer = new TimerBalancer();
   //public final GrimVelocity grimVelocity = new GrimVelocity();
   public final ScaffoldWalk scaffoldWalk = new ScaffoldWalk();
  //public final BoatHelper boatHelper = new BoatHelper();

   public final ChestStealer chestStealer = new ChestStealer();
   public final AutoArmor autoArmor = new AutoArmor();
   public final InvCleaner invCleaner = new InvCleaner();
   public final FastBreakBypass fastBreakBypass = new FastBreakBypass();

   public final PostNoSlow postNoSlow = new PostNoSlow();
   public final ChestAura chestAura = new ChestAura();
   public final InvMove invMove = new InvMove();
   public final Notifications notifications = new Notifications();
   public final CustomCape customCape = new CustomCape();
   public final TellyHelper tellyHelper = new TellyHelper();
   public final GrimTNTFly grimTNTFly = new GrimTNTFly();

   public final AutoTool autoTool = new AutoTool();
   public final FakeLag fakeLag = new FakeLag();
   public final WatchdogNoSlow watchdogNoSlow = new WatchdogNoSlow();
   public final Teleport teleport = new Teleport();
   public final Phase phase = new Phase();
   public final ChestStealer2 chestStealer2 = new ChestStealer2();
   public final AutoArmor2 autoArmor2 = new AutoArmor2();
   public final Regen regen = new Regen();
   public final KillEffects killEffects = new KillEffects();

   //public final ScaffoldWalk scaffoldWalk = new ScaffoldWalk();
   //public final NewScaffold newScaffold = new NewScaffold();
   public final FastBreak fastBreak = new FastBreak();
   public final Fucker fucker = new Fucker();
   public final ExpandSca expandSca = new ExpandSca();
   public final FastPlace fastPlace = new FastPlace();
   public final Scaffold scaffold = new Scaffold();
   public final Spammer spammer = new Spammer();
   public final MidClick midClick = new MidClick();
   public final Disabler disabler = new Disabler();
   public final PostDis postDis = new PostDis();

  // public final GrimDisabler grimDisabler = new GrimDisabler();
   //Post Dis Doesn't Work!
   public final AutoPlay autoPlay = new AutoPlay();
   public final AutoRegister autoRegister = new AutoRegister();
   public final SpinBot spinBot = new SpinBot();
   public final Fixes fixes = new Fixes();
   public final AutoWalk autoWalk = new AutoWalk();
   public final StaffDetector staffDetector = new StaffDetector();
   public final Shaders shaders = new Shaders();
   public final MLDisabler mlDisabler = new MLDisabler();
   public final AutoHeal autoHeal = new AutoHeal();
   public final AntiBadEffects antiBadEffects = new AntiBadEffects();

   public final Intave13Fly intave13Fly = new Intave13Fly();
    public List<Module> getModules() {
      return ma.getModules();
    }


   public List<Module> getModules(Categorys cat) {
      List<Module> modules = new ArrayList<>();
      for(Module mod : getModules()) {
         if(mod.getCategory().equals(cat)) {
            modules.add(mod);
         }
      }
      return modules;
   }


   public void setModules(List<Module> modules) {
      ma.setModules(modules);
   }

   public ArrayList<Module> getActiveModules() {
      return ma.getActiveModules();
   }


   public void setActiveModules(ArrayList<Module> activeModules) {
      ma.setActiveModules(activeModules);
   }
}
