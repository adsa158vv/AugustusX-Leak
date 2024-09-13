package net.augustus.clickgui;

import net.augustus.events.EventClickGui;

import net.augustus.utils.cape.LoadCape;
import net.augustus.utils.interfaces.MC;
import net.augustus.utils.interfaces.MM;
import net.augustus.utils.interfaces.SM;
import net.lenni0451.eventapi.reflection.EventTarget;

public class SettingSorter implements MC, MM, SM {
   @EventTarget
   public void onEventClickGui(EventClickGui eventClickGui) {
      split1();
      if (mm.killAura.mode.getSelected().equals("Advanced")) {
         split2();
         split3();
      } else {
         split4();
      }
      split5();
      split6();
      split7();
      split8();
      split9();
      speed();
   }

   private void speed() {
      mm.speed.hyp_damageBoost.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_bypassMode.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_fallingStrafe.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_fastFall.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_glide.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_sussyPacket.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
      mm.speed.hyp_sneakStrafe.setVisible(mm.speed.mode.getSelected().equals("Hypixel"));
   }

   private void split9() {
      //mm.velocity.h.setVisible(mm.velocity.mode.getSelected().equals("Packet"));
     //mm.velocity.vertical.setVisible(mm.velocity.mode.getSelected().equals("Packet"));
      mm.targetStrafe.towards.setVisible(mm.targetStrafe.legit.getBoolean());
      mm.targetStrafe.clickOnly.setVisible(mm.targetStrafe.legit.getBoolean());
      mm.targetStrafe.autowalk.setVisible(mm.targetStrafe.legit.getBoolean());

      mm.spinBot.spinSpeed.setVisible(mm.spinBot.mode.getSelected().equals("Spin"));
      mm.scaffold.latestPlace.setVisible(mm.scaffold.latestRotate.getBoolean() && mm.scaffold.rayCast.getBoolean());
      mm.scaffold.playerYaw.setVisible(mm.scaffold.rayCast.getBoolean());
      mm.scaffold.moonWalk.setVisible(mm.scaffold.playerYaw.isVisible() && mm.scaffold.playerYaw.getBoolean() && !mm.scaffold.godBridge.getBoolean());
      mm.scaffold.godBridge.setVisible(mm.scaffold.playerYaw.isVisible() && mm.scaffold.playerYaw.getBoolean() && !mm.scaffold.moonWalk.getBoolean());
      mm.scaffold.rotateToBlock.setVisible(!mm.scaffold.rayCast.getBoolean());
      mm.scaffold.correctSide.setVisible(mm.scaffold.rotateToBlock.isVisible() && mm.scaffold.rotateToBlock.getBoolean());
      mm.scaffold.sneakDelayBool.setVisible(mm.scaffold.sneak.getBoolean());
      mm.scaffold.sneakDelay.setVisible(mm.scaffold.sneakDelayBool.isVisible() && mm.scaffold.sneakDelayBool.getBoolean());
      mm.scaffold.sneakBlocks.setVisible(!mm.scaffold.sneakDelayBool.getBoolean() && mm.scaffold.sneak.getBoolean());
      mm.scaffold.sneakBlocksDiagonal.setVisible(!mm.scaffold.sneakDelayBool.getBoolean() && mm.scaffold.sneak.getBoolean());
      mm.scaffold.sneakTicks.setVisible(mm.scaffold.sneak.getBoolean());
      mm.scaffold.sneakOnPlace.setVisible(mm.scaffold.sneak.getBoolean());
      mm.scaffold.adStrafe.setVisible(mm.scaffold.playerYaw.getBoolean() && !mm.scaffold.moonWalk.getBoolean());
      mm.scaffold.adStrafeLegit.setVisible(mm.scaffold.adStrafe.getBoolean() && mm.scaffold.playerYaw.getBoolean() && !mm.scaffold.moonWalk.getBoolean());
      mm.scaffold.spamClickDelay.setVisible(mm.scaffold.spamClick.getBoolean() && !mm.scaffold.intaveHit.getBoolean());
      mm.scaffold.intaveHit.setVisible(mm.scaffold.spamClick.getBoolean());
      mm.scaffold.sameY.setVisible(mm.scaffold.rayCast.getBoolean());
      mm.scaffold.blockSafe.setVisible(mm.scaffold.playerYaw.getBoolean());
      mm.customGlint.customColor.setVisible(!mm.customGlint.removeGlint.getBoolean());
      mm.customGlint.glintSpeed.setVisible(!mm.customGlint.removeGlint.getBoolean());
      mm.customGlint.color.setVisible(mm.customGlint.customColor.getBoolean() && mm.customGlint.customColor.isVisible());
      mm.antiFireBall.range.setVisible(!mm.antiFireBall.rotate.getBoolean());
      mm.antiFireBall.yawSpeed.setVisible(mm.antiFireBall.rotate.getBoolean());
      mm.antiFireBall.pitchSpeed.setVisible(mm.antiFireBall.rotate.getBoolean());
      mm.antiFireBall.slowDown.setVisible(mm.antiFireBall.rotate.getBoolean());
      mm.antiFireBall.moveFix.setVisible(mm.antiFireBall.rotate.getBoolean());
   }

