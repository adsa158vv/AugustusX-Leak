package net.augustus.modules.combat;


import net.augustus.events.EventRayTraceRange;
import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class Reach extends Module {



    double attack_CorrectedRange;
    double place_CorrectedRange;
    public Reach() {
        super("Reach", Color.BLUE, Categorys.COMBAT);
    }
    public DoubleValue attackRange = new DoubleValue(12712,"AttackRange",this,3,0,6,3);
    public DoubleValue placeRange = new DoubleValue(6068,"PlaceRange",this,3,0,6,3);
    public BooleanValue fixServerMisplace = new BooleanValue(1502,"FixServerMisplace",this,true);
    private BooleanValue suffix = new BooleanValue(11272,"Suffix",this,false);
    @EventTarget
    public void onEventTick(EventTick eventTick) {

            this.setSuffix("A:" + attackRange.getValue() + " P:" + placeRange.getValue() + " " + fixServerMisplace.getBoolean(), suffix.getBoolean());

    }
    @EventTarget
    public void onEventRayTraceRange(EventRayTraceRange eventRayTraceRange) {
        attack_CorrectedRange = this.attackRange.getValue() + 0.00256f;
        place_CorrectedRange = this.placeRange.getValue() + 0.00256f;
        if (this.fixServerMisplace.getBoolean()) {
            final float n = 0.010625f;
            if (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH || mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST) {
                attack_CorrectedRange += n * 2.0f;
                place_CorrectedRange += n * 2.0f;
            }
        }
        eventRayTraceRange.setRange(attack_CorrectedRange);
        eventRayTraceRange.setBlockReachDistance(Math.max(mc.playerController.getBlockReachDistance(), place_CorrectedRange));
    }
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



}
