package net.augustus.modules.render;

import net.augustus.events.EventShaderRender;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.notify.reborn.CleanNotificationManager;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.skid.tenacity.RenderUtil;
import net.augustus.utils.skid.tenacity.blur.KawaseBloom;
import net.augustus.utils.skid.tenacity.blur.KawaseBlur;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

public class Shaders extends Module {
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    public final BooleanValue blur = new BooleanValue(6683, "Blur", this, true);
    private final DoubleValue iterations = new DoubleValue(4237, "Blur Iterations", this, 2, 8, 1, 0);
    private final DoubleValue offset = new DoubleValue(5322, "Blur Offset", this, 3, 10, 1, 0);
    private final BooleanValue bloom = new BooleanValue(3420, "Bloom", this, false);
    private final DoubleValue shadowRadius = new DoubleValue(16264, "Bloom Iterations", this, 3, 8, 1, 0);
    private final DoubleValue shadowOffset = new DoubleValue(9019, "Bloom Offset", this, 1, 10, 1, 0);
    public Shaders() {
        super("Shaders", Color.YELLOW, Categorys.RENDER);
    }

    public void stuffToBlur(boolean bloom) {

        ScaledResolution sr = new ScaledResolution(mc);

    }

    public void blurScreen() {
        if (!isToggled()) return;
        if (blur.getBoolean()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);

            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            EventManager.call(new EventShaderRender(false));
            CleanNotificationManager.shader();
            stuffToBlur(false);
            stencilFramebuffer.unbindFramebuffer();


            KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, (int) iterations.getValue(), (int) offset.getValue());

        }


        if (bloom.getBoolean()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);

            EventManager.call(new EventShaderRender(true));
            stuffToBlur(true);

            stencilFramebuffer.unbindFramebuffer();

            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, (int) shadowRadius.getValue(), (int) shadowOffset.getValue());

        }
    }
}