   private void split8() {
      mm.line.killAuraColor.setVisible(mm.line.killAura.getBoolean());
      mm.line.killAuraLineWidth.setVisible(mm.line.killAura.getBoolean());

      mm.fly.vanilla_Speed.setVisible(mm.fly.mode.getSelected().equalsIgnoreCase("Vanilla"));
      mm.fly.motion_Motion.setVisible(mm.fly.mode.getSelected().equalsIgnoreCase("Motion"));

      mm.arrayList.vegaColor1.setVisible(mm.arrayList.mode.getSelected().equals("Vega"));
      mm.arrayList.vegaColor2.setVisible(mm.arrayList.mode.getSelected().equals("Vega"));
      mm.spider.motion.setVisible(mm.spider.mode.getSelected().equals("Basic") || mm.spider.mode.getSelected().equals("Jump") && mm.spider.customJumpMotion.getBoolean());
      mm.spider.onGroundPacket.setVisible(mm.spider.mode.getSelected().equals("Jump"));
      mm.spider.motionToJump.setVisible(mm.spider.mode.getSelected().equals("Jump"));
      mm.spider.customJumpMotion.setVisible(mm.spider.mode.getSelected().equals("Jump"));
      mm.storageESP.color.setVisible(!mm.storageESP.rainbow.getBoolean());
      mm.storageESP.lineWidth.setVisible(mm.storageESP.mode.getSelected().equals("Box") || mm.storageESP.mode.getSelected().equals("OtherBox"));
      mm.storageESP.rainbowAlpha.setVisible(mm.storageESP.rainbow.getBoolean() && (mm.storageESP.mode.getSelected().equals("Box") || mm.storageESP.mode.getSelected().equals("OtherBox")));
      mm.storageESP.rainbowSpeed.setVisible(mm.storageESP.rainbow.getBoolean());
      mm.blockESP.rainbowAlpha.setVisible(mm.blockESP.rainbow.getBoolean());
      mm.blockESP.rainbowSpeed.setVisible(mm.blockESP.rainbow.getBoolean());
      mm.blockESP.color.setVisible(!mm.blockESP.rainbow.getBoolean());
      mm.tracers.color.setVisible(!mm.tracers.rainbow.getBoolean() && !mm.tracers.staticColor.getBoolean());
      mm.tracers.rainbow.setVisible(!mm.tracers.staticColor.getBoolean());
      mm.tracers.staticColor.setVisible(!mm.tracers.rainbow.getBoolean());
      mm.tracers.rainbowSpeed.setVisible(mm.tracers.rainbow.getBoolean());
      mm.crossHair.color.setVisible(!mm.crossHair.rainbow.getBoolean());
      mm.crossHair.rainbowSpeed.setVisible(mm.crossHair.rainbow.getBoolean());
      mm.scaffold.resetRotation.setVisible(mm.scaffold.snap.getBoolean());
      mm.scaffold.predict.setVisible(mm.scaffold.latestRotate.getBoolean() && mm.scaffold.rayCast.getBoolean() && mm.scaffold.latestRotate.isVisible());
      mm.scaffold.backupTicks.setVisible(mm.scaffold.latestRotate.getBoolean() && mm.scaffold.latestRotate.isVisible());
      mm.scaffold.latestRotate.setVisible(mm.scaffold.rayCast.getBoolean());
   }

