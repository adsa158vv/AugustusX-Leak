//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package net.augustus.utils.skid.xylitol;

import net.augustus.events.EventSendPacket;
import net.augustus.modules.exploit.PostDis;


import net.augustus.utils.interfaces.MC;
import net.minecraft.entity.Entity;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;

import net.minecraft.network.play.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.BlockPos;
import viamcp.ViaMCP;


import static net.augustus.Augustus.mc;

public class PacketUtil
{
    public static void send(final Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue((Packet)packet);
        }
    }

    public static void handlePacket(final Packet<INetHandlerPlayClient> packet) {
        final INetHandlerPlayClient netHandler = (INetHandlerPlayClient)mc.getNetHandler();
        if (packet instanceof S00PacketKeepAlive) {
            netHandler.handleKeepAlive((S00PacketKeepAlive)packet);
        }
        else if (packet instanceof S01PacketJoinGame) {
            netHandler.handleJoinGame((S01PacketJoinGame)packet);
        }
        else if (packet instanceof S02PacketChat) {
            netHandler.handleChat((S02PacketChat)packet);
        }
        else if (packet instanceof S03PacketTimeUpdate) {
            netHandler.handleTimeUpdate((S03PacketTimeUpdate)packet);
        }
        else if (packet instanceof S04PacketEntityEquipment) {
            netHandler.handleEntityEquipment((S04PacketEntityEquipment)packet);
        }
        else if (packet instanceof S05PacketSpawnPosition) {
            netHandler.handleSpawnPosition((S05PacketSpawnPosition)packet);
        }
        else if (packet instanceof S06PacketUpdateHealth) {
            netHandler.handleUpdateHealth((S06PacketUpdateHealth)packet);
        }
        else if (packet instanceof S07PacketRespawn) {
            netHandler.handleRespawn((S07PacketRespawn)packet);
        }
        else if (packet instanceof S08PacketPlayerPosLook) {
            netHandler.handlePlayerPosLook((S08PacketPlayerPosLook)packet);
        }
        else if (packet instanceof S09PacketHeldItemChange) {
            netHandler.handleHeldItemChange((S09PacketHeldItemChange)packet);
        }
        else if (packet instanceof S10PacketSpawnPainting) {
            netHandler.handleSpawnPainting((S10PacketSpawnPainting)packet);
        }
        else if (packet instanceof S0APacketUseBed) {
            netHandler.handleUseBed((S0APacketUseBed)packet);
        }
        else if (packet instanceof S0BPacketAnimation) {
            netHandler.handleAnimation((S0BPacketAnimation)packet);
        }
        else if (packet instanceof S0CPacketSpawnPlayer) {
            netHandler.handleSpawnPlayer((S0CPacketSpawnPlayer)packet);
        }
        else if (packet instanceof S0DPacketCollectItem) {
            netHandler.handleCollectItem((S0DPacketCollectItem)packet);
        }
        else if (packet instanceof S0EPacketSpawnObject) {
            netHandler.handleSpawnObject((S0EPacketSpawnObject)packet);
        }
        else if (packet instanceof S0FPacketSpawnMob) {
            netHandler.handleSpawnMob((S0FPacketSpawnMob)packet);
        }
        else if (packet instanceof S11PacketSpawnExperienceOrb) {
            netHandler.handleSpawnExperienceOrb((S11PacketSpawnExperienceOrb)packet);
        }
        else if (packet instanceof S12PacketEntityVelocity) {
            netHandler.handleEntityVelocity((S12PacketEntityVelocity)packet);
        }
        else if (packet instanceof S13PacketDestroyEntities) {
            netHandler.handleDestroyEntities((S13PacketDestroyEntities)packet);
        }
        else if (packet instanceof S14PacketEntity) {
            netHandler.handleEntityMovement((S14PacketEntity)packet);
        }
        else if (packet instanceof S18PacketEntityTeleport) {
            netHandler.handleEntityTeleport((S18PacketEntityTeleport)packet);
        }
        else if (packet instanceof S19PacketEntityStatus) {
            netHandler.handleEntityStatus((S19PacketEntityStatus)packet);
        }
        else if (packet instanceof S19PacketEntityHeadLook) {
            netHandler.handleEntityHeadLook((S19PacketEntityHeadLook)packet);
        }
        else if (packet instanceof S1BPacketEntityAttach) {
            netHandler.handleEntityAttach((S1BPacketEntityAttach)packet);
        }
        else if (packet instanceof S1CPacketEntityMetadata) {
            netHandler.handleEntityMetadata((S1CPacketEntityMetadata)packet);
        }
        else if (packet instanceof S1DPacketEntityEffect) {
            netHandler.handleEntityEffect((S1DPacketEntityEffect)packet);
        }
        else if (packet instanceof S1EPacketRemoveEntityEffect) {
            netHandler.handleRemoveEntityEffect((S1EPacketRemoveEntityEffect)packet);
        }
        else if (packet instanceof S1FPacketSetExperience) {
            netHandler.handleSetExperience((S1FPacketSetExperience)packet);
        }
        else if (packet instanceof S20PacketEntityProperties) {
            netHandler.handleEntityProperties((S20PacketEntityProperties)packet);
        }
        else if (packet instanceof S21PacketChunkData) {
            netHandler.handleChunkData((S21PacketChunkData)packet);
        }
        else if (packet instanceof S22PacketMultiBlockChange) {
            netHandler.handleMultiBlockChange((S22PacketMultiBlockChange)packet);
        }
        else if (packet instanceof S23PacketBlockChange) {
            netHandler.handleBlockChange((S23PacketBlockChange)packet);
        }
        else if (packet instanceof S24PacketBlockAction) {
            netHandler.handleBlockAction((S24PacketBlockAction)packet);
        }
        else if (packet instanceof S25PacketBlockBreakAnim) {
            netHandler.handleBlockBreakAnim((S25PacketBlockBreakAnim)packet);
        }
        else if (packet instanceof S26PacketMapChunkBulk) {
            netHandler.handleMapChunkBulk((S26PacketMapChunkBulk)packet);
        }
        else if (packet instanceof S27PacketExplosion) {
            netHandler.handleExplosion((S27PacketExplosion)packet);
        }
        else if (packet instanceof S28PacketEffect) {
            netHandler.handleEffect((S28PacketEffect)packet);
        }
        else if (packet instanceof S29PacketSoundEffect) {
            netHandler.handleSoundEffect((S29PacketSoundEffect)packet);
        }
        else if (packet instanceof S2APacketParticles) {
            netHandler.handleParticles((S2APacketParticles)packet);
        }
        else if (packet instanceof S2BPacketChangeGameState) {
            netHandler.handleChangeGameState((S2BPacketChangeGameState)packet);
        }
        else if (packet instanceof S2CPacketSpawnGlobalEntity) {
            netHandler.handleSpawnGlobalEntity((S2CPacketSpawnGlobalEntity)packet);
        }
        else if (packet instanceof S2DPacketOpenWindow) {
            netHandler.handleOpenWindow((S2DPacketOpenWindow)packet);
        }
        else if (packet instanceof S2EPacketCloseWindow) {
            netHandler.handleCloseWindow((S2EPacketCloseWindow)packet);
        }
        else if (packet instanceof S2FPacketSetSlot) {
            netHandler.handleSetSlot((S2FPacketSetSlot)packet);
        }
        else if (packet instanceof S30PacketWindowItems) {
            netHandler.handleWindowItems((S30PacketWindowItems)packet);
        }
        else if (packet instanceof S31PacketWindowProperty) {
            netHandler.handleWindowProperty((S31PacketWindowProperty)packet);
        }
        else if (packet instanceof S32PacketConfirmTransaction) {
            netHandler.handleConfirmTransaction((S32PacketConfirmTransaction)packet);
        }
        else if (packet instanceof S33PacketUpdateSign) {
            netHandler.handleUpdateSign((S33PacketUpdateSign)packet);
        }
        else if (packet instanceof S34PacketMaps) {
            netHandler.handleMaps((S34PacketMaps)packet);
        }
        else if (packet instanceof S35PacketUpdateTileEntity) {
            netHandler.handleUpdateTileEntity((S35PacketUpdateTileEntity)packet);
        }
        else if (packet instanceof S36PacketSignEditorOpen) {
            netHandler.handleSignEditorOpen((S36PacketSignEditorOpen)packet);
        }
        else if (packet instanceof S37PacketStatistics) {
            netHandler.handleStatistics((S37PacketStatistics)packet);
        }
        else if (packet instanceof S38PacketPlayerListItem) {
            netHandler.handlePlayerListItem((S38PacketPlayerListItem)packet);
        }
        else if (packet instanceof S39PacketPlayerAbilities) {
            netHandler.handlePlayerAbilities((S39PacketPlayerAbilities)packet);
        }
        else if (packet instanceof S3APacketTabComplete) {
            netHandler.handleTabComplete((S3APacketTabComplete)packet);
        }
        else if (packet instanceof S3BPacketScoreboardObjective) {
            netHandler.handleScoreboardObjective((S3BPacketScoreboardObjective)packet);
        }
        else if (packet instanceof S3CPacketUpdateScore) {
            netHandler.handleUpdateScore((S3CPacketUpdateScore)packet);
        }
        else if (packet instanceof S3DPacketDisplayScoreboard) {
            netHandler.handleDisplayScoreboard((S3DPacketDisplayScoreboard)packet);
        }
        else if (packet instanceof S3EPacketTeams) {
            netHandler.handleTeams((S3EPacketTeams)packet);
        }
        else if (packet instanceof S3FPacketCustomPayload) {
            netHandler.handleCustomPayload((S3FPacketCustomPayload)packet);
        }
        else if (packet instanceof S40PacketDisconnect) {
            netHandler.handleDisconnect((S40PacketDisconnect)packet);
        }
        else if (packet instanceof S41PacketServerDifficulty) {
            netHandler.handleServerDifficulty((S41PacketServerDifficulty)packet);
        }
        else if (packet instanceof S42PacketCombatEvent) {
            netHandler.handleCombatEvent((S42PacketCombatEvent)packet);
        }
        else if (packet instanceof S43PacketCamera) {
            netHandler.handleCamera((S43PacketCamera)packet);
        }
        else if (packet instanceof S44PacketWorldBorder) {
            netHandler.handleWorldBorder((S44PacketWorldBorder)packet);
        }
        else if (packet instanceof S45PacketTitle) {
            netHandler.handleTitle((S45PacketTitle)packet);
        }
        else if (packet instanceof S46PacketSetCompressionLevel) {
            netHandler.handleSetCompressionLevel((S46PacketSetCompressionLevel)packet);
        }
        else if (packet instanceof S47PacketPlayerListHeaderFooter) {
            netHandler.handlePlayerListHeaderFooter((S47PacketPlayerListHeaderFooter)packet);
        }
        else if (packet instanceof S48PacketResourcePackSend) {
            netHandler.handleResourcePack((S48PacketResourcePackSend)packet);
        }
        else {
            if (!(packet instanceof S49PacketUpdateEntityNBT)) {
                throw new IllegalArgumentException("Unable to match packet type to handle: " + packet.getClass());
            }
            netHandler.handleEntityNBT((S49PacketUpdateEntityNBT)packet);
        }
    }

    public static boolean isFlying(EventSendPacket type) {
        return type.getPacket() instanceof C03PacketPlayer || type.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || type.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || type.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook;
    }

    public static boolean isFlyingOnlyC03(EventSendPacket type) {
        return type.getPacket() instanceof C03PacketPlayer || !(type.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) || !(type.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) || !(type.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook);
    }

    public static void sendPacketC0F() {
        if (!PostDis.getGrimPost()) {
            send((Packet<?>)new C0FPacketConfirmTransaction(MathUtil.getRandom(102, 1000024123), (short)MathUtil.getRandom(102, 1000024123), true));
        }
    }

    public static void sendPacketC0F(final boolean noEvent) {
        if (!PostDis.getGrimPost()) {
            if (!noEvent) {
                send((Packet<?>)new C0FPacketConfirmTransaction(MathUtil.getRandom(102, 1000024123), (short)MathUtil.getRandom(102, 1000024123), true));
            }
            else {
                sendPacketNoEvent((Packet<?>)new C0FPacketConfirmTransaction(MathUtil.getRandom(102, 1000024123), (short)MathUtil.getRandom(102, 1000024123), true));
            }
        }
    }

    public static void sendPacketNoEvent(final Packet<?> packet) {
        if (packet == null) return;
        mc.thePlayer.sendQueue.addToSendQueueDirect(packet);
    }

    public static void receivePacketNoEvent(final Packet<?> packet) {
        if (packet == null) return;
        mc.getNetHandler().addToReceiveQueueUnregistered(packet);
    }

    public static void sendBlocking(boolean callEvent, boolean place) {
        C08PacketPlayerBlockPlacement packet = place ?
                new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.mc.thePlayer.getHeldItem(), 0, 0, 0) :
                new C08PacketPlayerBlockPlacement(MC.mc.thePlayer.getHeldItem());

        if(callEvent) {
            send(packet);
        } else {
            sendPacketNoEvent(packet);
        }
    }
    public static void sendAttack(Entity entity, boolean unregistered) {
        if (ViaMCP.getInstance().isOldCombat()) {
            if (unregistered){
                mc.thePlayer.sendQueue.addToSendQueueUnregistered(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                mc.thePlayer.sendQueue.addToSendQueueUnregistered(new C0APacketAnimation());
            }
            else {
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
        } else {
            if (unregistered){
                mc.thePlayer.sendQueue.addToSendQueueUnregistered(new C0APacketAnimation());
                mc.thePlayer.sendQueue.addToSendQueueUnregistered(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }
            else {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }

    public static class TimedPacket
    {
        private final Packet<?> packet;
        private final long time;

        public TimedPacket(final Packet<?> packet, final long time) {
            this.packet = packet;
            this.time = time;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public long getTime() {
            return this.time;
        }
    }
}
