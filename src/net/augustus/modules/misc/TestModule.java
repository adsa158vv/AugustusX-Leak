package net.augustus.modules.misc;

import net.augustus.clickgui.clickguis.ClickGui;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.KeyPressUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.item.*;
import net.minecraft.util.*;

import java.awt.*;

public class TestModule extends Module {

    public BooleanValue currentscreen1 = new BooleanValue(3576, "CurrentScreen", this , false);
    public BooleanValue clickguichk = new BooleanValue(784, "ClickGuiCheck", this , false);
    private final BooleanValue a123c = new BooleanValue(13786,"KA",this,false);
    private final BooleanValue disi = new BooleanValue(4627,"DisablerInstanceCheck",this,false);
    private final BooleanValue preorpost = new BooleanValue(2874,"PreOrPost",this,false);
    private final BooleanValue isUITMKey = new BooleanValue(11699,"IsUITMKey",this,true);
    private final BooleanValue isAbleFly = new BooleanValue(2062,"IsAbleFly",this,true);

    public TestModule() {
        super("TestModule", Color.RED, Categorys.MISC);
    }
    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {
        if (isUITMKey.getBoolean()) {
            mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("IsUsingItemKey = "+isUsingItemKey()));
        }
        if (currentscreen1.getBoolean()) mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Current Screen is"+" "+mc.currentScreen));
        if (clickguichk.getBoolean()) {
            if ((mc.currentScreen instanceof ClickGui)) {
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("1 You are in ClickGui"));
            }
            if (!(mc.currentScreen instanceof ClickGui)) {
                mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("2 You are not in ClickGui"));
            }
        }
        if (a123c.getBoolean()){
            LogUtil.addChatMessage("Right= "+KeyPressUtil.isPressingRight());

        }
        if (isAbleFly.getBoolean()) {
            LogUtil.addChatMessage("AllowFlying: "+mc.thePlayer.capabilities.allowFlying);
        }



    }
    private boolean isUsingItemKey() {
        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isEntityAlive() && mc.currentScreen == null) {
            return (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBucketMilk) && mc.gameSettings.keyBindUseItem.isKeyDown();
        }
        else return false;
    }

    @EventTarget
    public void onEventMove(EventMove event) {
        if (preorpost.getBoolean()) {
            LogUtil.addChatMessage("EventMovePre: " + event.PRE);
        }
    }


}
