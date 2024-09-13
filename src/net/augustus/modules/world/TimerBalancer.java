package net.augustus.modules.world;

import net.augustus.events.EventMove;
import net.augustus.events.EventRender2D;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.tenacity.RoundedUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * @author Genius
 * @since 2024/6/8 13:01
 * IntelliJ IDEA
 */

public class TimerBalancer extends Module {


    public TimerBalancer() {
        super("TimerBalancer", new Color(-1), Categorys.WORLD);
    }

    private DoubleValue timer = new DoubleValue(1, "TimerSpeed", this, 4F, 0.1F, 10F, 1); // Timer速度
    private DoubleValue time = new DoubleValue(2, "TimerMaxSet", this, 5, 0, 30, 1); // 等待积攒时间
    private DoubleValue tie = new DoubleValue(3, "TimerSet", this, 5, 0, 30, 1); // 释放时间

    private BooleanValue counter = new BooleanValue(4, "Counter", this, true); // 渲染框架
    private StringValue setaX = new StringValue(5, "StartXMode", this, "-", new String[]{"-", "+"});
    private DoubleValue xV = new DoubleValue(6, "StartX", this, 75, 0, 450, 1);
    private StringValue setY = new StringValue(7, "StartYMode", this, "+", new String[]{"-", "+"});
    private DoubleValue yV = new DoubleValue(8, "StartY", this, 20, 0, 450, 1);

    private ColorSetting fontColor = new ColorSetting(155, "FontColor", this, new Color(-1));

    //private DoubleValue fontR = new DoubleValue(9,"FontR", this, 255, 0, 255,1);
    //private DoubleValue fontG = new DoubleValue(10,"FontG", this, 255, 0, 255,1);
    //private DoubleValue fontB = new DoubleValue(11,"FontB", this, 255, 0, 255,1);
    //private DoubleValue fontA = new DoubleValue(12,"FontA", this,255, 0, 255,1);

    private ColorSetting rectangleColor = new ColorSetting(1155, "RectangleColor", this, new Color(-1));

    private StringValue setFrame = new StringValue(17, "FrameMode", this, "^", new String[]{"<", "^"});

    private ColorSetting frameColor = new ColorSetting(1554, "FrameColor", this, new Color(-1));
    private BooleanValue suffix = new BooleanValue(1112,"Suffix",this,true);
    private BooleanValue onWorld = new BooleanValue(114314,"DisableOnWorld",this,true);


    private long lastMS = 0L;
    private int s = 0;
    private int m = 0;
    private boolean timr = false;
    private float progress = 0f;
    @EventTarget
    public void onEventWorld(EventWorld eventWorld) {
        if (onWorld.getBoolean()) {
            this.setToggled(false);
        }
    }
    @EventTarget
    public void setSuf(EventMove eventMove) {
        this.setSuffix("T:"+timer.getValue()+" H:"+this.time.getValue()+" L"+this.tie.getValue(),this.suffix.getBoolean());
    }
    @Override
    public void onEnable() {
        progress = 0f;
    }

    @Override
    public void onDisable() {
        s = 0;
        m = 0;
        timr = false;
        mc.timer.timerSpeed = 1f;
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (timr) {
            s++;
        } else {
            m++;
        }
        if (m == (int) time.getValue()) {
            timr = true;
            mc.timer.timerSpeed = (float) timer.getValue();
            m = 0;
            s = 0;
        }
        if (s == (int) tie.getValue()) {
            timr = false;
            mc.timer.timerSpeed = 1F;
            m = 0;
            s = 0;
        }
    }

    /*
    @EventTarget
    public void onTick(EventTick eventTick) {
        Minecraft mc = Minecraft.getMinecraft(); // 假设Minecraft实例可以通过getInstance()获取
        try {
            Field field = mc.timer.getClass().getDeclaredField("field_74277_g");
            //Field field = mc.wrapped.timer.getClass().getDeclaredField("field_74277_g");
            field.setAccessible(true);
            long t = field.getLong(mc.timer);
            field.setLong(mc.timer, t + 50L);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

     */

    @EventTarget
    public void onRender2D(EventRender2D eventRender2D) {
        progress = (System.currentTimeMillis() - lastMS) / 100f;
        if (progress >= 1) progress = 1f;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        boolean counterMode = counter.getBoolean();
        double startX = (setaX.getSelected().equals("-")) ? scaledResolution.getScaledWidth() / 2 - xV.getValue() : scaledResolution.getScaledWidth() / 2 + xV.getValue();
        double startY = setY.getSelected().equals("-") ? scaledResolution.getScaledHeight() / 2 - yV.getValue() : scaledResolution.getScaledHeight() / 2 + yV.getValue();
        ;

        if (counterMode) {
            //RenderUtil.drawShadow(startX, startY, 160, 21)
            GlStateManager.resetColor();

            RoundedUtil.drawRound((float) startX, (float) startY, 160f, 22f, 3F, rectangleColor.getColor());
            if (counterMode) {
                switch (setFrame.getSelected().toLowerCase()) {
                    case "^" :{
                        RoundedUtil.drawRound((float) startX, (float) startY, 160f, 3f, 3F, frameColor.getColor());
                        GlStateManager.resetColor();
                        FontUtil.tenacity_blod16.drawString("Timer：" + m +" ReleaseTime：" + s, (startX - 4 + 36), (startY + 7.5), fontColor.getColor().getRGB());
                        GlStateManager.resetColor();
                    }
                    case"<" :{
                        RoundedUtil.drawRound((float) startX,(float) startY, 3f, 22f, 3F, frameColor.getColor());
                        GlStateManager.resetColor();
                        FontUtil.tenacity_blod16.drawString("Timer：" + m +" ReleaseTime：" + s, (startX - 4 + 36), (startY + 7.5), fontColor.getColor().getRGB());
                        GlStateManager.resetColor();
                    }
                }
                GlStateManager.resetColor();
            }
        }
    }
}
