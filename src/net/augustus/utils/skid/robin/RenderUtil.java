package net.augustus.utils.skid.robin;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Genius
 * @since 2024/7/15 下午6:58
 * IntelliJ IDEA
 */

public class RenderUtil {

    public static void glColor(int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
    }

    public static void drawLoadingCircle(float x2, float y2) {
        for (int i = 0; i < 2; ++i) {
            int rot = (int)(System.nanoTime() / 5000000L * (long)i % 360L);
            RenderUtil.drawCircle(x2, y2, i * 8, rot - 180, rot);
        }
    }

    public static void drawCircle(float x2, float y2, float radius, int start, int end) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtil.glColor(Color.WHITE.getRGB());
        GL11.glEnable((int)2848);
        GL11.glLineWidth((float)3.0f);
        GL11.glBegin((int)3);
        for (float i = (float)end; i >= (float)start; i -= 4.0f) {
            GL11.glVertex2f((float)((float)((double)x2 + Math.cos((double)i * Math.PI / 180.0) * (double)(radius * 1.001f))), (float)((float)((double)y2 + Math.sin((double)i * Math.PI / 180.0) * (double)(radius * 1.001f))));
        }
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


}
