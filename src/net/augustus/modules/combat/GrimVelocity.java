package net.augustus.modules.combat;



import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.robin.RayCastUtil;
import net.augustus.utils.skid.xylitol.MathUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.manager.EventManager;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector2f;
import viamcp.ViaMCP;




public class GrimVelocity extends Module {
    double motion;
    boolean attacked;
    public static boolean velocityInput;
    public static boolean shouldCancel;
    private boolean shouldSendC03;
    int resetPersec = 8;
    int grimTCancel = 0;
    int updates = 0;
    int cancelPacket = 6;
    LinkedList<Packet<INetHandlerPlayClient>> inBus = new LinkedList();
    boolean lastSprinting;
    public static boolean velocityOverrideSprint;

    public GrimVelocity() {
        super("GrimVelocity", new Color(73, 127, 163), Categorys.COMBAT);
    }
    public StringValue grimModes = new StringValue(1,"Mode",this,velMode.Vertical.toString(),new String[]{velMode.GrimV_2_3_45.toString(),velMode.GrimV_2_3_40.toString(),velMode.GrimV_2_4_43.toString(),velMode.GrimV_2_4_40.toString(),velMode.Vertical.toString()});
    public BooleanValue grimVe_RayCast = new BooleanValue(2,"RayCast",this,true);
    @Override
    public void onEnable() {
        if (Objects.equals(this.grimModes.getSelected(), velMode.GrimV_2_3_45.toString())) {
            this.grimTCancel = 0;
            this.inBus.clear();
        }
        shouldCancel = false;
    }

