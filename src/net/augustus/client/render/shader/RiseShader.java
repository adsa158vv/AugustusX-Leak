package net.augustus.client.render.shader;


import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.util.List;

@Getter
@Setter
public abstract class RiseShader {
    public Minecraft mc=Minecraft.getMinecraft();
    private boolean active;

    public abstract void run(ShaderRenderType type, float partialTicks, List<Runnable> runnable);

    public abstract void update();
}
