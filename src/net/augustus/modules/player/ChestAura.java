package net.augustus.modules.player;

import net.augustus.Augustus;
import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.KillAura;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.ColorSetting;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.PrePostUtil;
import net.augustus.utils.RenderUtil;
import net.augustus.utils.RotationUtil;
import net.augustus.utils.skid.azura.RaytraceUtil;
import net.augustus.utils.skid.xylitol.PacketUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Genius
 * @since 2024/7/15 下午9:24
 * IntelliJ IDEA
 */

public class ChestAura extends Module {

    public ChestAura() {
        super("ChestAura", Color.white, Categorys.WORLD);
    }

    public StringValue clickMode = new StringValue(6, "ClickMode", this, "Pre", new String[] {"Pre", "Post"});

    public DoubleValue searchRange = new DoubleValue(1, "Range", this, 3.2, 0.0, 10.0, 1);

    public BooleanValue moveFix = new BooleanValue(5, "MoveFix", this, true);

    public BooleanValue esp = new BooleanValue(3, "ESP", this, false);

    public StringValue mode = new StringValue(4829, "Mode", this, "Box", new String[]{"Box", "OtherBox", "FakeCorner", "Fake2D", "Real2D"});


    public ColorSetting color = new ColorSetting(4, "ESPColor", this, new Color(21, 121, 230, 65));

    public DoubleValue lineWidth = new DoubleValue(4828, "LineWidth", this, 6.0, 0.0, 15.0, 0);

    public BooleanValue disableOnWorld = new BooleanValue(458499, "DisabledOnWorld", this, true);

    private final java.util.ArrayList<TileEntity> tileEntities = new java.util.ArrayList<>();
    private final java.util.ArrayList<TileEntity> notRender = new java.util.ArrayList<>();
    private final java.util.ArrayList<TileEntity> clicked = new ArrayList<>();

    public KillAura killAura;

    public float[] rots, lastTargetRots;

    public RotationUtil rotationUtil = new RotationUtil();

    /*
    @EventTarget
    public void onEventSilentMove(EventSilentMove eventSilentMove) {

        if (this.moveFix.getBoolean() && KillAura.target == null) {
            eventSilentMove.setSilent(true);
        }

    }

     */

    @EventTarget
    public void onMove(EventMove eventMove) {
        if (this.moveFix.getBoolean() && KillAura.target == null && !mc.thePlayer.onGround) {
            mc.thePlayer.movementInput.moveForward = 0;
            mc.thePlayer.movementInput.moveStrafe = 0;
        }

    }

    @EventTarget
    public void onWorld(EventWorld eventWorld) {

        reset();
        if (disableOnWorld.getBoolean()) {
            this.setToggled(false);
        }

    }

    @Override
    public void onEnable() {
        reset();

        killAura = mm.killAura;

        if (mc.thePlayer != null) {
            this.rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            this.lastTargetRots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }

    }

    public void reset() {
        this.clicked.clear();
        this.tileEntities.clear();
        this.notRender.clear();
        if (mc.thePlayer != null)
        this.lastTargetRots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
    }

    @EventTarget
    public void onEventTick(EventTick eventTick) {
        if (mc.theWorld != null) {
            this.setSuffix(this.clickMode.getSelected() + this.mode.getSelected() + "R: " + this.searchRange.getValue(), true);
            this.tileEntities.clear();
            this.notRender.clear();

            for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                if ((tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)
                        && mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ())
                        < this.searchRange.getValue() * 16.0f) {
                    if (tileEntity instanceof TileEntityChest) {
                        TileEntityChest tileEntityChest = (TileEntityChest) tileEntity;
                        if (tileEntityChest.adjacentChestXNeg != null && !this.tileEntities.contains(tileEntityChest.adjacentChestXNeg)) {
                            this.notRender.add(tileEntityChest.adjacentChestXNeg);
                        } else if (tileEntityChest.adjacentChestZNeg != null && !this.tileEntities.contains(tileEntityChest.adjacentChestZNeg)) {
                            this.notRender.add(tileEntityChest.adjacentChestZNeg);
                        } else if (tileEntityChest.adjacentChestZPos != null && !this.tileEntities.contains(tileEntityChest.adjacentChestZPos)) {
                            this.notRender.add(tileEntityChest.adjacentChestZPos);
                        } else if (tileEntityChest.adjacentChestXPos != null && !this.tileEntities.contains(tileEntityChest.adjacentChestXPos)) {
                            this.notRender.add(tileEntityChest.adjacentChestXPos);
                        }
                    }

                    this.tileEntities.add(tileEntity);
                }
            }

