package net.augustus.modules.player;

import net.augustus.cleanGui.CleanClickGui;
import net.augustus.clickgui.clickguis.ClickGui;
import net.augustus.clickgui.screens.ConfigGui;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.MoveUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class InvMove extends Module {
   public InvMove() {
      super("InvMove", new Color(11, 143, 125), Categorys.PLAYER);
   }
   private final BooleanValue forward = new BooleanValue(8321,"Forward",this,true);
   private final BooleanValue backwards = new BooleanValue(11182,"Backwards",this,true);
   private final BooleanValue left = new BooleanValue(5381,"Left",this,true);
   private final BooleanValue right = new BooleanValue(4435,"Right",this,true);
   private final BooleanValue jump = new BooleanValue(2538,"Jump",this,true);
   private final BooleanValue sneak = new BooleanValue(5810,"Sneak",this,true);
   private final BooleanValue sprint = new BooleanValue(16193,"Sprint",this,true);

   private final BooleanValue noChat = new BooleanValue(14859,"NoChat",this,true);
   private final BooleanValue sneakOnMove = new BooleanValue(10936,"SneakOnMove",this,false);
   private final BooleanValue eventPreMove = new BooleanValue(14497,"EventPreMove",this,true);
   private final BooleanValue eventPostMove = new BooleanValue(13990,"EventPostMove",this,true);
   private final BooleanValue eventMove1 = new BooleanValue(12343,"EventMove",this,true);
   private final BooleanValue eventSlientMove1 = new BooleanValue(11559,"EventSlientMove",this,true);
   private final BooleanValue eventUpdate1 = new BooleanValue(4146,"EventUpdate",this,true);
   private final BooleanValue noC16Open = new BooleanValue(7586,"NoC16Open",this,true);
   private final BooleanValue debug = new BooleanValue(4069,"Debug",this,true);

   @Override
   public void onDisable(){
      super.onDisable();
      if (mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof CleanClickGui || mc.currentScreen instanceof ConfigGui) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
         if (!mm.sprint.isToggled())
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
         if (mm.sprint.isToggled()
                 && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
         }
      } else if (mc.currentScreen == null) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
         if (!mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
         if (mm.sprint.isToggled()
                 && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
         }

      }
      else {
         setKey();
      }
   }
   @EventTarget
   public void onEventPreMotion(EventPreMotion eventPreMotion) {
      if (eventPreMove.getBoolean()) {
         setKey();
      }
   }
   @EventTarget
   public void onEventPostMotion(EventPostMotion eventPostMotion) {
      if (eventPostMove.getBoolean()) {
         setKey();
      }
   }
   @EventTarget
   public void onEventMove(EventMove eventMove) {
      if (eventMove1.getBoolean()) {
         setKey();
      }
   }
   @EventTarget
   public void onEventSilentMove(EventSilentMove eventSilentMove) {
      if (eventSlientMove1.getBoolean()) {
         setKey();
      }
   }
   @EventTarget
   public void onEventUpdate(EventUpdate eventUpdate) {
      if (eventUpdate1.getBoolean()) {
         setKey();
      }
   }
   @EventTarget
   public void onEventSendPacket(EventSendPacket eventSendPacket) {
      if ((eventSendPacket.getPacket() instanceof C16PacketClientStatus) && ((C16PacketClientStatus) eventSendPacket.getPacket()).getStatus().equals(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
         eventSendPacket.setCancelled(noC16Open.getBoolean());
         if (debug.getBoolean()) {
            LogUtil.addChatMessage("§F[§6InvMove§f]§F[§3NoC16Open§f][§5Debug§f] Cancelled Packet §8"+eventSendPacket.getPacket()+" §f!");
         }
      }
   }

   private void setKey() {
      if (mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof CleanClickGui || mc.currentScreen instanceof ConfigGui) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
         if (!mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
         if (mm.sprint.isToggled()
                 && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
         }
         return;
      }
      if (mc.currentScreen == null) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
         if (!mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
         if (mm.sprint.isToggled()
                 && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
         }
         return;
      }
      if (!( mc.currentScreen instanceof GuiChat && noChat.getBoolean())) {
         if (sneakOnMove.getBoolean() && MoveUtil.noMoveKey()) {
            if (sneak.getBoolean() && mc.currentScreen != null) {
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            }
            if (!sneak.getBoolean() && mc.currentScreen != null) {
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);


            }
            if (mc.currentScreen == null) {
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
               KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
               if (!mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
               if (mm.sprint.isToggled()
                       && sprint.getBoolean() && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
                  KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
               }
            }
         }
         if (mc.currentScreen == null) return;
         if (forward.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
         if (backwards.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
         if (left.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
         if (right.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
         if (jump.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
         if (sneak.getBoolean()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
         if (sprint.getBoolean() && !mm.sprint.isToggled()) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
         if (mm.sprint.isToggled()
                 && sprint.getBoolean() && (!mm.scaffold.isToggled() || mm.scaffold.sprintMode.getSelected().equals("Always"))) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
         }
         if (sneakOnMove.getBoolean() && !MoveUtil.noMoveKey()) mc.gameSettings.keyBindSneak.pressed = true;
         if (!forward.getBoolean()) mc.gameSettings.keyBindForward.pressed = false;
         if (!backwards.getBoolean()) mc.gameSettings.keyBindBack.pressed = false;
         if (!left.getBoolean()) mc.gameSettings.keyBindLeft.pressed = false;
         if (!right.getBoolean()) mc.gameSettings.keyBindRight.pressed = false;
         if (!jump.getBoolean()) mc.gameSettings.keyBindJump.pressed = false;
         if (!sneak.getBoolean() && !sneakOnMove.getBoolean()) mc.gameSettings.keyBindSneak.pressed = false;
         if (!sprint.getBoolean()  ) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
         }

      }
   }
}
