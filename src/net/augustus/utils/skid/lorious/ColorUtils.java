package net.augustus.utils.skid.lorious;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ColorUtils {
    public static void color(int color, float alpha) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void color(int color) {
        float f = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GlStateManager.color(f1, f2, f3, f);
    }

    public static double[] sRGBtoHSV(double r, double g, double b) {
        double max;
        double min = Math.min(Math.min(r, g), b);
        double v = max = Math.max(Math.max(r, g), b);
        double delta = max - min;
        if (max == 0.0) {
            double s = 0.0;
            double h = -1.0;
            return new double[]{h, s, v};
        }
        double s = delta / max;
        double h = r == max ? (g - b) / delta : (g == max ? 2.0 + (b - r) / delta : 4.0 + (r - g) / delta);
        h *= 60.0;
        if (h < 0.0) {
            h += 360.0;
        }
        return new double[]{h, s, v};
    }

    public static float[] rgbToHsv(int r, int g, int b) {
        int max = Math.max(r /= 255, Math.max(g /= 255, b /= 255));
        int min = Math.min(r, Math.min(g, b));
        int h = max;
        int s = max;
        int v = max;
        int d = max - min;
        int n = s = max == 0 ? 0 : d / max;
        if (max == min) {
            h = 0;
        } else {
            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            }
            if (max == g) {
                h = (b - r) / d + 2;
            }
            if (max == b) {
                h = (r - g) / d + 4;
            }
            h /= 6;
        }
        return new float[]{h, s, v};
    }

    public static Color HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0f) {
            g = b = (int)(brightness * 255.0f + 0.5f);
            r = b;
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int)h) {
                case 0: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(t * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    r = (int)(q * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(t * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(q * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    r = (int)(t * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(q * 255.0f + 0.5f);
                }
            }
        }
        return new Color(r, g, b);
    }

    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1.0) {
            double left = offset % 1.0;
            int off = (int)offset;
            offset = off % 2 == 0 ? left : 1.0 - left;
        }
        double inverse_percent = 1.0 - offset;
        int redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        int alphaPart = (int)((double)color1.getAlpha() * inverse_percent + (double)color2.getAlpha() * offset);
        return new Color(redPart, greenPart, bluePart, alphaPart);
    }

    public static Color getRainbow(float seconds, float sat, float bright) {
        float hue = (float)(System.currentTimeMillis() % (long)((int)(seconds * 1000.0f))) / (seconds * 1000.0f);
        return Color.getHSBColor(hue, sat, bright);
    }
}