    @Override
    public void onDisable() {
        if (Objects.equals(this.grimModes.getSelected(), velMode.GrimV_2_3_45.toString())) {
            while (!this.inBus.isEmpty()) {
                this.inBus.poll().processPacket(this.mc.getNetHandler());
            }
            this.grimTCancel = 0;
        }
        shouldCancel = false;
        if (this.mc.thePlayer.hurtTime > 0 && !this.mc.thePlayer.isOnLadder()) {
            PacketUtil.sendPacketC0F();
            BlockPos pos = new BlockPos(this.mc.thePlayer);
            if (this.grimModes.getSelected() == velMode.GrimV_2_4_43.toString()) {
                this.mc.timer.lastSyncSysClock += (long) MathUtil.getRandom(32052, 89505);
                this.mc.timer.elapsedPartialTicks = (float)((double)this.mc.timer.elapsedPartialTicks - 0.38339);
                ++this.mc.thePlayer.positionUpdateTicks;
                PacketUtil.send(new C03PacketPlayer(this.mc.thePlayer.onGround));
            }
            if (this.grimModes.getSelected() == velMode.GrimV_2_4_43.toString() || this.grimModes.getSelected() == velMode.GrimV_2_4_40.toString()) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos.up(), EnumFacing.DOWN));
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos.up(), EnumFacing.DOWN));
            }
        }
    }

    @EventTarget
    public void onWorldLoad(EventWorld event) {
        shouldCancel = false;
        this.grimTCancel = 0;
        this.inBus.clear();
    }

    @EventTarget
    public void onEventMove(EventMove eventMove) {
        this.setSuffix(this.grimModes.getSelected(), true);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.mc.getNetHandler() == null) {
            return;
        }
        if (this.mc.theWorld == null) {
            return;
        }
        if (this.mc.thePlayer == null) {
            return;
        }
        if (this.grimModes.getSelected() == velMode.GrimV_2_3_40.toString()) {
            ++this.updates;
            if (this.resetPersec > 0 && this.updates >= 0) {
                this.updates = 0;
                if (this.grimTCancel > 0) {
                    --this.grimTCancel;
                }
            }
        }
        if (this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString()) {
            if (this.resetPersec > 0 && this.updates >= 0) {
                this.updates = 0;
                if (this.grimTCancel > 0) {
                    --this.grimTCancel;
                }
            }
            if (this.grimTCancel == 0) {
                while (!this.inBus.isEmpty()) {
                    this.inBus.poll().processPacket(this.mc.getNetHandler());
                }
            }
        }
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (this.grimModes.getSelected() == velMode.Vertical.toString()) {
            if (!ViaMCP.getInstance().isOldCombat()) {
                if (velocityInput) {
                    if (this.attacked) {
                        this.mc.thePlayer.motionX *= this.motion;
                        this.mc.thePlayer.motionZ *= this.motion;
                        this.attacked = false;
                    }
                    if (this.mc.thePlayer.hurtTime == 0) {
                        velocityInput = false;
                    }
                }
            } else if (this.mc.thePlayer.hurtTime > 0 && this.mc.thePlayer.onGround) {
                this.mc.thePlayer.addVelocity(-1.3E-10, -1.3E-10, -1.3E-10);
                this.mc.thePlayer.setSprinting(false);
            }
        }
    }



    @EventTarget
    public void onPacketReceive(EventReadPacket e) {
        if (this.mc.thePlayer == null) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity)e.getPacket();
            if (packetEntityVelocity.getEntityID() != this.mc.thePlayer.getEntityId()) {
                return;
            }
            if (this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() || this.grimModes.getSelected() == velMode.GrimV_2_3_40.toString()) {
                e.setCancelled(true);
                int n = this.grimTCancel = this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() ? 3 : this.cancelPacket;
            }
            if (this.grimModes.getSelected() == velMode.GrimV_2_4_40.toString() || this.grimModes.getSelected() == velMode.GrimV_2_4_43.toString()) {
                e.setCancelled(true);
                shouldCancel = true;
            }
            if (this.grimModes.getSelected() == velMode.Vertical.toString() && !ViaMCP.getInstance().isOldCombat()) {
                EntityLivingBase targets;
                velocityInput = true;
                MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(new Vector2f(this.mc.thePlayer.lastReportedYaw, this.mc.thePlayer.lastReportedPitch), 3.0);
                EntityLivingBase entityLivingBase = targets = grimVe_RayCast.getBoolean() && movingObjectPosition != null && movingObjectPosition.entityHit == KillAura.target ? (EntityLivingBase)movingObjectPosition.entityHit : KillAura.target;
                if (targets != null && !this.mc.thePlayer.isOnLadder()) {
                    boolean state = this.mc.thePlayer.isServerSprintState();
                    this.shouldSendC03 = true;
                    if (!state) {
                        PacketUtil.send(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                    }
                    for (int i = 0; i < 5; ++i) {
                        EventManager.call(new EventAttackEntity(targets, true));
                        PacketUtil.send(new C02PacketUseEntity((Entity)targets, C02PacketUseEntity.Action.ATTACK));
                        PacketUtil.send(new C0APacketAnimation());
                    }
                    velocityOverrideSprint = true;
                    this.mc.thePlayer.setSprinting(true);
                    this.mc.thePlayer.setServerSprintState(true);
                    this.attacked = true;
                    double strength = new Vec3(packetEntityVelocity.getMotionX(), packetEntityVelocity.getMotionY(), packetEntityVelocity.getMotionZ()).lengthVector();
                    this.motion = this.getMotion(packetEntityVelocity);
                }
            }
            if (this.grimTCancel > 0 && packet instanceof S32PacketConfirmTransaction && this.grimModes.getSelected() == velMode.GrimV_2_3_40.toString()) {
                e.setCancelled(true);
                --this.grimTCancel;
            }
            if (this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() && this.grimTCancel > 0 && packet.getClass().getSimpleName().startsWith("S") && !(packet instanceof S12PacketEntityVelocity) && !(packet instanceof S27PacketExplosion) && !(packet instanceof S03PacketTimeUpdate)) {
                e.setCancelled(true);
                this.inBus.add((Packet<INetHandlerPlayClient>) packet);
            }
        }
        if (e.getPacket() instanceof S27PacketExplosion) {
            if (this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() || this.grimModes.getSelected() == velMode.GrimV_2_3_40.toString()) {
                e.setCancelled(true);
                int n = this.grimTCancel = this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() ? 3 : this.cancelPacket;
            }
            if (this.grimModes.getSelected() == velMode.GrimV_2_4_40.toString() || this.grimModes.getSelected() == velMode.GrimV_2_4_43.toString()) {
                e.setCancelled(true);
                shouldCancel = true;
            }
        }
    }

    private double getMotion(S12PacketEntityVelocity packetEntityVelocity) {
        return 0.07776;
    }

    @EventTarget
    public void onPacketSend(EventSendPacket e) {
        if (this.mc.thePlayer == null) {
            return;
        }
        Packet packet = e.getPacket();
        if (this.grimModes.getSelected() == velMode.Vertical.toString() && !ViaMCP.getInstance().isOldCombat() && !mm.disabler.isToggled()  && !mm.disabler.grimbf.getBoolean() && packet instanceof C0BPacketEntityAction && velocityInput) {
            if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    e.setCancelled(true);
                }
                this.lastSprinting = true;
            } else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    e.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
        if (this.grimModes.getSelected() == velMode.GrimV_2_3_45.toString() && this.grimTCancel > 0) {
            if (packet instanceof C0APacketAnimation) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C13PacketPlayerAbilities) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C08PacketPlayerBlockPlacement) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C07PacketPlayerDigging) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C02PacketUseEntity) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C0EPacketClickWindow) {
                PacketUtil.sendPacketC0F();
            } else if (packet instanceof C0BPacketEntityAction) {
                PacketUtil.sendPacketC0F();
            }
        }
    }

    public static enum velMode {
        Vertical,
        GrimV_2_4_43,
        GrimV_2_4_40,
        GrimV_2_3_45,
        GrimV_2_3_40;

    }
}

