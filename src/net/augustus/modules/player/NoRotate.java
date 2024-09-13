package net.augustus.modules.player;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventSilentMove;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class NoRotate extends Module {
   public NoRotate() {
      super("NoRotate", new Color(11, 143, 125), Categorys.PLAYER);
   }

   @EventTarget
   public void onRecv(EventReadPacket event) {
      if(event.getPacket() instanceof S08PacketPlayerPosLook) {
         if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.ticksExisted > 10) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.thePlayer.rotationYaw;
            packet.pitch = mc.thePlayer.rotationPitch;
         }
      }
   }


}
