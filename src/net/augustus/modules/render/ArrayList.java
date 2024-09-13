package net.augustus.modules.render;

import java.awt.*;

import net.augustus.events.EventChangeGuiScale;
import net.augustus.events.EventClickSetting;
import net.augustus.events.EventRender2D;
import net.augustus.font.CustomFontUtil;
import net.augustus.font.UnicodeFontRenderer;
import net.augustus.font.testfontbase.FontUtil;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.RenderUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayList extends Module {
    public UnicodeFontRenderer vegaFontRenderer;
    public ColorSetting staticColor = new ColorSetting(5013, "CustomColor", this, Color.blue);
    public StringValue mode = new StringValue(15038, "Mode", this, "Default", new String[]{"Default", "Vega", "New"});
    public StringValue sideOption = new StringValue(2678, "Side", this, "Right", new String[]{"Left", "Right"});
    public StringValue sortOption = new StringValue(5222, "Sorting", this, "Length", new String[]{"Alphabet", "Length"});
    public StringValue font = new StringValue(23, "Font", this, "Minecraft", new String[]{"Minecraft", "Verdana", "Arial", "Roboto", "Comfortaa", "ESP", "Tenacity"});
    public BooleanValue randomColor = new BooleanValue(7056, "RandomColor", this, true);
    public BooleanValue underline = new BooleanValue(70567, "UnderLine", this, true);

    public BooleanValue rainbow = new BooleanValue(12530, "Rainbow", this, false);
    // public DoubleValue rainbowSpeed = new DoubleValue(9495, "RainbowSpeed", this, 55.0, 0.0, 1000.0, 0);
    public BooleanValue toggleSound = new BooleanValue(16027, "ToggleSound", this, true);
    public BooleanValue suffix = new BooleanValue(7873, "Suffix", this, true);

    public BooleanValue backGround = new BooleanValue(15756, "Backgound", this, true);
    public ColorSetting vegaColor1 = new ColorSetting(8222, "VegaColor1", this, Color.WHITE);
    public ColorSetting vegaColor2 = new ColorSetting(16341, "VegaColor2", this, Color.BLACK);
    public ColorSetting backGroundColor = new ColorSetting(5146, "BackGroundColor", this, new Color(0, 0, 0, 100));

    public DoubleValue x1 = new DoubleValue(2739, "PosX", this, 0.0, 0.0, 2000.0, 2);
    public DoubleValue y1 = new DoubleValue(12776, "PosY", this, 0.0, 0.0, 2000.0, 2);
    public DoubleValue x2 = new DoubleValue(10004, "PosX2", this, 0.0, 0.0, 2000.0, 2);
    public DoubleValue y2 = new DoubleValue(11924, "PosY2", this, 0.0, 0.0, 2000.0, 2);
    private CustomFontUtil customFont = new CustomFontUtil("Verdana", 16);
    private final FontRenderer fontRenderer = mc.fontRendererObj;

    public ArrayList() {
        super("ArrayList", Color.CYAN, Categorys.RENDER);

        this.x1.setVisible(false);
        this.y1.setVisible(false);
        this.x2.setVisible(false);
        this.y2.setVisible(false);
    }

    @Override
    public void onEnable() {
    }

    public void renderArrayList() {
        ScaledResolution sr = new ScaledResolution(mc);
        boolean leftSide = this.sideOption.getSelected().equals("Left");
        float x = (float)this.x1.getValue();
        if (leftSide) {
            x = (float)sr.getScaledWidth();
        }
        float y = (float) this.y1.getValue() + 2.0F;
        if(sideOption.getSelected().equalsIgnoreCase(mm.hud.side.getSelected())) {
            switch (mm.hud.mode.getSelected()) {
                case "Ryu": {
                    y = mc.fontRendererObj.FONT_HEIGHT + 5F;
                    break;
                }
                case "Old": {
                    y = 48F;
                    break;
                }
                case "Other": {
                    y = (float)((int)((double)((float)mc.fontRendererObj.FONT_HEIGHT * 1.75F + 4.0F + 1.0F) * 1.5));
                    break;
                }
                case "Basic": {
                    y = (float)(mc.fontRendererObj.FONT_HEIGHT + 1);
                    break;
                }
            }
        }
        int i = 0;
        double yMargin = 1.0;
        int backGroundColor = this.backGroundColor.getColor().getRGB();
        boolean minecraftFont = this.font.getSelected().equals("Minecraft");
        int height = (int)((double)(minecraftFont ? this.fontRenderer.FONT_HEIGHT : Math.round(this.customFont.getHeight())) + yMargin * 2.0);
        int color = this.staticColor.getColor().getRGB();
        java.util.ArrayList<Module> moduleArrayList;

        moduleArrayList = mm.getActiveModules();

        for(Module module : moduleArrayList) {
            String moduleName = module.getDisplayName();
            x = leftSide ? (float)this.x1.getValue() + 1.0F : (float)sr.getScaledWidth();
            module.setModuleWidth(
                    this.font.getSelected().equals("Minecraft")
                            ? (float)this.fontRenderer.getStringWidth(moduleName)
                            : this.customFont.getStringWidth(moduleName) - this.customFont.getStringWidth(FontRenderer.getFormatFromString(moduleName))
            );
            if (!leftSide) {
                x -= module.getModuleWidth() + 3.0F;
            }

            if (this.backGround.getBoolean()) {
                RenderUtil.drawRect(
                        x + (float)(leftSide ? -1 : 0),
                        (double)y - yMargin,
                        x + module.getModuleWidth() + (float)(leftSide ? 1 : 3),
                        (double)(y + (float)height) - yMargin,
                        backGroundColor
                );
            }

            if (this.randomColor.getBoolean()) {
                color = module.getColor().getRGB();
            }

            if (minecraftFont) {
                this.fontRenderer.drawStringWithShadow(moduleName, x + (float)(leftSide ? 0 : 1), y + 1.0F, color);
            } else {
                String string = FontRenderer.getFormatFromString(moduleName);
                int ii = 0;
                if (string.length() >= 2) {
                    ii = this.fontRenderer.getColorCode(string.charAt(1));
                }

                float red = (float)(ii >> 16 & 0xFF) / 255.0F;
                float green = (float)(ii >> 8 & 0xFF) / 255.0F;
                float blue = (float)(ii & 0xFF) / 255.0F;
                if (!string.equals("") && moduleName.contains(string)) {
                    String[] s = moduleName.split(string);
                    this.customFont.drawString(s[0], x + (float)(leftSide ? 0 : 1), y, color);
                    this.customFont
                            .drawString(
                                    s[1], x + (float)(leftSide ? 0 : 1) + this.customFont.getStringWidth(s[0]), y, new Color(red, green, blue).getRGB()
                            );
                } else {
                    this.customFont.drawString(moduleName, x + (float)(leftSide ? 0 : 1), y, color);
                }
            }

            ++i;
            y += (float)height;
        }
    }
    //private UnicodeFontRenderer caseMinecraft = new UnicodeFontRenderer(FontUtil.getFont((mc.fontRendererObj.locationFontTextureBase.getResourcePath()),(int) 16));
    private UnicodeFontRenderer caseVardana = new UnicodeFontRenderer(FontUtil.getFont("verdana.ttf", (int) 16));

    private UnicodeFontRenderer caseRoboto = new UnicodeFontRenderer(FontUtil.getFont("roboto.ttf", (int) 16));
    ;
    private UnicodeFontRenderer caseArial = new UnicodeFontRenderer(FontUtil.getFont("arial.ttf", (int) 16));
    private UnicodeFontRenderer caseComfortaa = new UnicodeFontRenderer(FontUtil.getFont("comfortaa.ttf", (int) 16));
    private UnicodeFontRenderer caseESP = new UnicodeFontRenderer(FontUtil.getFont("ESP.ttf", (int) 16));

    private UnicodeFontRenderer caseTenacity = new UnicodeFontRenderer(FontUtil.getFont("tenacity-bold.ttf", (int) 16));



    @EventTarget
    public void onRender2D(EventRender2D event) {
        String var2 = mm.arrayList.font.getSelected();
        switch(var2) {
            default:
                vegaFontRenderer = caseVardana;
                break;
            case "Verdana":
                vegaFontRenderer = caseVardana;
                break;
            case "Roboto":
                vegaFontRenderer = caseRoboto;
                break;
            case "Arial":
                vegaFontRenderer = caseArial;
                break;
            case "Comfortaa":
                vegaFontRenderer = caseComfortaa;
                break;
            case "ESP":
                vegaFontRenderer = caseESP;
                break;
            case "Tenacity":
                vegaFontRenderer = caseTenacity;
                break;


        }
        switch (mm.arrayList.mode.getSelected()) {

            case "Default": {
                mm.arrayList.renderArrayList();
                break;
            }

        }
    }

    public void updateSorting() {

        mm.getActiveModules().sort(Module::compareTo);

    }

    @EventTarget
    public void onEventClickSetting(EventClickSetting eventClickSetting) {
        if (!mm.arrayList.font.getSelected().equals("Minecraft") && !this.customFont.getFontName().equalsIgnoreCase(mm.arrayList.font.getSelected())) {
            System.out.println("Selecting " + mm.arrayList.font.getSelected());
            String var2 = mm.arrayList.font.getSelected();
            switch(var2) {
                case "Verdana":
                    this.customFont = FontUtil.verdana;
                    break;
                case "Roboto":
                    this.customFont = FontUtil.roboto;
                    break;
                case "Arial":
                    this.customFont = FontUtil.arial;
                    break;
                case "Comfortaa":
                    this.customFont = FontUtil.comfortaa;
                    break;
                case "ESP":
                    this.customFont = FontUtil.esp;
                    break;
                case "Tenacity":
                    this.customFont = FontUtil.tenacity_blod16;
                    break;
            }
        }

        this.updateSorting();
    }

    @EventTarget
    public void onEventChangeGuiScale(EventChangeGuiScale eventChangeGuiScale) {
        this.updateSorting();
    }

    public CustomFontUtil getCustomFont() {
        return this.customFont;
    }
}
