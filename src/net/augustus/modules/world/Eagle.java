package net.augustus.modules.world;

import net.augustus.events.EventPreMotion;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.awt.*;

/**
 * @author Genius
 * @date 2024/4/4
 * @time 15:13
 */

public class Eagle extends Module {

    public Eagle() {
        super("Eagle", new Color(255,255,255), Categorys.WORLD);
    }

    public static Block getBlock(final BlockPos pos) {
        return Eagle.mc.theWorld.getBlockState(pos).getBlock();
    }

    public static Block getBlockUnderPlayer(final EntityPlayer player) {
        return getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }

    @EventTarget
    public void onUpdate(final EventPreMotion event) {

        if (getBlockUnderPlayer((EntityPlayer) Eagle.mc.thePlayer) instanceof BlockAir) {
            if (Eagle.mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
        } else if (Eagle.mc.thePlayer.onGround) {
            KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }

    }

    public void onEnable() {
        if (Eagle.mc.thePlayer == null) {
            return;
        }
        Eagle.mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }

}
