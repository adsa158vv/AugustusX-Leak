package net.augustus.modules.world;

import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.*;
import net.augustus.utils.skid.azura.RaytraceUtil;
import net.augustus.utils.skid.gotaj.InventoryUtils;
import net.augustus.utils.skid.vestige.BlockInfo;
import net.augustus.utils.skid.vestige.WorldUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Genius
 * @since 2024/5/31 21:19
 * IntelliJ IDEA
 */

public class ScaffoldWalk extends Module {

    public BlockInfo b = null;

    public int lastSlot;

    public float[] rots, lastTargetRots;

    public double lastY;

    public boolean placed;

    public int airTicks = 0, jumpTicks = 0, sprintTicks = 0;

    public RotationUtil rotationUtil = new RotationUtil();

    private ArrayList<float[]> lastRotations = new ArrayList<>();

    public TimeHelper timer = new TimeHelper();

    public StringValue mode = new StringValue(1545, "Rotation Mode", this, "Normal", new String[] {"Normal", "NCP", "CorrectSide", "PitchSpeed", "Telly", "Test"});

    public BooleanValue interpolation = new BooleanValue(15821, "Interpolation", this, false);

    public DoubleValue number = new DoubleValue(2, "Block To Switch", this, 1.0, 0.0, 63.0, 1);

    public DoubleValue searchRange = new DoubleValue(6, "Search", this, 4.0, 1.0, 6.0, 1);

    public BooleanValue randomSearch = new BooleanValue(7, "RandomSearch", this, false);

    public BooleanValue randomRotation = new BooleanValue(8, "RandomRotation", this, false);

    public StringValue sprintValue = new StringValue(15, "SprintValue", this, "BeforeRotation", new String[] {"BeforeRotation", "WhenGround", "WhileNoRotation", "None", "BeforeJump", "OnGround"});

    public StringValue rotationTiming = new StringValue(14, "Rotation Timing", this, "WhenAir", new String[] {"Always", "WhenAir", "AirDelay", "AfterJump", "AfterJumpDelay", "SprintTicks"});

    public DoubleValue delay = new DoubleValue(17, "Delay", this, 2.0, 0.0, 10.0,1);

    public DoubleValue airPlaceDelay = new DoubleValue(5478, "AirPlaceDelay", this, 2.0, 0.0, 10.0,1);

    public DoubleValue yawSpeed = new DoubleValue(10, "YawSpeed", this, 60, 0.1, 180, 1);

    public DoubleValue pitchSpeed = new DoubleValue(11, "PitchSpeed", this, 60,0.1, 180,1);

    public BooleanValue movefix = new BooleanValue(1, "MoveFix", this, true);

    public BooleanValue rotationFix = new BooleanValue(79799, "RotationFix", this, false);

    public BooleanValue better = new BooleanValue(1549, "BetterRotationFix", this, true);

    public BooleanValue cancleSilentMove = new BooleanValue(165, "AdvancedSilentMoveFix", this, false);

    public BooleanValue cancleMove = new BooleanValue(164, "CancleAirMove", this, false);

    public BooleanValue cancleMoveInput = new BooleanValue(164, "CancleAirMoveInput", this, false);

    public BooleanValue sameY = new BooleanValue(4, "SameY", this, true);

    public StringValue raytrace = new StringValue(12, "RayTrace", this, "Normal", new String[] {"Normal", "Strict", "None"});

    public BooleanValue autoJump = new BooleanValue(5, "AutoJump", this, false);

    public BooleanValue smart = new BooleanValue(16, "SmartJump", this, true);

    public BooleanValue sprint = new BooleanValue(15454, "JumpWhileSprint", this, true);

    public StringValue jumpTiming = new StringValue(9, "JumpTiming", this, "BeforeRotation", new String[] {"BeforeRotation", "AfterRotation", "AfterPlaced", "SprintTicks", "NotRotation", "Parkou"});

    public BooleanValue noSwing = new BooleanValue(13, "NoSwing", this, true);

    public ScaffoldWalk() {
        super("ScaffoldWalk", new Color(-1), Categorys.WORLD);
    }

