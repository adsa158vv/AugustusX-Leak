package net.augustus.modules.combat;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.misc.MidClick;
import net.augustus.settings.*;
import net.augustus.utils.PrePostUtil;
import net.augustus.utils.RenderUtil;
import net.augustus.utils.TimeHelper;
import net.augustus.utils.skid.atani.EntitiesUtil;
import net.augustus.utils.skid.atani.FightUtil;
import net.augustus.utils.skid.atani.VecUtil;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;


import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


public class Backtrack extends Module {
    public Backtrack() {
        super("BackTrack", new Color(110, 186, 9), Categorys.COMBAT);
    }
    public boolean oldPacket_BlockPackets;
    public LinkedBlockingQueue<Packet<?>> postFix_StoredPackets = new LinkedBlockingQueue<>();

    //
    public final StringValue mode = new StringValue(298,"Mode", this, "Packet",new String[]{"Packet", "OldPacket"});
    public BooleanValue oldPacket_PacketVelocity = new BooleanValue(11835, "Velocity", this, true);

    public BooleanValue oldPacket_PacketVelocityExplosion = new BooleanValue(9272, "ExplosionVelocity", this, true);

    public BooleanValue oldPacket_PacketTimeUpdate = new BooleanValue(2644, "TimeUpdate", this, true);

    public BooleanValue oldPacket_PacketKeepAlive = new BooleanValue(13599, "KeepAlive", this, true);
    //public BooleanValue packetC0F = new BooleanValue(1484,"C0F",this,false);
    public BooleanValue oldPacket_PacketS32 = new BooleanValue(13878,"S32",this,false);
    public BooleanValue attack_Players = new BooleanValue(1265,"Players",  this, true);
    public BooleanValue attack_Animals = new BooleanValue(127,"Animals", this, true);
    public BooleanValue attack_Monsters = new BooleanValue(14517,"Monsters",  this, true);
    public BooleanValue attack_Invisibles = new BooleanValue(3961,"Invisibles", this, true);
    public BooleansSetting oldPacket_packetsToDelay = new BooleansSetting(12249, "PacketsToDelay", this, new Setting[]{this.oldPacket_PacketVelocity, this.oldPacket_PacketVelocityExplosion, this.oldPacket_PacketTimeUpdate, this.oldPacket_PacketKeepAlive, this.oldPacket_PacketS32});
    public BooleansSetting attack_Setting_Targets = new BooleansSetting(10002, "Targets", this, new Setting[]{this.attack_Players, this.attack_Monsters, this.attack_Animals, this.attack_Invisibles});
    public DoubleValue oldPacket_Delay = new DoubleValue(16269,"Delay", this, 450L, 0L, 5000L, 0);
    public DoubleValue oldPacket_MaximumRange = new DoubleValue(6005,"MaxRange",  this, 6f, 1f, 6f, 3);


    //public BooleansSetting attack_Setting_Targets = new BooleansSetting(9776, "Targets", this, new Setting[]{this.attack_Players, this.attack_Monsters, this.attack_Animals, this.attack_Invisibles});


    public final DoubleValue packet_MinRange = new DoubleValue(4988,"MinRange", this, 2.9f, 0f, 6f, 3);
    public final DoubleValue packet_MaxStartRange = new DoubleValue(2961,"MaxStartRange", this, 3.2f, 2f, 4f, 3);
    public final DoubleValue packet_MaxActiveRange = new DoubleValue(12772,"MaxActiveRange", this, 5f, 2f, 6f, 3);
    public final DoubleValue packet_MinDelay = new DoubleValue(3435,"MinDelay", this, 100, 0, 500, 0);
    public final DoubleValue packet_MaxDelay = new DoubleValue(10936,"MaxDelay",  this, 200, 0, 1000, 1);
    public final DoubleValue packetLimit_Amount = new DoubleValue(109236,"PacketLimitation",  this, 20, 0, 64, 0);
    public BooleanValue oldPacket_OnlyWhenNeeded = new BooleanValue(6287,"OnlyWhenNeeded", this, true);
    public final DoubleValue packet_MaxHurtTime = new DoubleValue(1959,"MaxHurtTime",  this, 6, 0, 10, 0);
    public final DoubleValue packet_MinReleaseRange = new DoubleValue(15324,"MinReleaseRange", this, 3.2F, 2f, 6f, 3);
    public final BooleanValue limitPacketAmount = new BooleanValue(4373,"LimitPacketAmount",this, true);
    public final BooleanValue packet_SyncHurtTime = new BooleanValue(473,"SyncHTPing",this, true);

