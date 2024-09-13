package net.augustus.utils.skid.atani;

import net.augustus.modules.combat.AntiBot;
import net.augustus.modules.misc.MidClick;
import net.augustus.utils.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static net.augustus.utils.interfaces.MM.mm;

public class FightUtil {

    public static boolean canHit(double chance) {
        return Math.random() <= chance;
    }

    public static List<EntityLivingBase> getMultipleTargets(double range, boolean players, boolean animals, boolean walls, boolean mobs, boolean invis) {
        List<EntityLivingBase> list = new ArrayList<>();
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (list.size() > 5)
                break;

            if (!(entity instanceof EntityLivingBase))
                continue;

            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (entityLivingBase == Minecraft.getMinecraft().thePlayer ||
                    getRange(entityLivingBase) > range
                    || !entityLivingBase.canEntityBeSeen(Minecraft.getMinecraft().thePlayer) && !walls
                    || entityLivingBase.isDead
                    || entityLivingBase instanceof EntityArmorStand
                    || entityLivingBase instanceof EntityVillager
                    || entityLivingBase instanceof EntityAnimal && !animals
                    || entityLivingBase instanceof EntitySquid && !animals
                    || entityLivingBase instanceof EntityMob && !mobs
                    || entityLivingBase instanceof EntitySlime && !mobs
                    || (mm.antiBot.isToggled() && AntiBot.isServerBot(entity))
                    || ((mm.midClick.isToggled()) && MidClick.friends.contains(entity) && !mm.midClick.noFiends)
                    || entityLivingBase.isInvisible() && !invis){
                continue;
            }

            list.add(entityLivingBase);
        }
        return list;
    }

    public static boolean isValid(EntityLivingBase entityLivingBase, double range, boolean invis, boolean players, boolean animals, boolean mobs) {

        if (entityLivingBase == null || entityLivingBase.isDead)
            return false;

        return !(getRange(entityLivingBase) > range
                || entityLivingBase.isDead
                || entityLivingBase instanceof EntityArmorStand
                || entityLivingBase instanceof EntityVillager
                || entityLivingBase instanceof EntityPlayer && !players
                || entityLivingBase instanceof EntityAnimal && !animals
                || entityLivingBase instanceof EntityMob && !mobs
                || entityLivingBase.isInvisible() && !invis
                || (mm.antiBot.isToggled() && AntiBot.isServerBot(entityLivingBase))
                || ((mm.midClick.isToggled()) && MidClick.friends.contains(entityLivingBase) && !mm.midClick.noFiends)
                || Minecraft.getMinecraft().theWorld.getEntityByID(entityLivingBase.getEntityId()) != entityLivingBase
                || entityLivingBase == Minecraft.getMinecraft().thePlayer
                ||  entityLivingBase == null
                || entityLivingBase.getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId());
    }

    // For esp
    public static boolean isValidWithPlayer(Entity entity, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(entity.isDead
                || entity instanceof EntityArmorStand
                || entity instanceof EntityVillager
                || entity instanceof EntityPlayer && !players
                || entity instanceof EntityAnimal && !animals
                || entity instanceof EntityMob && !mobs
                || entity.isInvisible() && !invis
                || (mm.antiBot.isToggled() && AntiBot.isServerBot(entity))
                || ((mm.midClick.isToggled()) && MidClick.friends.contains(entity) && !mm.midClick.noFiends)
                || Minecraft.getMinecraft().theWorld.getEntityByID(entity.getEntityId()) != entity
                || entity == null );
    }

    public static boolean isValidWithPlayer(Entity entity, float range, boolean invis, boolean players, boolean animals, boolean mobs) {
        return !(entity.isDead
                || entity == null
                || !(entity instanceof EntityLivingBase)
                || Minecraft.getMinecraft().thePlayer == entity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0
                || getRange(entity) > range
                || entity instanceof EntityArmorStand
                || entity instanceof EntityVillager
                || entity instanceof EntityPlayer && !players
                || entity instanceof EntityAnimal && !animals
                || entity instanceof EntityMob && !mobs
                || entity.isInvisible() && !invis
                || (mm.antiBot.isToggled() && AntiBot.isServerBot(entity))
                || ((mm.midClick.isToggled()) && MidClick.friends.contains(entity) && !mm.midClick.noFiends)
                || Minecraft.getMinecraft().theWorld.getEntityByID(entity.getEntityId()) != entity
                || entity == null );
    }

    public static double yawDist(EntityLivingBase e) {
        final Vec3 difference = e.getPositionVector().addVector(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(Minecraft.getMinecraft().thePlayer.getPositionVector().addVector(0.0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0));
        final double d = Math.abs(Minecraft.getMinecraft().thePlayer.rotationYaw - (Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f)) % 360.0f;
        return (d > 180.0f) ? (360.0f - d) : d;
    }

    public static double getRange(Entity entity) {
        if(Minecraft.getMinecraft().thePlayer == null)
            return 0;
        return Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0f).distanceTo(RotationUtil.getBestHitVec(Minecraft.getMinecraft().thePlayer,1f));
    }

    public static double getEffectiveHealth(EntityLivingBase entity) {
        return entity.getHealth() * (entity.getMaxHealth() / entity.getTotalArmorValue());
    }

}