   private void split7() {
      mm.esp.rainbowSpeed.setVisible(mm.esp.rainbow.getBoolean());
      mm.esp.rainbowAlpha.setVisible(mm.esp.rainbow.getBoolean() && mm.esp.box.getBoolean());
      mm.esp.lineWidth.setVisible(mm.esp.box.getBoolean());
      mm.esp.otherColorOnHit.setVisible(!mm.esp.vanilla.getBoolean());
      mm.esp.hitColor.setVisible(mm.esp.otherColorOnHit.getBoolean() && !mm.esp.vanilla.getBoolean());
      mm.scoreboard.yCord.setVisible(!mm.scoreboard.remove.getBoolean() && !mm.scoreboard.stick.getBoolean());
      mm.scoreboard.xCord.setVisible(!mm.scoreboard.remove.getBoolean());
      mm.scoreboard.stick.setVisible(!mm.scoreboard.remove.getBoolean());
      mm.fucker.instant.setVisible(mm.fucker.action.getSelected().equals("Break"));

      mm.autoSoup.autoClose.setVisible(mm.autoSoup.fill.getBoolean());
      mm.noSlow.swordForward.setVisible(mm.noSlow.swordSlowdown.getBoolean());
      mm.noSlow.swordStrafe.setVisible(mm.noSlow.swordSlowdown.getBoolean());
      mm.noSlow.bowForward.setVisible(mm.noSlow.bowSlowdown.getBoolean());
      mm.noSlow.bowStrafe.setVisible(mm.noSlow.bowSlowdown.getBoolean());
      mm.noSlow.restForward.setVisible(mm.noSlow.restSlowdown.getBoolean());
      mm.noSlow.restStrafe.setVisible(mm.noSlow.restSlowdown.getBoolean());
      mm.noSlow.timerSword.setVisible(mm.noSlow.swordTimer.getBoolean());
      mm.noSlow.timerBow.setVisible(mm.noSlow.bowTimer.getBoolean());
      mm.noSlow.timerRest.setVisible(mm.noSlow.restTimer.getBoolean());
      mm.line.lineWidth.setVisible(mm.line.line.getBoolean());
      mm.line.color.setVisible(mm.line.line.getBoolean());
      mm.line.lineTime.setVisible(mm.line.line.getBoolean());
      mm.line.killAuraLineTime.setVisible(mm.line.killAura.getBoolean());
   }

   private void split6() {

      mm.noFall.fallDistance.setVisible(mm.noFall.mode.getSelected().equals("OnGround"));
      mm.noFall.lookRange.setVisible(mm.noFall.mode.getSelected().equals("Legit"));
      mm.noFall.yawSpeed.setVisible(mm.noFall.mode.getSelected().equals("Legit"));
      mm.noFall.pitchSpeed.setVisible(mm.noFall.mode.getSelected().equals("Legit"));
      mm.noFall.delay.setVisible(mm.noFall.mode.getSelected().equals("Legit"));
      mm.noFall.legitFallDistance.setVisible(mm.noFall.mode.getSelected().equals("Legit"));
      mm.jesus.speed.setVisible(mm.jesus.mode.getSelected().equals("Speed"));
      mm.nameTags.height.setVisible(!mm.nameTags.mode.getSelected().equals("None"));
      mm.nameTags.armor.setVisible(!mm.nameTags.mode.getSelected().equals("None"));
      mm.nameTags.scale.setVisible(!mm.nameTags.mode.getSelected().equals("None"));
      mm.tracers.color.setVisible(mm.tracers.staticColor.getBoolean());
      mm.hud.color.setVisible(!mm.hud.mode.getSelected().equals("None"));
      mm.hud.backGround.setVisible(!mm.hud.mode.getSelected().equals("None"));
      mm.hud.backGroundColor.setVisible(!mm.hud.mode.getSelected().equals("None") && mm.hud.backGround.getBoolean());
      mm.hud.size.setVisible(mm.hud.mode.getSelected().equals("Augustus"));
      mm.hud.radius.setVisible(mm.hud.targetMode.getSelected().equals("AugustusX_Round"));
      mm.hud.blur.setVisible(mm.hud.targetMode.getSelected().equals("AugustusX_Round"));
      if (!mm.esp.rainbow.getBoolean()) {
         mm.esp.color.setVisible(!mm.esp.vanilla.getBoolean());
         mm.esp.outlineColor.setVisible(mm.esp.vanilla.getBoolean());
      } else {
         mm.esp.color.setVisible(false);
         mm.esp.outlineColor.setVisible(false);
      }
   }

