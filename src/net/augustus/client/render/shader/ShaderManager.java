package net.augustus.client.render.shader;

import net.augustus.client.render.shader.shaders.GaussianBlurShader;
import net.augustus.client.render.shader.shaders.MainMenuBackgroundShader;

public class ShaderManager {
    public static final MainMenuBackgroundShader mainMenuBackgroundShader=new MainMenuBackgroundShader();
    public static final GaussianBlurShader GaussianBlurShader=new GaussianBlurShader();
}
