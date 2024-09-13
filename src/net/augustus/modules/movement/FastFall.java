package net.augustus.modules.movement;

import net.augustus.events.EventRender3D;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.PlayerUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

import java.awt.*;

public class FastFall extends Module {
    public StringValue mode = new StringValue(112183,"Mode",this,"Polar",new String[]{"Polar","Timer"});
    public DoubleValue fallheightCheck = new DoubleValue(112185,"HeightCheck",this,5,1,24,2);
    public BooleanValue checkVoid = new BooleanValue(112184,"CheckVoid",this,false);

    public FastFall(){super("FastFall", new Color(123, 240, 120), Categorys.MOVEMENT);}
    private int ticks = 0;
    private int tick2 = 0;


    @EventTarget
    public void upDate(EventUpdate e){
        if (mc.thePlayer !=null){
            if (mc.thePlayer.onGround){
                ticks =0;
                if (mm.fastFall.mode.getSelected().equals("Timer")) {
                    tick2++;
                    if (tick2 == 2) {
                        mc.timer.timerSpeed = 1f;
                    }
                }
            }
        }
    }
    @EventTarget
    public void onEventTick(EventTick eventTick){
        if (mc.thePlayer != null) {
            if (!checkVoid.getBoolean() || shouldFastFall()) {
                String var2 = this.mode.getSelected();
                switch (var2) {
                    case "Polar": {
                        if (mc.thePlayer.fallDistance >= fallheightCheck.getValue()) {
                            if (mc.thePlayer.motionY <= -0.10) {
                                ticks++;
                                if (ticks == 2) {
                                    mc.thePlayer.motionY = -0.2;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventTarget
    public void onRender(EventRender3D e){
        if (mc.thePlayer != null){
            if (!checkVoid.getBoolean() || shouldFastFall()) {
                String var2 = this.mode.getSelected();
                switch (var2) {
                    case "Timer": {
                        if (mc.thePlayer.fallDistance >= fallheightCheck.getValue()){
                            tick2 =0;
                            mc.timer.timerSpeed =10f;
                        }
                    }
                }
            }
        }
    }
    private boolean shouldFastFall() {
        if (!mm.longJump.isToggled() && !mm.fly.isToggled()) {
            double posX = mc.thePlayer.posX;
            double posY = mc.thePlayer.posY;
            double posZ = mc.thePlayer.posZ;
            double motionX = mc.thePlayer.motionX;
            double motionY = mc.thePlayer.motionY;
            double motionZ = mc.thePlayer.motionZ;
            boolean isJumping = mc.thePlayer.isJumping;
            for(int i = 0; i < 200; ++i) {
                double[] doubles = PlayerUtil.getPredictedPos(
                        mc.thePlayer.movementInput.moveForward, mc.thePlayer.movementInput.moveStrafe, motionX, motionY, motionZ, posX, posY, posZ, isJumping
                );
                isJumping = false;
                posX = doubles[0];
                posY = doubles[1];
                posZ = doubles[2];
                motionX = doubles[3];
                motionY = doubles[4];
                motionZ = doubles[5];
                BlockPos b = new BlockPos(posX, posY, posZ);
                Block block = mc.theWorld.getBlockState(b).getBlock();
                if (!(block instanceof BlockAir)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }
}