            this.tileEntities.removeIf(this.notRender::contains);
            if (!this.clicked.isEmpty()) {
                this.tileEntities.removeIf(this.clicked::contains);
            }
        }
    }

    @Override
    public void onDisable() {
        reset();

        if (mc.thePlayer != null) {
            this.rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            this.lastTargetRots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        }

    }

    @EventTarget
    public void onEventUpdate(EventUpdate eventUpdate) {

        if (!tileEntities.isEmpty() && !(mc.currentScreen instanceof GuiChest)) {

            for (TileEntity tileEntity : tileEntities) {

                if (mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ())
                        <= this.searchRange.getValue()) {

                    BlockPos blockPos = tileEntity.getPos();

                    if (mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= this.searchRange.getValue()) {
                        this.rots = RotationUtil.getRotationsToPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        this.setRotation();
                    }

                    this.lastTargetRots = this.rots;

                    //MovingObjectPosition movingObjectPosition = RaytraceUtil.rayTrace(this.searchRange.getValue(), this.rots[0], this.rots[1]);

                    //if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockPos)) {

                    switch (this.clickMode.getSelected()) {
                        case "Pre": {
                            if (PrePostUtil.isPre()) {

                                this.sendClick(blockPos);
                                this.clicked.add(tileEntity);

                            }
                            break;
                        }
                        case "Post": {
                            if (PrePostUtil.isPost()) {

                                this.sendClick(blockPos);
                                this.clicked.add(tileEntity);

                            }
                            break;
                        }
                    }

                }

            }

        }

    }

    public void sendClick(BlockPos pos) {
        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, (double)pos.getY() + 0.5 < ChestAura.mc.thePlayer.posY + 1.7 ? 1 : 0, ChestAura.mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
        ChestAura.mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    public void setRotation() {
        if (mc.currentScreen == null) {
            mc.thePlayer.rotationYaw = this.rots[0];
            mc.thePlayer.rotationPitch = this.rots[1];
            mc.thePlayer.prevRotationYaw = this.lastTargetRots[0];
            mc.thePlayer.prevRotationPitch = this.lastTargetRots[1];
        }
    }

/*
    public void setBackRotation() {
        mc.thePlayer.prevRotationYaw = mc.thePlayer.rotationYaw;
        mc.thePlayer.rotationYaw = Augustus.getInstance().getYawPitchHelper().realYaw;
        mc.thePlayer.prevRotationPitch = Augustus.getInstance().getYawPitchHelper().realPitch;
        mc.thePlayer.rotationPitch = this.rots[1];
    }

 */

    @EventTarget
    public void onEventRender3D(EventRender3D eventRender3D) {

        if (esp.getBoolean()) {
            float red = (float) this.color.getColor().getRed() / 225.0F;
            float green = (float) this.color.getColor().getGreen() / 225.0F;
            float blue = (float) this.color.getColor().getBlue() / 225.0F;
            float alpha = (float) this.color.getColor().getAlpha() / 225.0F;

            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);

            for (TileEntity blockEntity : this.tileEntities) {
                this.render(blockEntity, red, green, blue, alpha);
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

    private void render(TileEntity tileEntity, float red, float green, float blue, float alpha) {
        float lineWidth = (float)(this.lineWidth.getValue() / 2.0);
        if (mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) > 1.0) {
            double d0 = 1.0
                    - mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) / 20.0;
            if (d0 < 0.3) {
                d0 = 0.3;
            }

            lineWidth = (float)((double)lineWidth * d0);
        }

        String var9 = this.mode.getSelected();
        switch(var9) {
            case "Box": {
                RenderUtil.drawBlockESP(tileEntity, red, green, blue, alpha, 1.0F, lineWidth, false);
                break;
            }
            case "OtherBox": {
                RenderUtil.drawBlockESP(tileEntity, red, green, blue, alpha, 1.0F, lineWidth, true);
                break;
            }
            case "FakeCorner": {
                RenderUtil.drawCornerESP(tileEntity, red, green, blue);
                break;
            }
            case "Fake2D": {
                RenderUtil.drawFake2DESP(tileEntity, red, green, blue);
                break;
            }
        }
    }

}
