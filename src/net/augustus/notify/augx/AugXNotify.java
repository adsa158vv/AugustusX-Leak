package net.augustus.notify.augx;

import net.augustus.font.testfontbase.FontUtil;
import net.augustus.notify.NotificationType;
import net.augustus.utils.ResourceUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.tenacity.RoundedUtil;
import net.augustus.utils.skid.tomorrow.AnimationUtils;
import net.augustus.utils.skid.tomorrow.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static net.augustus.utils.interfaces.MM.mm;

/**
 * @author Genius
 * @since 2024/5/1 12:37
 * IntelliJ IDEA
 */

public class AugXNotify {

    public float x;
    public float width, height;

    public String text;

    public NotificationType type;

    public TimeHelper timer;

    public float lastTime;

    public boolean setBack;
    private float fy, cy = 0;
    private final TimeHelper anitimer = new TimeHelper();
    private final AnimationUtils animationUtils = new AnimationUtils();
    private final AnimationUtils animationUtils2 = new AnimationUtils();
    private final ResourceLocation on = ResourceUtil.loadResourceLocation("net/augustus/notify/augx/res/on.png", "on");

    private final ResourceLocation off = ResourceUtil.loadResourceLocation("net/augustus/notify/augx/res/off.png", "off");

    public AugXNotify(String name, NotificationType type) {
        this.text = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;
    }

    public AugXNotify(String name, NotificationType type, float lastTime) {
        this.text = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;

    }


    public void render(float y) {

        float radius = (float) mm.notifications.radius.getValue();

        fy = y;
        if (cy == 0) {
            cy = fy + 25;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        //RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, radius,new Color(10, 10, 10, 220));
        //RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, 1.5f,new Color(10, 10, 10, 220));
        //RenderUtil.drawCustomImageAlpha(sr.getScaledWidth() - x + 3, cy + 4, 12, 12, new ResourceLocation("client/switchBack.png"), -1, 255);
        switch (type) {
            case ToggleOn: {
                RenderUtil.drawCustomImageAlpha(sr.getScaledWidth() - x + 3, cy + 4, 12, 12, on, -1, 255);
                RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x), cy, 120, 20, radius,new Color(0, 255, 41, 160));
                break;
            }
            case ToggleOff: {
                RenderUtil.drawCustomImageAlpha(sr.getScaledWidth() - x + 3, cy + 4, 12, 12, off, -1, 255);
                RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x), cy, 120, 20, radius,new Color(253, 0, 0, 155));
                break;
            }
        }

        FontUtil.tenacity_blod16.drawString(text, (sr.getScaledWidth() - x) + 18, cy + 7, -1);

    }

    public void update() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (timer == null && Math.abs(x - width) < 0.1f) {
            timer = new TimeHelper();
            timer.reset();
        }
        if (anitimer.reached(10)) {
            cy = animationUtils.animate(fy, cy, 0.1f);

            if (!setBack) {
                x = animationUtils2.animate(width, x, 0.1f);
            } else {
                x = animationUtils2.animate(0, x, 0.1f);
            }
            anitimer.reset();
        }
    }


}
