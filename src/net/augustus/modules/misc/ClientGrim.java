// Decompiled with: CFR 0.152
// Class Version: 8
package net.augustus.modules.misc;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.utils.skid.xylitol.DebugUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.text.DecimalFormat;


public class ClientGrim
        extends Module {

    public static final DecimalFormat DF_1 = new DecimalFormat("0.000000");
    int vl;

    public ClientGrim() {
        super("ClientGrim", new Color(189, 32, 110), Categorys.MISC);
    }

    @Override
    public void onEnable() {
        this.vl = 0;
    }

    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        this.vl = 0;
    }

    @EventTarget
    public void onPacket(EventReadPacket event) {
        if (0 == 0) {
            S19PacketEntityStatus s19;
            if (event.getPacket() instanceof S19PacketEntityStatus && (s19 = (S19PacketEntityStatus)event.getPacket()).getOpCode() == 2) {
                new Thread(() -> this.checkCombatHurt(s19.getEntity(ClientGrim.mc.theWorld))).start();
            }
            if (event.getPacket() instanceof S14PacketEntity ) {
                S14PacketEntity packet = (S14PacketEntity)event.getPacket();
                Entity entity = packet.getEntity(ClientGrim.mc.theWorld);
                if (!(entity instanceof EntityPlayer)) {
                    return;
                }
                new Thread(() -> this.checkPlayer((EntityPlayer)entity)).start();
            }
        }
    }

    private void checkCombatHurt(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || entity == ClientGrim.mc.thePlayer) {
            return;
            //Do not Fucking Check MySelf with Reach.
        }
        Entity attacker = null;
        int attackerCount = 0;
        for (Entity worldEntity : ClientGrim.mc.theWorld.getLoadedEntityList()) {
            if (!(worldEntity instanceof EntityPlayer) || worldEntity.getDistanceToEntity(entity) > 7.0f || ((Object)worldEntity).equals(entity)) continue;
            ++attackerCount;
            attacker = worldEntity;
        }
        if (attacker == null || attacker.equals(entity) ) {
            return;
        }
        double reach = attacker.getDistanceToEntity(entity);
        String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.GREEN + "ClientGrim" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + attacker.getName() + EnumChatFormatting.WHITE + " failed ";
        if (reach > 3.0) {
            DebugUtil.log(prefix + EnumChatFormatting.AQUA + "Reach" + EnumChatFormatting.WHITE + " (vl:" + attackerCount + ".0)" + EnumChatFormatting.GRAY + ": " + DF_1.format(reach) + " blocks");
        }
    }

    private void checkPlayer(EntityPlayer player) {
        if (player != (ClientGrim.mc.thePlayer) ) {
            //Do not Fucking Check Myself with NoSlow.

            String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.GREEN + "ClientGrim" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + player.getName() + EnumChatFormatting.WHITE + " failed ";
            if (player.isUsingItem() && (player.posX - player.lastTickPosX > 0.2 || player.posZ - player.lastTickPosZ > 0.2)) {
                DebugUtil.log(prefix + EnumChatFormatting.AQUA + "NoSlow" + EnumChatFormatting.WHITE + " (vl:" + this.vl + ".0)");
                ++this.vl;
            }
            if (!ClientGrim.mc.theWorld.loadedEntityList.contains(player) || !player.isEntityAlive()) {
                this.vl = 0;
            }
        }
    }
}