   public void split1() {
      if (mm.customCape.reload.getBoolean()) {
         mm.customCape.reload.setBoolean(false);
         mm.customCape.customCapes.setStringList(LoadCape.getCapes());
      }
      mm.arrayList.backGroundColor.setVisible(mm.arrayList.backGround.getBoolean() && mm.arrayList.mode.getSelected().equalsIgnoreCase("Default"));
      mm.arrayList.staticColor.setVisible(!mm.arrayList.randomColor.getBoolean() && !mm.arrayList.rainbow.getBoolean() && mm.arrayList.mode.getSelected().equalsIgnoreCase("Default"));
      mm.arrayList.randomColor.setVisible(!mm.arrayList.rainbow.getBoolean() && mm.arrayList.mode.getSelected().equalsIgnoreCase("Default"));
      mm.arrayList.rainbow.setVisible(!mm.arrayList.randomColor.getBoolean());
      // mm.arrayList.rainbowSpeed.setVisible(mm.arrayList.rainbow.getBoolean() && mm.arrayList.mode.getSelected().equalsIgnoreCase("Default"));
      mm.killAura.maxDelay.setValue(Math.max(mm.killAura.minDelay.getValue(), mm.killAura.maxDelay.getValue()));
      mm.killAura.minDelay.setValue(Math.min(mm.killAura.minDelay.getValue(), mm.killAura.maxDelay.getValue()));
   }
   public void split2() {
      mm.killAura.coolDown.setVisible(true);
      mm.killAura.targetRandom.setVisible(true);
      mm.killAura.rangeMode.setVisible(true);
      mm.killAura.blockMode.setVisible(true);
      mm.killAura.attackMode.setVisible(true);
      mm.killAura.yawSpeedMin.setVisible(true);
      mm.killAura.yawSpeedMax.setVisible(true);
      mm.killAura.pitchSpeedMin.setVisible(true);
      mm.killAura.pitchSpeedMax.setVisible(true);
      mm.killAura.randomStrength.setVisible(true);
      mm.killAura.interpolation.setVisible(true);
      mm.killAura.smoothBackRotate.setVisible(true);
      mm.killAura.stopOnTarget.setVisible(true);
      mm.killAura.smartAim.setVisible(true);
      mm.killAura.invattack.setVisible(true);
      mm.killAura.randomize.setVisible(false);
      mm.killAura.multi.setVisible(false);
      mm.killAura.targetRandom.setVisible(true);
      mm.killAura.hitChance.setVisible(true);
      mm.killAura.block.setVisible(false);
      mm.killAura.moveFix.setVisible(true);
      mm.killAura.heuristics.setVisible(true);
      mm.killAura.preHit.setVisible(true);
      mm.killAura.advancedRots.setVisible(true);
   }
   public void split3() {
      mm.killAura.hazeAdd.setVisible(mm.killAura.hazeRange.getBoolean());
      mm.killAura.hazeMax.setVisible(mm.killAura.hazeRange.getBoolean());
      mm.killAura.hazeRange.setVisible(true);
      mm.killAura.perfectHit.setVisible(true);
      mm.killAura.perfectHitGomme.setVisible(mm.killAura.perfectHit.isVisible() && mm.killAura.perfectHit.getBoolean());
      mm.killAura.yawSpeedMax.setValue(Math.max(mm.killAura.yawSpeedMin.getValue(), mm.killAura.yawSpeedMax.getValue()));
      mm.killAura.yawSpeedMin.setValue(Math.min(mm.killAura.yawSpeedMin.getValue(), mm.killAura.yawSpeedMax.getValue()));
      mm.killAura.pitchSpeedMax.setValue(Math.max(mm.killAura.pitchSpeedMin.getValue(), mm.killAura.pitchSpeedMax.getValue()));
      mm.killAura.pitchSpeedMin.setValue(Math.min(mm.killAura.pitchSpeedMin.getValue(), mm.killAura.pitchSpeedMax.getValue()));
      mm.killAura.silentMoveFix.setVisible(mm.killAura.moveFix.getBoolean());
      mm.killAura.intave.setVisible(true);
      if (mm.killAura.blockMode.getSelected().equalsIgnoreCase("Custom")) {
         mm.killAura.unblockHit.setVisible(true);
         mm.killAura.unblockHitOnly.setVisible(mm.killAura.unblockHit.getBoolean());
         mm.killAura.startBlock.setVisible(mm.killAura.unblockHit.getBoolean() && !mm.killAura.unblockHitOnly.getBoolean());
         mm.killAura.endBlock.setVisible(mm.killAura.unblockHit.getBoolean() && !mm.killAura.unblockHitOnly.getBoolean());
         mm.killAura.endBlockHitOnly.setVisible(mm.killAura.unblockHitOnly.getBoolean() && mm.killAura.unblockHit.getBoolean());
      } else {
         mm.killAura.unblockHit.setVisible(false);
         mm.killAura.unblockHitOnly.setVisible(false);
         mm.killAura.startBlock.setVisible(false);
         mm.killAura.endBlock.setVisible(false);
         mm.killAura.endBlockHitOnly.setVisible(false);
      }
   }
   public void split4() {
      mm.killAura.hazeAdd.setVisible(false);
      mm.killAura.hazeMax.setVisible(false);
      mm.killAura.hazeRange.setVisible(false);
      mm.killAura.minDelay.setVisible(true);
      mm.killAura.maxDelay.setVisible(true);
      mm.killAura.perfectHit.setVisible(false);
      mm.killAura.perfectHitGomme.setVisible(false);
      mm.killAura.coolDown.setVisible(false);
      mm.killAura.multi.setVisible(true);
      mm.killAura.targetRandom.setVisible(false);
      mm.killAura.rangeMode.setVisible(false);
      mm.killAura.blockMode.setVisible(false);
      mm.killAura.attackMode.setVisible(false);
      mm.killAura.yawSpeedMin.setVisible(false);
      mm.killAura.yawSpeedMax.setVisible(false);
      mm.killAura.pitchSpeedMin.setVisible(false);
      mm.killAura.pitchSpeedMax.setVisible(false);
      mm.killAura.randomStrength.setVisible(false);
      mm.killAura.interpolation.setVisible(false);
      mm.killAura.smoothBackRotate.setVisible(false);
      mm.killAura.stopOnTarget.setVisible(false);
      mm.killAura.moveFix.setVisible(false);
      mm.killAura.silentMoveFix.setVisible(false);
      mm.killAura.smartAim.setVisible(false);
      mm.killAura.invattack.setVisible(true);
      mm.killAura.randomize.setVisible(true);
      mm.killAura.targetRandom.setVisible(false);
      mm.killAura.hitChance.setVisible(false);
      mm.killAura.startBlock.setVisible(false);
      mm.killAura.endBlock.setVisible(false);
      mm.killAura.unblockHit.setVisible(false);
      mm.killAura.unblockHitOnly.setVisible(false);
      mm.killAura.endBlockHitOnly.setVisible(false);
      mm.killAura.block.setVisible(true);
      mm.killAura.heuristics.setVisible(false);
      mm.killAura.preHit.setVisible(false);
      mm.killAura.advancedRots.setVisible(false);
      mm.killAura.minDelay.setVisible(true);
      mm.killAura.maxDelay.setVisible(true);
      mm.killAura.intave.setVisible(false);
   }
   public void split5() {
      mm.velocity.ignoreExplosion.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Basic"));
      mm.velocity.YValue.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Basic"));
      mm.velocity.XZValue.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Basic"));
     // mm.velocity.XZValueIntave.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Intave"));
      mm.velocity.jumpIntave.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Intave"));
      mm.velocity.pushStart.setVisible(mm.velocity.mode.getSelected().equals("Push"));
      mm.velocity.pushEnd.setVisible(mm.velocity.mode.getSelected().equals("Push"));
      mm.velocity.pushXZ.setVisible(mm.velocity.mode.getSelected().equals("Push"));
      mm.velocity.pushOnGround.setVisible(mm.velocity.mode.getSelected().equals("Push"));
      mm.velocity.reverseStart.setVisible(mm.velocity.mode.getSelected().equals("Reverse"));
      mm.velocity.karhuStart.setVisible(mm.velocity.mode.getSelected().equals("TickZero"));
      mm.velocity.reverseStrafe.setVisible(mm.velocity.mode.getSelected().equals("Reverse"));


   }
}