    public final BooleanValue attack_OnlyKillAura = new BooleanValue(5503,"OnlyKillAura", this, true);
    public final BooleanValue packet_ResetOnVelocity = new BooleanValue(3705,"ReleaseOnVelocity",this, true);
    public final BooleanValue packet_ResetOnLagging = new BooleanValue(12729,"ReleaseOnFlag",  this, true);

    public BooleanValue cancelPost = new BooleanValue(4080,"CancelPost",this,true);
    public BooleanValue CancelPost_C07 = new BooleanValue(14604,"CancelC07",this,true);
    public BooleanValue CancelPost_C08 = new BooleanValue(12842,"CancelC08",this,true);
    public BooleanValue cancelPost_Debug = new BooleanValue(14437,"CancelPostDebug",this,true);
    public BooleanValue storePost = new BooleanValue(15119,"StorePost",this,true);
    public BooleanValue storePost_C0A = new BooleanValue(10769,"StoreC0A",this,true);
    public BooleanValue storePost_C02 = new BooleanValue(1415,"StoreC02",this,true);

    public BooleanValue storePost_C0E = new BooleanValue(11522,"StoreC0E",this,true);
    public BooleanValue storePost_Debug = new BooleanValue(11593,"StorePostDebug",this,true);
    public final BooleanValue renderEntity = new BooleanValue(12165,"RenderEntity", this, true);
    public BooleanValue suffix = new BooleanValue(14891,"Suffix",this,false);
    public BooleanValue autoRestart = new BooleanValue(12123,"AutoRestart",this,true);
    public BooleanValue autoRestart_NoNoti = new BooleanValue(1860,"NoNoti",this,true);

    // Old
    public final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();
    public EntityLivingBase entity = null;
    public INetHandler packetListener = null;
    public WorldClient lastWorld;
    public final TimeHelper timeHelper = new TimeHelper();

    // New
    public final ArrayList<Packet<INetHandler>> storedPackets = new ArrayList<>();
    public final ArrayList<Entity> targets = new ArrayList<>();

    public KillAura killAura;

    public TimeHelper freezeTimer = new TimeHelper();
    public Entity targetEntity = null;
    public boolean freezingNeeded = false;
    private boolean restarted = false;

