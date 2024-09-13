package net.augustus.notify.custom;

import net.augustus.font.testfontbase.FontUtil;
import net.augustus.notify.NotificationType;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.tenacity.RoundedUtil;
import net.augustus.utils.skid.tomorrow.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author Genius
 * @since 2024/5/12 12:31
 * IntelliJ IDEA
 */

public class CustomNotify {

    public float x,x1;
    public float width, height;

    public String text;

    public float realWidth = 20;

    public NotificationType type;

    public TimeHelper timer;

    public TimeHelper timer1;

    public float lastTime;

    public int delect = 0;

    public boolean setBack;
    private float fy, cy = 0;
    private final TimeHelper anitimer = new TimeHelper();

    private final TimeHelper timeHelper = new TimeHelper();
    private final AnimationUtils animationUtils = new AnimationUtils();
    private final AnimationUtils animationUtils2 = new AnimationUtils();

    public CustomNotify(String name, NotificationType type) {
        this.text = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;
        this.delect = 0;
        this.realWidth = 20;
    }

    public CustomNotify(String name, NotificationType type, float lastTime) {
        this.text = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;
        this.delect = 0;
        this.realWidth = 20;
    }


    public void render(float y) {
        String text2 = null;
        Color c = new Color(-1);

        if (this.timeHelper.reached(20) && !(this.x == -1 | this.x1 >= 88)) {
            this.x1++;
            this.timeHelper.reset();
        } else if (this.x1 >= 88) {
            this.x1 = -1;
        }

        fy = y;
        if (cy == 0) {
            cy = fy + 45;
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        switch (type) {
            case ToggleOn: {
                text2 = " Enabled";
                c = new Color(27, 246, 61, 233);
                FontUtil.icon36.drawString("o", (float) (sr.getScaledWidth_double() - x) - 65, cy + 5, new Color(27, 246, 61, 233).getRGB());
                this.realWidth = FontUtil.tenacity_blod16.getStringWidth(text + text2);
                break;
            }
            case ToggleOff: {
                text2 = " Disabled";
                c = new Color(255, 20, 20, 228);
                FontUtil.icon36.drawString("p", (float) (sr.getScaledWidth_double() - x) - 65, cy + 5, new Color(255, 20, 20, 228).getRGB());
                this.realWidth = FontUtil.tenacity_blod16.getStringWidth(text + text2);
                break;
            }
        }

        RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 70, cy, this.realWidth, 30, 1, new Color(0x88020202, true));


        RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 70, cy + 29, this.realWidth - delect, 2, 1f, new Color(0xE127F1E1, true));

        FontUtil.tenacity_blod16.drawString(text, (sr.getScaledWidth() - x) - 55, cy + 10, -1);
        FontUtil.tenacity_blod16.drawString(text2, (sr.getScaledWidth() - x) - 53 + FontUtil.tenacity_blod16.getStringWidth(text), cy + 10, c.getRGB());


    }

    public void update() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (timer == null && Math.abs(x - width) < 0.1f) {
            timer = new TimeHelper();
            timer.reset();
        }
        if (anitimer.reached(10)) {
            cy = animationUtils.animate(fy, cy, 0.1f);

            if (delect >= this.realWidth) {
                delect = -1;
            }
            if (delect != -1) {
                delect++;
            }

            if (!setBack) {
                x = animationUtils2.animate(width, x, 0.1f);
            } else {
                x = animationUtils2.animate(0, x, 0.1f);
            }
            anitimer.reset();
        }
    }


}
