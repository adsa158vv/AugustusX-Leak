package net.augustus.client.render.shader.shaders;

import net.augustus.client.render.shader.RiseShader;
import net.augustus.client.render.shader.RiseShaderProgram;
import net.augustus.client.render.shader.ShaderRenderType;
import net.augustus.client.render.shader.ShaderUniforms;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class MainMenuBackgroundShader extends RiseShader {

    private final RiseShaderProgram program = new RiseShaderProgram("main_menu.frag", "vertex.vsh");

    private Framebuffer tempFBO = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

    @Override
    public void run(ShaderRenderType type, float partialTicks, List<Runnable> runnable) {
        // Prevent rendering
        if (!Display.isVisible()) {
            return;
        }

        if (type == ShaderRenderType.OVERLAY) {
            this.update();

            // program ids
            final int programID = this.program.getProgramId();
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableAlpha();

            mc.getFramebuffer().bindFramebuffer(true);
            this.program.start();
            ShaderUniforms.uniform2f(programID, "iResolution", mc.displayWidth, mc.displayHeight);
            ShaderUniforms.uniform2f(programID,"iMouse",0,0);
            ShaderUniforms.uniform1f(programID, "iTime", (System.currentTimeMillis() - mc.startTime) / 1000F);
            RiseShaderProgram.drawQuad();
            RiseShaderProgram.stop();
        }
    }

    @Override
    public void update() {
        // can be true since this is only called in gui screen
        this.setActive(true);

        if (mc.displayWidth != tempFBO.framebufferWidth || mc.displayHeight != tempFBO.framebufferHeight) {
            tempFBO.deleteFramebuffer();
            tempFBO = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        } else {
            tempFBO.framebufferClear();
        }
    }
}

