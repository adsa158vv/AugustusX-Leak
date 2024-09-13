package net.augustus.modules.movement;

import net.augustus.events.EventTick;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class EntitySpeed extends Module {

    public EntitySpeed() {
        super("EntitySpeed", new Color(-1), Categorys.MOVEMENT);
    }


    @EventTarget
    public void onTicks(EventTick eventTick) {
        int collisions = 0;
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(1.0, 1.0, 1.0); // grow方法改为expand方法
        for (Entity entity : mc.theWorld.loadedEntityList) {
            AxisAlignedBB entityBox = entity.getEntityBoundingBox();
            if (canCauseSpeed(entity) && box.intersectsWith(entityBox)) { // intersects方法改为intersectsWith方法
                collisions++;
            }
        }

        // Grim gives 0.08 leniency per entity.
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double boost = 0.08 * collisions;
        mc.thePlayer.motionX += -Math.sin(yaw) * boost;
        mc.thePlayer.motionZ += Math.cos(yaw) * boost;
    }

    private boolean canCauseSpeed(Entity entity) {
        return entity != mc.thePlayer && entity instanceof EntityLivingBase && !(entity instanceof EntityArmorStand);
    }

}
