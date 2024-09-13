package net.augustus.notify.double_;

import net.augustus.font.CustomFontUtil;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.notify.NotificationType;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.interfaces.MM;
import net.augustus.utils.skid.tenacity.RenderUtil;
import net.augustus.utils.skid.tomorrow.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class DoubleNotification implements MM {
    public double delete;
    public float x;
    public float width, height;
    public String name;
    public float lastTime;
    public TimeHelper timer;
    public NotificationType type;
    public boolean setBack;
    private float fy, cy = 0;
    private final TimeHelper anitimer = new TimeHelper();
    private static CustomFontUtil tenacity_blod_18 = FontUtil.tenacity_blod18;
    private final AnimationUtils animationUtils = new AnimationUtils();
    private final AnimationUtils animationUtils2 = new AnimationUtils();


    public DoubleNotification(String name, NotificationType type) {
        this.name = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = tenacity_blod_18.getStringWidth(name) + 25;
        this.height = 20;
        this.delete = 0;
    }

    public DoubleNotification(String name, NotificationType type, float lastTime) {
        this.name = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = tenacity_blod_18.getStringWidth(name) + 25;
        this.height = 20;
        this.delete = 0;
    }

    public void render(float y) {
        fy = y;
        if (cy == 0) {
            cy = fy + 25;
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        //RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, new Color(10, 10, 10, 220));
        //RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy + height - 1, (float) (sr.getScaledWidth_double() - x) + width, cy + height, new Color(180, 180, 180));

        if (delete > ((sr.getScaledWidth_double() - x) + width)) {
            delete = ((sr.getScaledWidth_double() - x) + width);
        }

        if(!(mm.shaders.isToggled() && mm.shaders.blur.getBoolean())) {
            switch (type) {
                case ToggleOn:
                    RenderUtil.drawGradientRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width) - delete, cy + height, new Color(104,246,35,232).getRGB(), new Color(104, 246, 35, 232).getRGB());
                    break;
                case ToggleOff:
                    RenderUtil.drawGradientRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width) - delete, cy + height, new Color(255, 0, 0, 255).getRGB(), new Color(255, 0, 0, 255).getRGB());
                    break;
            }
            net.augustus.utils.skid.tomorrow.RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, new Color(10, 10, 10, 120));
        }

        //arial18.drawString(name, (sr.getScaledWidth() - x) + 18, cy + 7, -1);
        tenacity_blod_18.drawString(name, (sr.getScaledWidth() - x) + 8 , cy+ 7, -1);
    }

    public void update() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (timer == null && Math.abs(x - width) < 0.1f) {
            timer = new TimeHelper();
            timer.reset();
        }
        if (anitimer.reached(10)) {
            cy = animationUtils.animate(fy, cy, 0.1f);

            delete += 1;

            if (!setBack) {
                x = animationUtils2.animate(width, x, 0.1f);
            } else {
                x = animationUtils2.animate(0, x, 0.1f);
            }
            anitimer.reset();
        }
    }

    public void shader() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        switch (type) {
            case ToggleOn:
                RenderUtil.drawGradientRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width) - delete, cy + height, new Color(104,246,35,232).getRGB(), new Color(104, 246, 35, 232).getRGB());
                break;
            case ToggleOff:
                RenderUtil.drawGradientRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width) - delete, cy + height, new Color(245, 18, 18, 224).getRGB(), new Color(245, 18, 18, 224).getRGB());
                break;
        }
        Gui.drawRect2((float) (sr.getScaledWidth_double() - x), cy, width, height, -1);
    }
}