package net.augustus.modules.special;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.PlayerUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.*;

import java.awt.*;

public class IPNoSlow extends Module {
    public final BooleanValue sword = new BooleanValue(1,"Sword",this,true);
    public final BooleanValue food = new BooleanValue(2,"Food",this,true);

    public final BooleanValue sword_SendC0C = new BooleanValue(3,"SwordSendC0C",this,false);
    public final BooleanValue food_SendC0C = new BooleanValue(35,"FoodSendC0C",this,false);
    public final BooleanValue food_Strict = new BooleanValue(13,"FoodStrict",this,false);
    public final BooleanValue food_SendC0C_Allow = new BooleanValue(13,"C0CAllow",this,false);


    private boolean lastUsingRestItem = false;
    private boolean shouldSword = false;
    private boolean shouldFood = false;
    public IPNoSlow() {
        super("IPNoSlow", Color.RED, Categorys.SPECIAL);
    }
    @Override
    public void onDisable() {
        super.onDisable();
        lastUsingRestItem = false;
        shouldSword = false;
        shouldFood = false;
    }
    @Override
    public void onEnable() {
        super.onEnable();
        lastUsingRestItem = false;
        shouldFood = false;
        shouldSword = false;
    }

    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        lastUsingRestItem = false;
        shouldFood = false;
        shouldSword = false;
    }

    @EventTarget
    public void onEventNoSlow(EventSlowDown eventSlowDown) {
        if (this.sword.getBoolean() && PlayerUtil.isHoldingSword() && PlayerUtil.isUsingItemB() && shouldSword) {
            eventSlowDown.setMoveForward(1);
            eventSlowDown.setMoveStrafe(1);
            eventSlowDown.setSprint(true);
        }
        else if (this.sword.getBoolean() && PlayerUtil.isHoldingSword() && PlayerUtil.isUsingItemB()) {
            eventSlowDown.setMoveForward(0.2f);
            eventSlowDown.setMoveStrafe(0.2f);
            eventSlowDown.setSprint(false);
        }
        if (this.food.getBoolean() && PlayerUtil.isHoldingFood() && PlayerUtil.isUsingItemB() && shouldFood) {
            eventSlowDown.setMoveStrafe(1);
            eventSlowDown.setMoveForward(1);
            eventSlowDown.setSprint(true);
        }
        else if (this.food.getBoolean() && PlayerUtil.isHoldingFood() && PlayerUtil.isUsingItemB()) {
            eventSlowDown.setMoveForward(0.2f);
            eventSlowDown.setMoveStrafe(0.2f);
            eventSlowDown.setSprint(false);
        }

    }
    @EventTarget
    public void onPreMotion(EventPreMotion eventPreMotion) {
        if (!PlayerUtil.isUsingItemB()) {
            lastUsingRestItem = false;
            shouldSword = false;
            shouldFood = false;
            return;
        }

            if (PlayerUtil.isHoldingFood()) {
                if (this.food.getBoolean()) {
                    if (!lastUsingRestItem) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
                        shouldFood = true;
                    }
                    if (this.food_SendC0C.getBoolean()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(0, 0.82f, false, false));
                        if (food_SendC0C_Allow.getBoolean()) {
                           shouldFood = true;
                        }
                    }
                    lastUsingRestItem = true;
                    if (!this.food_Strict.getBoolean()) {
                        shouldFood = true;
                    }
                }
            } else {
                lastUsingRestItem = false;
                shouldFood = false;
                if (PlayerUtil.isHoldingSword() && sword.getBoolean()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    if (sword_SendC0C.getBoolean()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(0, 0.82f, false, false));
                    }
                    shouldSword = true;
                } else shouldSword = false;
            }

    }

}
