package net.augustus.modules;

import net.augustus.clickgui.clickguis.ClickGui;
import net.augustus.clickgui.screens.ConfigGui;
import net.augustus.events.Event;
import net.augustus.modules.combat.Backtrack;
import net.augustus.utils.skid.vestige.LogUtil;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static net.augustus.utils.interfaces.MC.mc;
import static net.augustus.utils.interfaces.MM.mm;

public class ModuleHelper extends Event {
    public void SetVisible() {
        try {


            //GrimNoXZ Visible Helper.

            mm.expandSca.switchBack.setVisible(!mm.expandSca.blockPicker.getSelected().equals("None"));
            //
            mm.speed.grimCollideCheckKeyPressed.setVisible(mm.speed.mode.getSelected().equals("GrimCollide"));
            mm.speed.vuclanSlow.setVisible(mm.speed.mode.getSelected().equals("Vulcan"));
            mm.speed.VulcanYport.setVisible(mm.speed.mode.getSelected().equals("Vulcan"));
            mm.speed.onlyHop.setVisible(mm.speed.mode.getSelected().equals("Vulcan"));
            //
            mm.killAura.grimRangeGround.setVisible(mm.killAura.autoGrimRange.getBoolean());
            mm.killAura.grimRangeAir.setVisible(mm.killAura.autoGrimRange.getBoolean());
            //
            mm.postNoSlow.flagDetect_Debug.setVisible(mm.postNoSlow.flagDetect.getBoolean());
            //
            mm.antiBot.hytGetNameMode.setVisible(mm.antiBot.hytGetNames.getBoolean());
            mm.antiBot.tips.setVisible(mm.antiBot.hytGetNames.getBoolean());
            mm.antiBot.livingTime_Ticks.setVisible(mm.antiBot.livingTime.getBoolean());
            //
            boolean attackReduce = mm.velocity.mode.getSelected().equalsIgnoreCase("AttackReduce");
            mm.velocity.attackReduceMotionAmount.setVisible(attackReduce);
            mm.velocity.tickZeroY.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("TickZero"));
            //
            mm.fixes.memoryFix_onTime.setVisible(mm.fixes.memoryFix.getBoolean());
            mm.fixes.memoryFix_onWorld.setVisible(mm.fixes.memoryFix.getBoolean());
            mm.fixes.memoryFix_onMemoryAbove.setVisible(mm.fixes.memoryFix.getBoolean());
            mm.fixes.memoryFix_onTime_Ticks.setVisible(mm.fixes.memoryFix.getBoolean() && mm.fixes.memoryFix_onTime.getBoolean() && mm.fixes.memoryFix_onTime.isVisible());
            mm.fixes.memoryFix_onMemoryAbove_Max.setVisible(mm.fixes.memoryFix.getBoolean() && mm.fixes.memoryFix_onMemoryAbove.getBoolean() && mm.fixes.memoryFix_onMemoryAbove.isVisible());
            mm.fixes.getMemoryFix_onMemoryAbove_Ticks.setVisible(mm.fixes.memoryFix.getBoolean() && mm.fixes.memoryFix_onMemoryAbove.getBoolean() && mm.fixes.memoryFix_onMemoryAbove.isVisible());
            //
            mm.antiDesync.C03sSend_DelayTicks.setVisible(mm.antiDesync.C03sSend.getBoolean());
            mm.antiDesync.C07sSend_DelayTicks.setVisible(mm.antiDesync.C07sSend.getBoolean());
            mm.antiDesync.C0ESend_DelayTicks.setVisible(mm.antiDesync.C0ESend.getBoolean());
            //
            boolean oldPacket = mm.backTrack.mode.getSelected().equalsIgnoreCase("OldPacket");
            boolean packet = mm.backTrack.mode.getSelected().equalsIgnoreCase("Packet");

            mm.backTrack.CancelPost_C07.setVisible((mm.backTrack.cancelPost.getBoolean()));
            mm.backTrack.CancelPost_C08.setVisible((mm.backTrack.cancelPost.getBoolean()));
            mm.backTrack.cancelPost_Debug.setVisible((mm.backTrack.cancelPost.getBoolean()));


            mm.backTrack.storePost_Debug.setVisible(mm.backTrack.storePost.getBoolean());
            mm.backTrack.storePost_C0A.setVisible((mm.backTrack.storePost.getBoolean()));
            mm.backTrack.storePost_C0E.setVisible((mm.backTrack.storePost.getBoolean()));
            mm.backTrack.storePost_C02.setVisible((mm.backTrack.storePost.getBoolean()));


            Backtrack backtrack = mm.backTrack;
            backtrack.oldPacket_MaximumRange.setVisible(oldPacket);

            backtrack.oldPacket_packetsToDelay.setVisible(oldPacket);
            backtrack.oldPacket_OnlyWhenNeeded.setVisible(oldPacket);
            backtrack.oldPacket_Delay.setVisible(oldPacket);


            backtrack.packet_MaxActiveRange.setVisible(packet);
            backtrack.packet_MinDelay.setVisible(packet);
            backtrack.packet_MaxDelay.setVisible(packet);
            backtrack.packet_MaxStartRange.setVisible(packet);
            backtrack.packet_MinRange.setVisible(packet);
            backtrack.packet_MinReleaseRange.setVisible(packet);
            backtrack.packet_SyncHurtTime.setVisible(packet);
            backtrack.packet_MaxHurtTime.setVisible(packet);
            backtrack.packet_ResetOnLagging.setVisible(packet);
            backtrack.packet_ResetOnVelocity.setVisible(packet);
            backtrack.attack_OnlyKillAura.setVisible(packet);
            backtrack.autoRestart.setVisible(packet);

            backtrack.attack_Setting_Targets.setVisible((!mm.backTrack.attack_OnlyKillAura.getBoolean() && packet) || (oldPacket && !mm.backTrack.attack_OnlyKillAura.getBoolean()));
            backtrack.autoRestart_NoNoti.setVisible(backtrack.autoRestart.getBoolean() && backtrack.autoRestart.isVisible());
            backtrack.packetLimit_Amount.setVisible(backtrack.limitPacketAmount.getBoolean());

            //

            mm.tellyHelper.switchBlock_SwitchBack.setVisible(mm.tellyHelper.switchBlock.getBoolean());
            //
            mm.noSlow.autoRestart_NoNoti.setVisible(mm.noSlow.autoRestart.getBoolean() && mm.noSlow.autoRestart.isVisible());


            mm.noSlow.flagDetect_Debug.setVisible(mm.noSlow.flagDetect.getBoolean());
            //
            mm.postNoSlow.autoRestart_NoNoti.setVisible(mm.postNoSlow.autoRestart.getBoolean() && mm.postNoSlow.autoRestart.isVisible());

            mm.postNoSlow.flagDetect_Debug.setVisible(mm.postNoSlow.flagDetect.getBoolean());
            //
            mm.dumbMessageFucker.notBlockMessageAbove_Amount.setVisible(mm.dumbMessageFucker.notBlockMessageAbove.getBoolean());
            //
            mm.velocity.setXZMotion_cancelSprint.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("SetXZMotion"));
            mm.velocity.setXZMotion_NoAttacking.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("SetXZMotion"));
            mm.velocity.setXZMotion_Amount.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("SetXZMotion"));
            //
            mm.moreKB.idkWhatToCallThisLmao.setVisible(mm.moreKB.mode.getSelected().equalsIgnoreCase("MorePacket"));
            mm.moreKB.legitFast_MinDelay.setVisible(mm.moreKB.mode.getSelected().equalsIgnoreCase("LegitFast"));
            mm.moreKB.legitFast_MaxDelay.setVisible(mm.moreKB.mode.getSelected().equalsIgnoreCase("LegitFast"));
            //
            mm.fastPlace.delay.setVisible(mm.fastPlace.mode.getSelected().equalsIgnoreCase("RightClick"));
            //
            mm.autoHeal.heal_Delay.setVisible(mm.autoHeal.mode.getSelected().equalsIgnoreCase("/heal"));
            mm.autoHeal.heal_MinHealth.setVisible(mm.autoHeal.mode.getSelected().equalsIgnoreCase("/heal"));
            //
            //mm.scaffold.doGroundSetSpeed_GroundSpeedAmount.setVisible(mm.scaffold.doGroundSetSpeed.getBoolean());
            //mm.scaffold.doAirSetSpeed_AirSpeedAmount.setVisible(mm.scaffold.doArSetSpeed.getBoolean());
            //
            mm.autoTool.switchBack.setVisible(!mm.autoTool.slientSwitch.getBoolean());
            //
            mm.esp.healthLine.setVisible(false);
            mm.esp.fakeC.setVisible(false);
            mm.esp.box.setVisible(false);
            mm.esp.vanilla.setVisible(false);
            mm.esp.hase.setVisible(false);
            mm.esp.other2D.setVisible(false);
            mm.esp.f2D.setVisible(false);
            //
            mm.criticals.verusSmart_UseC06.setVisible(mm.criticals.mode.getSelected().equalsIgnoreCase("VerusSmart"));
            mm.criticals.jump_NoGround.setVisible(mm.criticals.mode.getSelected().equalsIgnoreCase("Jump"));
            mm.criticals.jump_CustomJumpHeight.setVisible(mm.criticals.mode.getSelected().equalsIgnoreCase("Jump"));
            //
            mm.fly.latestNCP_Teleport.setVisible(mm.fly.mode.getSelected().equalsIgnoreCase("LatestNCP"));
            mm.fly.latestNCP_AddSpeed.setVisible(mm.fly.mode.getSelected().equalsIgnoreCase("LatestNCP"));
            mm.fly.latestNCP_Timer.setVisible(mm.fly.mode.getSelected().equalsIgnoreCase("LatestNCP"));
            mm.fly.timer.setVisible(!mm.fly.latestNCP_Timer.isVisible());
            //
            mm.arrayList.underline.setVisible(mm.arrayList.mode.getSelected().equalsIgnoreCase("New"));
            mm.arrayList.sideOption.setVisible(mm.arrayList.mode.getSelected().equalsIgnoreCase("Vega"));
            mm.arrayList.font.setVisible(mm.arrayList.mode.getSelected().equalsIgnoreCase("Vega"));
            mm.arrayList.suffix.setVisible(!mm.arrayList.mode.getSelected().equalsIgnoreCase("Vega"));
            //
            mm.blink.inAllows.setVisible(mm.blink.in.getBoolean());
            mm.blink.outAllows.setVisible(mm.blink.out.getBoolean());
            //
            mm.speed.intaveTimer_NoHurt.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("IntaveTimer"));
            //
            //mm.grimVelocity.grimVe_RayCast.setVisible(mm.grimVelocity.grimModes.getSelected().equalsIgnoreCase(GrimVelocity.velMode.Vertical.toString()));
          //  mm.scaffold.offGroundSpeed.setVisible(mm.scaffold.sprint.getSelected().equals("OffGroundSpeed"));
            mm.scaffold.offGroundSpeed.setVisible(mm.scaffold.sprintMode.getSelected().equalsIgnoreCase("OffGroundSpeed"));
            //
            mm.ipNoSlow.food_SendC0C.setVisible(mm.ipNoSlow.food.getBoolean());
            mm.ipNoSlow.sword_SendC0C.setVisible(mm.ipNoSlow.sword.getBoolean());
            mm.ipNoSlow.food_Strict.setVisible(mm.ipNoSlow.food.getBoolean());
            mm.ipNoSlow.food_SendC0C_Allow.setVisible(mm.ipNoSlow.food_SendC0C.isVisible() && mm.ipNoSlow.food_SendC0C.getBoolean());
            //
            mm.speed.aacMode.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("AAC"));
            //
            mm.noSlow.restLegitBug_PostDisFix.setVisible(mm.noSlow.restlegitbug.getBoolean());
        } catch (NullPointerException nullPointerException) {
            nullPointerException.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void SetData() {
        if (mm.killAura.autoGrimRange.getBoolean()) {
            if (mc.thePlayer.onGround) {
                mm.killAura.aimRange.setValue(mm.killAura.grimRangeGround.getValue());
                mm.killAura.rangeSetting.setValue(mm.killAura.grimRangeGround.getValue());
                mm.killAura.blockrange.setValue(mm.killAura.grimRangeGround.getValue());
            }
            if (!mc.thePlayer.onGround) {

                mm.killAura.aimRange.setValue(mm.killAura.grimRangeAir.getValue());
                mm.killAura.rangeSetting.setValue(mm.killAura.grimRangeAir.getValue());
                mm.killAura.blockrange.setValue(mm.killAura.grimRangeAir.getValue());

            }
        }
        if (!mm.postDis.isToggled() && mm.chestStealer2.sendPostC0FFix.getBoolean()) {
            LogUtil.addChatMessage("§F[§6ChestStealer2§F]§F[§5Anti§4LIQ§F] You need to Enable §4PostDis§F to Use §5SendPostC0FFix§F !");
            mm.chestStealer2.sendPostC0FFix.setBoolean(false);
        }
        mm.chestStealer2.sendPostC0FFix.setVisible(mm.chestStealer2.post.getBoolean());
        //
        mm.speed.dmgSpeed.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("Verus"));
        mm.speed.verus_damageBoost.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("Verus"));
        mm.speed.vanilla_Strafe.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("Vanilla") || mm.speed.mode.getSelected().equalsIgnoreCase("VanillaHop"));
        mm.speed.vanillaSpeed.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("Vanilla") || mm.speed.mode.getSelected().equalsIgnoreCase("VanillaHop"));
        mm.speed.vanillaHeight.setVisible(mm.speed.mode.getSelected().equalsIgnoreCase("VanillaHop"));
        //
        mm.velocity.lreverse.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Intave"));
        mm.velocity.yreducer.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Intave"));
        mm.velocity.yreduce.setVisible(mm.velocity.mode.getSelected().equalsIgnoreCase("Intave") && mm.velocity.yreducer.isVisible());
        //
        mm.speed.timerSpeed.setVisible(!mm.speed.mode.getSelected().equalsIgnoreCase("IntaveTimer"));
    }

    public void SetKey(){
        if (mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof net.augustus.cleanGui.CleanClickGui || mc.currentScreen instanceof ConfigGui) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            if (!mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
            if (mm.sprint.isToggled()
                    && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equalsIgnoreCase("Always"))) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            }

        }

    }
}
