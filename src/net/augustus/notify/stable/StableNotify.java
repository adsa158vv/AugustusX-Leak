package net.augustus.notify.stable;

import net.augustus.font.testfontbase.FontUtil;
import net.augustus.notify.NotificationType;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.tenacity.RoundedUtil;
import net.augustus.utils.skid.tomorrow.AnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

import static net.augustus.utils.interfaces.MM.mm;

/**
 * @author Genius
 * @since 2024/5/12 12:31
 * IntelliJ IDEA
 */

public class StableNotify {

    public float x,x1;
    public float width, height;

    public String text;

    public NotificationType type;

    public TimeHelper timer;

    public float lastTime;

    public boolean setBack;
    private float fy, cy = 0;
    private final TimeHelper anitimer = new TimeHelper();

    private final TimeHelper timeHelper = new TimeHelper();
    private final AnimationUtils animationUtils = new AnimationUtils();
    private final AnimationUtils animationUtils2 = new AnimationUtils();

    public StableNotify(String name, NotificationType type) {
        this.text = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;
    }

    public StableNotify(String name, NotificationType type, float lastTime) {
        this.text = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = FontUtil.tenacity_blod16.getStringWidth(name) + 25;
        this.height = 20;

    }


    public void render(float y) {
        String text2 = null;
        Color c = new Color(-1);

        float radius = (float) mm.notifications.radius.getValue();

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

        RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 70, cy, 88, 30, radius, new Color(0x88020202, true));


        switch (type) {
            case ToggleOn: {
                c = new Color(0,255,41,160);
                text2 = " Toggled";
                RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 65, cy + 5, 5, 20, 2.1f, new Color(0, 255, 41, 160));
                if (this.x1 != -1) {
                    RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 70, cy, this.x1, 30, radius, new Color(0x6BFFFFFF, true));
                }
                break;
            }
            case ToggleOff: {
                c = new Color(253,0,0,155);
                text2 = " Disabled";
                RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 65, cy + 5, 5, 20, 2.1f, new Color(253, 0, 0, 155));
                if (this.x1 != -1) {
                    RoundedUtil.drawRound((float) (sr.getScaledWidth_double() - x) - 70, cy, this.x1, 30, radius, new Color(0x65FFFFFF, true));
                }
                break;
            }
        }

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

            if (!setBack) {
                x = animationUtils2.animate(width, x, 0.1f);
            } else {
                x = animationUtils2.animate(0, x, 0.1f);
            }
            anitimer.reset();
        }
    }


}