    @EventTarget
    public void onEventSendPacket(EventSendPacket eventSendPacket) {
        Packet packet = eventSendPacket.getPacket();
        MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), this.lastTargetRots[0], this.lastTargetRots[1]);

        if (this.placed && packet != null && packet instanceof C03PacketPlayer && !raytrace() && this.b != null &&  movingObjectPosition != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos()) && this.rotationFix.getBoolean()) {
            ((C03PacketPlayer) packet).setYaw(this.lastTargetRots[0]);
            ((C03PacketPlayer) packet).setPitch(this.lastTargetRots[1]);
        }

        if (this.placed && packet != null && packet instanceof C03PacketPlayer && !raytrace() && this.rotationFix.getBoolean()) {

            ((C03PacketPlayer) packet).setYaw(this.rots[0]);
            ((C03PacketPlayer) packet).setPitch(this.rots[1]);

        }
    }

    @EventTarget
    public void onEventMove(EventMove eventMove) {
        if (this.cancleMove.getBoolean() && mc.thePlayer != null && mc.theWorld != null && ((!mc.thePlayer.onGround && rotationTiming.getSelected().equals("WhenAir")) || (this.rotationTiming.getSelected().equals("AirDelay") && this.airTicks >= (int) this.delay.getValue()))) {
            eventMove.setCancelled(true);
        }

        if (this.cancleMoveInput.getBoolean() && mc.thePlayer != null && mc.theWorld != null && ((!mc.thePlayer.onGround && rotationTiming.getSelected().equals("WhenAir")) || (this.rotationTiming.getSelected().equals("AirDelay") && this.airTicks >= (int) this.delay.getValue()))) {
            mc.thePlayer.movementInput.moveForward = 0;
            mc.thePlayer.movementInput.moveStrafe = 0;
        }

        if (!this.movefix.getBoolean()) {
            eventMove.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
        }

    }

    @EventTarget
    public void onEventJump(EventJump eventJump) {
        if (!this.movefix.getBoolean()) {
            eventJump.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
        }
    }


    @EventTarget
    public void onEventSilentMove(EventSilentMove eventSilentMove) {
        if (this.movefix.getBoolean()) {
            eventSilentMove.setSilent(true);
        }
        if (this.cancleSilentMove.getBoolean()) {
            eventSilentMove.setAdvanced(true);
        }

    }

    @Override
    public void onEnable() {
        this.lastRotations.clear();

        this.placed = false;
        this.airTicks = 0;
        this.jumpTicks = 0;
        this.sprintTicks = 0;

        if (mc.theWorld != null && mc.thePlayer != null) {


            this.rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

            this.lastTargetRots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};


            this.lastY = mc.thePlayer.posY;

            if (mc.thePlayer != null) {
                this.lastSlot = mc.thePlayer.inventory.currentItem;
            }

        }

        timer.reset();

    }

    @Override
    public void onDisable() {
        Augustus.getInstance().getSlotSpoofer().stopSpoofing();

        timer.reset();

        this.airTicks = 0;
        this.jumpTicks = 0;
        this.sprintTicks = 0;

        this.lastRotations.clear();

        this.placed = false;

        if (mc.thePlayer != null && mc.theWorld != null) {

            this.b = null;

            if (this.autoJump.getBoolean() && mc.gameSettings.keyBindJump.pressed) {
                mc.gameSettings.keyBindJump.pressed = false;
            }

            this.rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

            this.lastTargetRots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

            this.lastY = mc.thePlayer.posY;

            this.back(mc.thePlayer);

        }
    }

    @EventTarget
    public void onEventTicks(EventTick eventTick) {

        this.lastSlot = Augustus.getInstance().getSlotSpoofer().getSpoofedSlot();
        if (this.getBlock() != -1) {
            if (shouldBlock(mc.thePlayer)) {
                mc.thePlayer.inventory.currentItem = this.getBlock();
            }
        }
        Augustus.getInstance().getSlotSpoofer().startSpoofing(this.lastSlot);

        MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), this.lastTargetRots[0], this.lastTargetRots[1]);

        if (this.placed && !raytrace() && this.b != null && movingObjectPosition != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos()) && this.rotationFix.getBoolean()) {
            mc.thePlayer.rotationYaw = lastTargetRots[0];
            mc.thePlayer.rotationPitch = lastTargetRots[1];
        }

        if (this.better.getBoolean() && this.getRayTraceRotation() != null) {
            mc.thePlayer.rotationYaw = this.getRayTraceRotation()[0];
            mc.thePlayer.rotationPitch = this.getRayTraceRotation()[1];
        }

        if (mc.thePlayer.isSprinting()) {
            this.sprintTicks++;
        } else {
            this.sprintTicks = 0;
        }

        if (mc.gameSettings.keyBindJump.pressed) {
            this.jumpTicks++;
        } else {
            this.jumpTicks = 0;
        }

        if (mc.thePlayer.onGround) {
            this.airTicks = 0;
        } else {
            this.airTicks ++;
        }

        if (this.sprintValue.getSelected().equals("OnGround") && mc.thePlayer.onGround) {
            mc.thePlayer.setSprinting(true);
        }

        if (this.jumpTiming.getSelected().equals("NotRotation") && this.smart.getBoolean()) {
            if ((Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean()) && (mc.thePlayer.rotationYaw == Augustus.getInstance().getYawPitchHelper().realYaw)) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        } else if (this.jumpTiming.getSelected().equals("NotRotation") && !this.smart.getBoolean()) {
            if (mc.thePlayer.onGround && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean()) && mc.thePlayer.rotationYaw == Augustus.getInstance().getYawPitchHelper().realYaw) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {

        if(mc.thePlayer.ticksExisted < 10) {
            this.setToggled(false);
            return;
        }

        if(this.sameY.getBoolean()) {
            if(mc.thePlayer.onGround || mc.gameSettings.keyBindJump.isEventKey()) {
                this.lastY = mc.thePlayer.posY;
            }
        } else {
            this.lastY = mc.thePlayer.posY;
        }

        this.b = WorldUtil.getBlockInfo(mc.thePlayer.posX, this.lastY - 0.5, mc.thePlayer.posZ, (int) this.searchRange.getValue());

        if (this.sprintValue.getSelected().equals("BeforeJump") && !mc.thePlayer.isJumping && mc.thePlayer.onGround) {
            if (MoveUtil.isMoving()) {
                mc.thePlayer.setSprinting(true);
            }
        }

        if ((this.sprintTicks >= (int) this.delay.getValue() && this.jumpTiming.getSelected().equals("SprintTicks")) && !this.smart.getBoolean()) {
            if ((mc.thePlayer.onGround && mc.thePlayer.isSprinting() && this.sprintTicks >= (int) this.delay.getValue()) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        } else if ((this.jumpTiming.getSelected().equals("SprintTicks")) && this.smart.getBoolean()) {
            if ((Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air && mc.thePlayer.isSprinting() && this.sprintTicks >= (int) this.delay.getValue()) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

        if (this.jumpTiming.getSelected().equals("BeforeRotation") && this.autoJump.getBoolean() && !this.smart.getBoolean()) {
            if ((mc.thePlayer.onGround && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean())) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        } else if (this.jumpTiming.getSelected().equals("BeforeRotation") && this.autoJump.getBoolean() && this.smart.getBoolean()) {
            if ((Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean())) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

        if (sprintValue.getSelected().equalsIgnoreCase("BeforeRotation") && mc.gameSettings.keyBindForward.isPressed()) {
            mc.thePlayer.setSprinting(true);
        }

        //Todo rotation






        if (this.b != null) {
            Vec3 vec = WorldUtil.getVec3(this.b.getPos(), this.b.getFacing(), this.randomSearch.getBoolean());
            if (this.rotationTiming.getSelected().equalsIgnoreCase("Always")
                    || (this.rotationTiming.getSelected().equals("AirDelay") && this.airTicks >= (int) this.delay.getValue())
                    || (this.rotationTiming.getSelected().equals("WhenAir") && !mc.thePlayer.onGround)
                    || (this.rotationTiming.getSelected().equals("AfterJump") && mc.thePlayer.isJumping)
                    || (this.rotationTiming.getSelected().equals("AfterJumpDelay") && this.jumpTicks >= (int) this.delay.getValue())
                    || (this.rotationTiming.getSelected().equals("SprintTicks") && this.sprintTicks >= (int) this.delay.getValue())
            ) {
                switch (this.mode.getSelected()) {
                    case "Normal": {
                        float yawSpeed = (float) this.yawSpeed.getValue();
                        float pitchSpeed =(float) this.pitchSpeed.getValue();
                        this.rots = RotationUtil.getRotationsToPositionSmooth(vec.xCoord, vec.yCoord, vec.zCoord, yawSpeed,  pitchSpeed);
                        break;
                    }
                    case "NCP": {
                        this.rots = RotationUtil.getRotations(this.b.getPos(), this.b.getFacing());
                        break;
                    }
                    case "Telly": {
                        float yawSpeed = (float) this.yawSpeed.getValue();
                        float pitchSpeed =(float) this.pitchSpeed.getValue();
                        Vec3 vec3 = this.getAimPosBasic();

                        if (vec3 == null) {
                            this.rots = lastTargetRots;
                        } else {
                            this.rots = this.rotationUtil.tellyRotation(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.lastTargetRots,  yawSpeed, pitchSpeed, this.randomRotation.getBoolean(), mc.timer.renderPartialTicks);
                        }
                        break;
                    }
                    case "CorrectSide": {
                        float yawSpeed = (float) this.yawSpeed.getValue();
                        float pitchSpeed =(float) this.pitchSpeed.getValue();
                        Vec3 vec3 = this.getAimPosBasic();

                        if (vec3 == null) {
                            this.rots = lastTargetRots;
                        } else {
                            this.rots = RotationUtil.positionRotation(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.lastTargetRots, yawSpeed, pitchSpeed, this.randomRotation.getBoolean());
                        }
                        break;
                    }
                    case "Test": {
                        float yawSpeed = MathHelper.clamp_float((float)(this.yawSpeed.getValue() + (double)RandomUtil.nextFloat(0.0F, 15.0F)), 0.0F, 180.0F);
                        float pitchSpeed = MathHelper.clamp_float((float)(this.pitchSpeed.getValue() + (double)RandomUtil.nextFloat(0.0F, 15.0F)), 0.0F, 180.0F);
                        this.rots = this.rotationUtil.test(this.lastTargetRots,yawSpeed, pitchSpeed);
                        break;
                    }
                    case "PitchSpeed": {
                        float yawSpeed = (float) this.yawSpeed.getValue();
                        float pitchSpeed =(float) this.pitchSpeed.getValue();
                        for (float i = 70f; i <= 90f; i+= pitchSpeed / 10) {
                            MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), Augustus.getInstance().getYawPitchHelper().realYaw + 180, i);

                            if (i >= 90f) {
                                i = 70f;
                            }

                            if (raytrace.getSelected().equals("Normal") && movingObjectPosition != null && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos())) {
                                this.rots = new float[] {Augustus.getInstance().getYawPitchHelper().realYaw + 180, i};
                            }

                            if (raytrace.getSelected().equals("Strict") && movingObjectPosition != null && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos()) && movingObjectPosition.sideHit == this.b.getFacing()) {
                                this.rots = new float[] {Augustus.getInstance().getYawPitchHelper().realYaw + 180, i};
                            }
                        }
                        break;
                    }
                }
                this.lastTargetRots = this.rots;
                this.lastRotations.add(this.rots);
            } else {
                this.rots = new float[] {Augustus.getInstance().yawPitchHelper.realYaw, Augustus.getInstance().yawPitchHelper.realPitch};
                if (sprintValue.getSelected().equalsIgnoreCase("WhenGround")) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        }

        if (mc.thePlayer.rotationYaw == Augustus.getInstance().getYawPitchHelper().realYaw && sprintValue.getSelected().equalsIgnoreCase("WhileNoRotation")) {
            mc.thePlayer.setSprinting(true);
        }

        if (this.rots != null) {
            this.setRotation();
        }

        if (this.jumpTiming.getSelected().equals("AfterRotation") && this.autoJump.getBoolean() && !this.smart.getBoolean()) {
            if ((mc.thePlayer.onGround && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean()) ) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        } else if (this.jumpTiming.getSelected().equalsIgnoreCase("AfterRotation") && this.autoJump.getBoolean() && this.smart.getBoolean()) {
            if ((Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean())) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

        //Todo place
        if(this.b != null && (raytrace.getSelected().equals("None") || raytrace())) {
             this.placed = placeBlock();
        }

        if (this.jumpTiming.getSelected().equals("AfterPlaced") && this.autoJump.getBoolean() && !this.smart.getBoolean()) {
            if ((mc.thePlayer.onGround && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean())) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        } else if (this.jumpTiming.getSelected().equalsIgnoreCase("AfterPlaced") && this.autoJump.getBoolean() && this.smart.getBoolean()) {
            if ((Eagle.getBlockUnderPlayer(mc.thePlayer) == Blocks.air && (mc.thePlayer.isSprinting() || !this.sprint.getBoolean())) || mc.gameSettings.keyBindJump.isPressed()) {
                mc.gameSettings.keyBindJump.pressed = true;
            } else {
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

    }

    private void setRotation() {

        if (mc.currentScreen == null) {
            mc.thePlayer.rotationYaw = this.rots[0];
            mc.thePlayer.rotationPitch = this.rots[1];
            mc.thePlayer.prevRotationYaw = this.lastTargetRots[0];
            mc.thePlayer.prevRotationPitch = this.lastTargetRots[1];
        }

    }

    public void back(@NotNull EntityPlayer playerIn) {
        playerIn.inventory.currentItem = this.lastSlot;
    }

    public int getBlock() {
        return InventoryUtils.getBlockSlot((int)this.number.getValue());
    }

    private static boolean isBlockValid(final Block block) {
        return (block.isFullBlock() || block == Blocks.glass) &&
                block != Blocks.sand &&
                block != Blocks.gravel &&
                block != Blocks.dispenser &&
                block != Blocks.command_block &&
                block != Blocks.noteblock &&
                block != Blocks.furnace &&
                block != Blocks.crafting_table &&
                block != Blocks.tnt &&
                block != Blocks.dropper &&
                block != Blocks.beacon;
    }


    private boolean raytrace() {

        MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

        if (raytrace.getSelected().equals("Normal") && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos())) {
            return true;
        }

        if (raytrace.getSelected().equals("Strict") && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos()) && movingObjectPosition.sideHit == this.b.getFacing()) {
            return true;
        }

        return false;
    }

    public boolean placeBlock() {
        if (this.airTicks >= this.airPlaceDelay.getValue()) {

            if (this.getBlock() != -1) {
                this.lastSlot = Augustus.getInstance().getSlotSpoofer().getSpoofedSlot();
                if (shouldBlock(mc.thePlayer)) {
                    mc.thePlayer.inventory.currentItem = this.getBlock();
                }
                Augustus.getInstance().getSlotSpoofer().startSpoofing(this.lastSlot);
            }

            return sendPlacing();
        }
        return false;
    }

    @EventTarget
    public void onEventSwing(EventSwingItemClientSide eventSwingItemClientSide) {
        if (this.noSwing.getBoolean()) {
            eventSwingItemClientSide.cancel = true;
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }
    }

    public boolean sendPlacing() {

        ItemStack stack = mc.thePlayer.getHeldItem();
        Vec3 vec = WorldUtil.getVec3(this.b.getPos(), this.b.getFacing(), this.randomSearch.getBoolean());

        boolean success = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, this.b.getPos(), this.b.getFacing(), vec);

        if (success) {
            mc.thePlayer.swingItem();
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack);
        }
        return success;
    }

    private Vec3 getAimPosBasic() {
        BlockPos b = this.b.getPos();
        if (b == null) {
            return null;
        } else {
            EnumFacing enumFacing = this.getPlaceSide(b);
            Block block = mc.theWorld.getBlockState(b).getBlock();
            double add = 0.01F;
            Vec3 min = null;
            Vec3 max = null;
            if (enumFacing != null) {
                if (enumFacing == EnumFacing.UP) {
                    min = new Vec3((double)b.getX() + add, (double)b.getY() + block.getBlockBoundsMaxY(), (double)b.getZ() + add);
                    max = new Vec3(
                            (double)b.getX() + block.getBlockBoundsMaxX() - add,
                            (double)b.getY() + block.getBlockBoundsMaxY(),
                            (double)b.getZ() + block.getBlockBoundsMaxZ() - add
                    );
                } else if (enumFacing == EnumFacing.WEST) {
                    min = new Vec3(b.getX(), (double)b.getY() + add, (double)b.getZ() + add);
                    max = new Vec3(
                            b.getX(), (double)b.getY() + block.getBlockBoundsMaxY() - add, (double)b.getZ() + block.getBlockBoundsMaxZ() - add
                    );
                } else if (enumFacing == EnumFacing.EAST) {
                    min = new Vec3((double)b.getX() + block.getBlockBoundsMaxX(), (double)b.getY() + add, (double)b.getZ() + add);
                    max = new Vec3(
                            (double)b.getX() + block.getBlockBoundsMaxX(),
                            (double)b.getY() + block.getBlockBoundsMaxY() - add,
                            (double)b.getZ() + block.getBlockBoundsMaxZ() - add
                    );
                } else if (enumFacing == EnumFacing.SOUTH) {
                    min = new Vec3((double)b.getX() + add, (double)b.getY() + add, (double)b.getZ() + block.getBlockBoundsMaxZ());
                    max = new Vec3(
                            (double)b.getX() + block.getBlockBoundsMaxX() - add,
                            (double)b.getY() + block.getBlockBoundsMaxY() - add,
                            (double)b.getZ() + block.getBlockBoundsMaxZ()
                    );
                } else if (enumFacing == EnumFacing.NORTH) {
                    min = new Vec3((double)b.getX() + add, (double)b.getY() + add, b.getZ());
                    max = new Vec3(
                            (double)b.getX() + block.getBlockBoundsMaxX() - add, (double)b.getY() + block.getBlockBoundsMaxY() - add, b.getZ()
                    );
                } else if (enumFacing == EnumFacing.DOWN) {
                    min = new Vec3((double)b.getX() + add, b.getY(), (double)b.getZ() + add);
                    max = new Vec3(
                            (double)b.getX() + block.getBlockBoundsMaxX() - add, b.getY(), (double)b.getZ() + block.getBlockBoundsMaxZ() - add
                    );
                }
            }

            return min != null ? this.getBestHit(min, max) : null;
        }
    }

    private Vec3 getBestHitFeet(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(), (double)blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double(mc.thePlayer.posY, blockPos.getY(), (double)blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(), (double)blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private EnumFacing getPlaceSide(BlockPos blockPos) {
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<>();
        double[] ichVercrafteDasAllesNichtMehr = new double[] {0,0};
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX + ichVercrafteDasAllesNichtMehr[0], mc.thePlayer.posY, mc.thePlayer.posZ + ichVercrafteDasAllesNichtMehr[1]);
        if (BlockUtil.isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equalsBlockPos(playerPos) && !mc.thePlayer.onGround) {
            BlockPos bp = blockPos.add(0, 1, 0);
            Vec3 vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.UP);
        }

        if (BlockUtil.isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(1, 0, 0);
            Vec3 vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.EAST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(-1, 0, 0);
            Vec3 vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.WEST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(0, 0, 1);
            Vec3 vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.SOUTH);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equalsBlockPos(playerPos)) {
            BlockPos bp = blockPos.add(0, 0, -1);
            Vec3 vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.NORTH);
        }

        positions.sort(Comparator.comparingDouble(vec3x -> mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        if (!positions.isEmpty()) {
            Vec3 vec3 = this.getBestHitFeet(this.b.getPos());
            if (mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)
                    >= mc.thePlayer.getDistance(positions.get(0).xCoord, positions.get(0).yCoord, positions.get(0).zCoord)) {
                return hashMap.get(positions.get(0));
            }
        }

        return null;
    }

    private Vec3 getBestHit(Vec3 min, Vec3 max) {
        double x = MathHelper.clamp_double(mc.thePlayer.posX, min.xCoord, max.xCoord);
        double y = MathHelper.clamp_double(mc.thePlayer.posY, min.yCoord, max.yCoord);
        double z = MathHelper.clamp_double(mc.thePlayer.posZ, min.zCoord, max.zCoord);
        return new Vec3(x, y, z);
    }

    public float[] getRayTraceRotation() {

        if (!this.lastRotations.isEmpty() && this.lastRotations.contains(this.lastTargetRots)) {
            this.lastRotations.add(this.lastTargetRots);
        }

        if (!this.lastRotations.isEmpty() && this.b != null) {
            for (float[] rots : this.lastRotations) {
                MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), rots[0], rots[1]);

                if (raytrace.getSelected().equals("Normal") && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos())) {
                    return rots;
                }

                if (raytrace.getSelected().equals("Strict") && this.b != null && movingObjectPosition.getBlockPos().equalsBlockPos(this.b.getPos()) && movingObjectPosition.sideHit == this.b.getFacing()) {
                    return rots;
                }
            }
        }

        return null;
    }

    public boolean shouldBlock(EntityPlayer playerIn) {

        ItemStack stack = mc.thePlayer.inventory.getCurrentItem();

        if (stack != null) {

            if (stack.getItem() instanceof ItemBlock && stack.stackSize > (int) this.number.getValue()) {
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
}