    @EventTarget
    public void onEventMove(EventMove eventMove) {
        if (this.mode.getSelected().equals("OldPacket")) {
            this.setSuffix(packets.size()+" "+this.mode.getSelected()+" "+this.oldPacket_Delay.getValue() + "ms", suffix.getBoolean());
        }
        else {
            this.setSuffix(storedPackets.size()+" "+this.mode.getSelected()+" "+this.packet_MinDelay.getValue() +"-"+packet_MaxDelay.getValue()+ "ms", suffix.getBoolean());
        }
    }
    @EventTarget
    public void onEventEarlyTick(EventEarlyTick eventEarlyTick) {
        if (!restarted && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.ticksExisted > 10  && this.autoRestart.getBoolean() && this.autoRestart.isVisible()) {
            this.restart(autoRestart_NoNoti.getBoolean());
            restarted = true;
        }



        if(this.mode.getSelected().equalsIgnoreCase("OldPacket")) {
            try {
                if (mc.thePlayer != null && this.packetListener != null && mc.theWorld != null && oldPacket_CanAttacked(entity)) {
                    double d0 = this.entity.realPosX / 32.0D;
                    double d1 = this.entity.realPosY / 32.0D;
                    double d2 = this.entity.realPosZ / 32.0D;
                    double d3 = (double) this.entity.serverPosX / 32.0D;
                    double d4 = (double) this.entity.serverPosY / 32.0D;
                    double d5 = (double) this.entity.serverPosZ / 32.0D;
                    AxisAlignedBB alignedBB = new AxisAlignedBB(d3 - (double) this.entity.width, d4, d5 - (double) this.entity.width, d3 + (double) this.entity.width, d4 + (double) this.entity.height, d5 + (double) this.entity.width);
                    Vec3 positionEyes = mc.thePlayer.getPositionEyes(mc.getTimer().renderPartialTicks);
                    double currentX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB.minX, alignedBB.maxX);
                    double currentY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB.minY, alignedBB.maxY);
                    double currentZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB.minZ, alignedBB.maxZ);
                    AxisAlignedBB alignedBB2 = new AxisAlignedBB(d0 - (double) this.entity.width, d1, d2 - (double) this.entity.width, d0 + (double) this.entity.width, d1 + (double) this.entity.height, d2 + (double) this.entity.width);
                    double realX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB2.minX, alignedBB2.maxX);
                    double realY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB2.minY, alignedBB2.maxY);
                    double realZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB2.minZ, alignedBB2.maxZ);
                    double distance = this.oldPacket_MaximumRange.getValue();
                    if (!this.mc.thePlayer.canEntityBeSeen(this.entity)) {
                        distance = distance > 3 ? 3 : distance;
                    }
                    double bestX = MathHelper.clamp_double(positionEyes.xCoord, this.entity.getEntityBoundingBox().minX, this.entity.getEntityBoundingBox().maxX);
                    double bestY = MathHelper.clamp_double(positionEyes.yCoord, this.entity.getEntityBoundingBox().minY, this.entity.getEntityBoundingBox().maxY);
                    double bestZ = MathHelper.clamp_double(positionEyes.zCoord, this.entity.getEntityBoundingBox().minZ, this.entity.getEntityBoundingBox().maxZ);
                    boolean b = positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.9 || (mc.thePlayer.hurtTime < 8 && mc.thePlayer.hurtTime > 1);
                    if (!this.oldPacket_OnlyWhenNeeded.getBoolean()) {
                        b = true;
                        this.oldPacket_BlockPackets = true;
                    }
                    if (!(b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) + 0.05) || !(mc.thePlayer.getDistance(d0, d1, d2) < distance) || this.timeHelper.reached((long) this.oldPacket_Delay.getValue())) {
                        this.oldPacket_BlockPackets = false;
                        this.resetPackets(this.packetListener);
                        this.timeHelper.reset();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet p = eventSendPacket.getPacket();
        if (this.cancelPost.getBoolean() && cancelPost.isVisible()) {
            if (PrePostUtil.isPost()) {
                if (!packets.isEmpty()) {
                    if ((p instanceof C07PacketPlayerDigging && CancelPost_C07.getBoolean() ) || (p instanceof C08PacketPlayerBlockPlacement && CancelPost_C08.getBoolean())) {
                        if (cancelPost_Debug.getBoolean()) {
                            LogUtil.addChatMessage("§F[§6Backtrack§F]§F[§4CancelPost§5Debug§F] Cancelled Post §2" + p + "§F Packet!");
                        }
                        eventSendPacket.setCancelled(true);
                    }

                }
            }
        }
        if (this.storePost.getBoolean() && storePost.isVisible()) {
            if (PrePostUtil.isPost()) {
                if (!packets.isEmpty()) {
                    if ((p instanceof C0APacketAnimation && storePost_C0A.getBoolean()) || (p instanceof C0EPacketClickWindow && storePost_C0E.getBoolean()) || (p instanceof C02PacketUseEntity && storePost_C02.getBoolean())) {
                        if (storePost_Debug.getBoolean()) {
                            LogUtil.addChatMessage("§F[§6Backtrack§F]§F[§4StorePost§5Debug§F] Stored Post §2" + p + "§F Packet! Ready to Send When Pre.");
                        }
                        postFix_StoredPackets.add(p);
                        eventSendPacket.setCancelled(true);
                    }
                }
            }

        }
    }
    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {
        SendStoredPostPackets();
    }

    @EventTarget
    public void onEventReadPacket(EventReadPacket event) {
        if (killAura == null)
            killAura = mm.killAura;

        if(mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        if(this.mode.getSelected().equalsIgnoreCase("Packet")) {
            Packet<?> packet = event.getPacket();
            WorldClient theWorld = mc.theWorld;


            if (packet instanceof S14PacketEntity) {
                handleS14PacketEntity(packet, theWorld, event);
            } else {
                handleNonS14Packet(packet, event);
            }

            if (packet instanceof C02PacketUseEntity && (!attack_OnlyKillAura.getBoolean() || !mm.killAura.isToggled() && KillAura.target == null)) {
                handleC02PacketUseEntity(packet, theWorld);
            }
            if (attack_OnlyKillAura.getBoolean() && mm.killAura.isToggled() && KillAura.target != null) {
                targetEntity = KillAura.target;
            }
        } else if(this.mode.getSelected().equalsIgnoreCase("OldPacket")) {
            if (event.getNetHandler() != null && event.getNetHandler() instanceof OldServerPinger) return;
            if (mc.theWorld != null) {

                this.packetListener = event.getNetHandler();
                if (packets.size() >= packetLimit_Amount.getValue() && limitPacketAmount.getBoolean()) {
                    this.resetPackets(packetListener);
                    return;
                }
                synchronized (Backtrack.class) {
                    final Packet<?> p = event.getPacket();
                    if (p instanceof S14PacketEntity) {
                        S14PacketEntity packetEntity = (S14PacketEntity) p;
                        final Entity entity = mc.theWorld.getEntityByID(packetEntity.getEntityId());
                        if (!oldPacket_CanAttacked(entity)) return;
                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                            entityLivingBase.realPosX += packetEntity.func_149062_c();
                            entityLivingBase.realPosY += packetEntity.func_149061_d();
                            entityLivingBase.realPosZ += packetEntity.func_149064_e();
                        }
                    }
                    if (p instanceof S18PacketEntityTeleport) {
                        S18PacketEntityTeleport teleportPacket = (S18PacketEntityTeleport) p;
                        final Entity entity = mc.theWorld.getEntityByID(teleportPacket.getEntityId());
                        if (!oldPacket_CanAttacked(entity)) return;
                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                            entityLivingBase.realPosX = teleportPacket.getX();
                            entityLivingBase.realPosY = teleportPacket.getY();
                            entityLivingBase.realPosZ = teleportPacket.getZ();
                        }
                    }

                    this.entity = null;
                    try {
                        if (mm.killAura.isToggled() && oldPacket_CanAttacked(KillAura.target) && KillAura.target != null) {
                            this.entity = KillAura.target;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (this.entity == null) {
                        this.resetPackets(event.getNetHandler());
                        return;
                    }
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        if (this.lastWorld != mc.theWorld) {
                            resetPackets(event.getNetHandler());
                            this.lastWorld = mc.theWorld;
                            return;
                        }
                        this.addPackets(p, event);
                    }
                    this.lastWorld = mc.theWorld;
                }
            }
        }
    }
    private boolean oldPacket_CanAttacked(Entity entity) {
        if (entity == null) return false;
        if (attack_OnlyKillAura.getBoolean()) {
            return entity == KillAura.target;
        }
        if (entity instanceof EntityLivingBase) {
            if (entity.isInvisible()) {
                return false;
            }

            if (((EntityLivingBase) entity).deathTime > 1) {
                return false;
            }

            if (entity.isInvisible() && !this.attack_Invisibles.getBoolean()) {
                return false;
            }

            if (entity instanceof EntityAnimal && !this.attack_Animals.getBoolean()) {
                return false;
            }

            if (entity instanceof EntityMob && !this.attack_Monsters.getBoolean()) {
                return false;
            }

            if (entity instanceof EntityPlayer && !this.attack_Players.getBoolean()) {
                return false;
            }



            if (entity.ticksExisted < 50) {
                return false;
            }

            if (entity instanceof EntityPlayer && mm.teams.isToggled() && mm.teams.getTeammates().contains(entity)) {
                return false;
            }

            if (entity instanceof EntityPlayer && (entity.getName().equals("§aShop") || entity.getName().equals("SHOP") || entity.getName().equals("UPGRADES"))) {
                return false;
            }

            if (entity.isDead) {
                return false;
            }

            if (entity instanceof EntityPlayer && mm.antiBot.isToggled() && AntiBot.isServerBot(entity)) {
                return false;
            }

            if (entity instanceof EntityPlayer && !mm.midClick.noFiends && MidClick.friends.contains(entity.getName())) {
                return false;
            }
        }

        return entity instanceof EntityLivingBase && entity != mc.thePlayer && (double) mc.thePlayer.getDistanceToEntity(entity) < this.oldPacket_MaximumRange.getValue();
    }
    public void resetPackets(INetHandler netHandler) {
        if (!this.packets.isEmpty()) {
            synchronized (this.packets) {
                while (!this.packets.isEmpty()) {
                    try {
                        this.packets.get(0).processPacket(netHandler);
                    } catch (Exception ignored) {
                    }
                    this.packets.remove(this.packets.get(0));
                }

            }
        }
    }
    public void resetAll() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            SendStoredPostPackets();
            resetPackets(packetListener);
        }
        oldPacket_BlockPackets = false;
        restarted = false;
    }
    public void SendStoredPostPackets() {
        if (PrePostUtil.isPre() && !postFix_StoredPackets.isEmpty()) {
            try {
                this.postFix_StoredPackets.forEach(mc.thePlayer.sendQueue::addToSendQueueDirect);
                if (storePost_Debug.getBoolean()) {
                    LogUtil.addChatMessage("§F[§6Backtrack§F]§F[§4StorePost§5Debug§F] Send §2"+postFix_StoredPackets.size()+" §FStoredPackets!");
                }
                postFix_StoredPackets.clear();
            } catch (NullPointerException ignored) {} catch (Exception var2) {
                System.err.println("Error Backtrack StorePostPackets Send.");
            }
        }
    }
    public void addPackets(Packet packet, EventReadPacket eventReadPacket) {
        synchronized (this.packets) {
            if (this.blockPacket(packet)) {
                this.packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    public boolean blockPacket(Packet packet) {

        if (packet instanceof S03PacketTimeUpdate) {
            return this.oldPacket_PacketTimeUpdate.getBoolean();
        } else if (packet instanceof S00PacketKeepAlive) {
            return this.oldPacket_PacketKeepAlive.getBoolean();
        } else if (packet instanceof S12PacketEntityVelocity) {
            return this.oldPacket_PacketVelocity.getBoolean();
        } else if (packet instanceof S27PacketExplosion) {
            return this.oldPacket_PacketVelocityExplosion.getBoolean();
        } else if (packet instanceof S32PacketConfirmTransaction) {
            return this.oldPacket_PacketS32.getBoolean();
        } else if (packet instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus entityStatus = (S19PacketEntityStatus) packet;
            return entityStatus.getOpCode() != 2 || !(mc.theWorld.getEntityByID(entityStatus.getEntityId()) instanceof EntityLivingBase);
        } else {
            return !(packet instanceof S06PacketUpdateHealth) && !(packet instanceof S29PacketSoundEffect) && !(packet instanceof S3EPacketTeams) && !(packet instanceof S0CPacketSpawnPlayer);
        }

    }


    public void handleS14PacketEntity(Packet<?> packet, WorldClient theWorld, EventReadPacket event) {
        S14PacketEntity entityPacket = (S14PacketEntity) packet;
        Entity entity = entityPacket.getEntity(theWorld);

        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        entity.serverPosX += entityPacket.func_149062_c();
        entity.serverPosY += entityPacket.func_149061_d();
        entity.serverPosZ += entityPacket.func_149064_e();

        double x = entity.serverPosX / 32.0;
        double y = entity.serverPosY / 32.0;
        double z = entity.serverPosZ / 32.0;

        boolean isValidTarget = (!attack_OnlyKillAura.getBoolean() || freezingNeeded) && FightUtil.isValidWithPlayer(entity, 100, attack_Invisibles.getBoolean(), attack_Players.getBoolean(), attack_Animals.getBoolean(), attack_Monsters.getBoolean()) || (killAura.isToggled() && KillAura.target != null && entity == KillAura.target);

        if (isValidTarget) {
            double afterRange = calculateAfterRange(x, y, z);
            double beforeRange = calculateBeforeRange(entity);

            if (beforeRange <= packet_MaxStartRange.getValue() && isInRange(afterRange, packet_MinRange.getValue(), packet_MaxActiveRange.getValue()) && afterRange > beforeRange + 0.02 && ((EntityLivingBase) entity).hurtTime <= calculateMaxHurtTime()) {
                handleValidTarget(entity, event);
                return;
            }
        }

        if (freezingNeeded) {
            handleFreezing(entity, event);
            return;
        }

        handleNonCancelled(entity, x, y, z, entityPacket, event);
    }

    public void handleNonS14Packet(Packet<?> packet, EventReadPacket event) {
        if ((packet instanceof S12PacketEntityVelocity && packet_ResetOnVelocity.getBoolean() || (packet instanceof S08PacketPlayerPosLook && packet_ResetOnLagging.getBoolean()))) {
            storedPackets.add((Packet<INetHandler>) packet);
            event.setCancelled(true);
            releasePackets();
        } else if (freezingNeeded && !event.isCancelled()) {
            if (packet instanceof S19PacketEntityStatus) {
                if (((S19PacketEntityStatus) packet).logicOpcode == (byte) 2) {
                    return;
                }
            }
            storedPackets.add((Packet<INetHandler>) packet);
            event.setCancelled(true);
        }
    }

    public void handleC02PacketUseEntity(Packet<?> packet, WorldClient theWorld) {
        C02PacketUseEntity useEntityPacket = (C02PacketUseEntity) packet;
        if (useEntityPacket.getAction() == C02PacketUseEntity.Action.ATTACK && freezingNeeded) {
            targetEntity = useEntityPacket.getEntityFromWorld(theWorld);
        }
    }

    public double calculateAfterRange(double x, double y, double z) {
        AxisAlignedBB afterBB = new AxisAlignedBB(x - 0.4, y - 0.1, z - 0.4, x + 0.4, y + 1.9, z + 0.4);
        Vec3 eyes = mc.thePlayer.getPositionEyes(1F);
        return VecUtil.getNearestPointBB(eyes, afterBB).distanceTo(eyes);
    }

    public double calculateBeforeRange(Entity entity) {
        return EntitiesUtil.getDistanceToEntityBox(mc.thePlayer, entity);
    }

    public boolean isInRange(double value, double minValue, double maxValue) {
        return value >= minValue && value <= maxValue;
    }

    public void handleValidTarget(Entity entity, EventReadPacket event) {
        if (!freezingNeeded) {
            freezeTimer.reset();
            freezingNeeded = true;
        }
        if (!targets.contains(entity)) {
            targets.add(entity);
        }
        event.setCancelled(true);
    }

    public void handleFreezing(Entity entity, EventReadPacket event) {
        if (!targets.contains(entity)) {
            targets.add(entity);
        }
        event.setCancelled(true);
    }

    public void handleNonCancelled(Entity entity, double x, double y, double z, S14PacketEntity entityPacket, EventReadPacket event) {
        float f = entityPacket.func_149060_h() ? (entityPacket.func_149066_f() * 360) / 256.0f : entity.rotationYaw;
        float f1 = entityPacket.func_149060_h() ? (entityPacket.func_149063_g() * 360) / 256.0f : entity.rotationPitch;

        entity.setPositionAndRotation2(x, y, z, f, f1, 3, false);
        entity.onGround = entityPacket.onGround;
        event.setCancelled(true);
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (! renderEntity.getBoolean()) return;
        if(this.mode.getSelected().equalsIgnoreCase("Packet")) {
            if (!freezingNeeded) {
                return;
            }
            GL11.glPushMatrix();
            GlStateManager.disableAlpha();


            for (Entity entity : targets) {
                renderFrozenEntity(entity, event);
            }



            GlStateManager.enableAlpha();
            GlStateManager.resetColor();
            GL11.glPopMatrix();
        }


        if (this.mode.getSelected().equalsIgnoreCase("OldPacket")) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            if (this.entity != null && this.oldPacket_BlockPackets) {
                this.render(this.entity);
            }

            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
        }


    }
    public void render(EntityLivingBase entity) {
        //Old Packet Mode.
        float red = 0.0F;
        float green = 1.1333333F;
        float blue = 0.0F;
        float lineWidth = 3.0F;
        float alpha = 0.03137255F;
        if (true) {
            double d0 = 1.0F - mc.thePlayer.getDistanceToEntity(entity) / 20.0F;
            if (d0 < 0.3D) {
                d0 = 0.3D;
            }

            lineWidth = (float) ((double) lineWidth * d0);
        }

        RenderUtil.drawEntityServerESP(entity, red, green, blue, alpha, 1.0F, lineWidth);
    }

    public void renderFrozenEntity(Entity entity, EventRender3D event) {
        if (!(entity instanceof EntityOtherPlayerMP)) {
            return;
        }

        EntityOtherPlayerMP mp = new EntityOtherPlayerMP(mc.theWorld, ((EntityOtherPlayerMP) entity).getGameProfile());
        mp.posX = entity.serverPosX / 32.0;
        mp.posY = entity.serverPosY / 32.0;
        mp.posZ = entity.serverPosZ / 32.0;
        mp.prevPosX = mp.posX;
        mp.prevPosY = mp.posY;
        mp.prevPosZ = mp.posZ;
        mp.lastTickPosX = mp.posX;
        mp.lastTickPosY = mp.posY;
        mp.lastTickPosZ = mp.posZ;
        mp.rotationYaw = entity.rotationYaw;
        mp.rotationPitch = entity.rotationPitch;
        mp.rotationYawHead = ((EntityOtherPlayerMP) entity).rotationYawHead;
        mp.prevRotationYaw = mp.rotationYaw;
        mp.prevRotationPitch = mp.rotationPitch;
        mp.prevRotationYawHead = mp.rotationYawHead;
        mp.swingProgress = ((EntityOtherPlayerMP) entity).swingProgress;
        mp.swingProgressInt = ((EntityOtherPlayerMP) entity).swingProgressInt;
        mc.getRenderManager().renderEntitySimple(mp, mc.getTimer().renderPartialTicks);
    }

    @EventTarget
    public void onEventPostMotion(EventPostMotion event) {


            if (freezingNeeded && this.mode.getSelected().equalsIgnoreCase("Packet")) {
                if (storedPackets.size() >= packetLimit_Amount.getValue() && limitPacketAmount.getBoolean()) {
                    releasePackets();
                    return;
                }
                if (freezeTimer.reached((long) packet_MaxDelay.getValue())) {
                    releasePackets();
                    return;
                }

                if (!targets.isEmpty()) {
                    boolean shouldRelease = false;

                    for (Entity entity : targets) {
                        double x = entity.serverPosX / 32.0;
                        double y = entity.serverPosY / 32.0;
                        double z = entity.serverPosZ / 32.0;

                        AxisAlignedBB entityBB = new AxisAlignedBB(x - 0.4, y - 0.1, z - 0.4, x + 0.4, y + 1.9, z + 0.4);

                        double range = entityBB.getLookingTargetRange(mc.thePlayer);

                        if (range == Double.MAX_VALUE) {
                            Vec3 eyes = mc.thePlayer.getPositionEyes(1F);
                            range = VecUtil.getNearestPointBB(eyes, entityBB).distanceTo(eyes) + 0.075;
                        }

                        if (range <= packet_MinRange.getValue()) {
                            shouldRelease = true;
                            break;
                        }

                        Entity entity1 = targetEntity;
                        if (entity1 != entity) {
                            continue;
                        }

                        if (freezeTimer.reached((long) packet_MinDelay.getValue())) {
                            if (range >= packet_MinReleaseRange.getValue()) {
                                shouldRelease = true;
                                break;
                            }
                        }
                    }

                    if (shouldRelease) {
                        releasePackets();
                    }
                }
            }

    }

    @EventTarget
    public void onWorld(EventWorld event) {
        targetEntity = null;
        targets.clear();

        if (event.getWorldClient() == null) {
            storedPackets.clear();
        }
    }

    public void releasePackets() {
        targetEntity = null;
        INetHandlerPlayClient netHandler = mc.getNetHandler();

        if (storedPackets.isEmpty()) {
            return;
        }

        while (!storedPackets.isEmpty()) {
            Packet<INetHandler> packet = storedPackets.remove(0);

            try {
                try {
                    packet.processPacket(netHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (ThreadQuickExitException ignored) {
                // Ignore the exception
            }
        }

        while (!targets.isEmpty()) {
            Entity entity = targets.remove(0);

            if (!entity.isDead) {
                double x = entity.serverPosX / 32.0;
                double y = entity.serverPosY / 32.0;
                double z = entity.serverPosZ / 32.0;

                entity.setPosition(x, y, z);
            }
        }

        freezingNeeded = false;
    }

    public int calculateMaxHurtTime() {
        int ping = EntitiesUtil.getPing(mc.thePlayer);

        return (int) (packet_MaxHurtTime.getValue() + (packet_SyncHurtTime.getBoolean() ? (int) Math.ceil(ping / 50.0) : 0));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetAll();

    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetAll();
    }
    @EventTarget
    public void onEventWorld(EventWorld eventWorld) {
        resetAll();
    }
}
