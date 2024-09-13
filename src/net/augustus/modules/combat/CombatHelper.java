package net.augustus.modules.combat;

import net.augustus.Augustus;
import net.augustus.events.EventJump;
import net.augustus.events.EventMove;
import net.augustus.events.EventSilentMove;
import net.augustus.events.EventUpdate;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.utils.RayTraceUtil;
import net.augustus.utils.RotationUtil;
import net.augustus.utils.TimeHelper;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class CombatHelper extends Module {
    public CombatHelper(){
        super("CombatHelper", Color.GREEN, Categorys.COMBAT);
    }
    public final BooleanValue AntiBurning = new BooleanValue(112111,"AntiBurning",this,false);
    public final BooleanValue collectwater = new BooleanValue(112077,"CollectWater",this,false);
    private final BooleanValue AutoThrowthing = new BooleanValue(112199,"AutoThrow",this,false);
    private final BooleanValue AutoRod = new BooleanValue(112200,"AutoRod",this,false);
    private final RotationUtil rotationUtil = new RotationUtil();

    private BlockPos water;
    private int tick = 0;
    private int ticks =0;
    private int d = 0;
    private int dd = 0;
    private int delay = 0;
    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper timeHelper2 = new TimeHelper();
    private boolean rod = true;
    private float[] rots = new float[2];
    private float[] lastRots = new float[2];
    @EventTarget
    public void onUpdate(EventUpdate event){
        this.AntiBurning.setVisible(true);
        this.AutoThrowthing.setVisible(true);
        this.AutoRod.setVisible(true);
        //auto-water
        BlockPos pos;
        if (AntiBurning.getBoolean() && this.getSlotWaterBucket() != -1 && KillAura.target == null){
            if (mc.thePlayer.isBurning()) {
                mc.thePlayer.inventory.currentItem = getSlotWaterBucket();
                tick++;
                pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                this.rots = this.rotationUtil
                        .positionRotation(
                                pos.getX(),
                                pos.getY() -1.53,
                                pos.getZ(),
                                this.lastRots[0],
                                this.lastRots[1],
                                180f,
                                180f,
                                false
                        );
                this.lastRots = this.rots;
                this.setPitch();
                if (tick >= 8) {
                    if (timeHelper.reached(50)) {
                        this.placeWater(pos);
                        timeHelper.reset();
                    }
                }
            } else {
                if (tick >9) {
                    tick = 0;
                }
            }
        }
        if (collectwater.getBoolean() && this.getSlotBucket() != -1 && mc.thePlayer.isInWater()){
            Vec3 waterPos = null;
            if (!mm.killAura.isToggled() || KillAura.target == null){
                waterPos = this.findWaterPos();
                this.water = waterPos == null ? null : new BlockPos(waterPos);
            }else {
                this.water =null;
            }
            if (this.water == null) {
                this.lastRots = this.rots;
            } else {
                mc.thePlayer.inventory.currentItem = getSlotBucket();
                ticks ++;
                Block block = mc.theWorld.getBlockState(this.water).getBlock();
                float[] floats = this.rotationUtil
                        .positionRotation(
                                this.water.getX(),
                                this.water.getY()-1.5,
                                this.water.getZ(),
                                this.rots[0],
                                this.rots[1],
                                360,
                                360,
                                false
                        );
                this.lastRots = this.rots;
                this.rots = floats;
                this.setRotation();
                if (ticks >=8){
                    if (timeHelper2.reached(50)) {
                        this.placeWater(water);
                        timeHelper2.reset();
                    }
                }
                if (ticks >=9){
                    if (mc.thePlayer.inventoryContainer.getSlot(this.getSlotBucket() + 36).getStack().getItem().equals(Item.getItemById(326)) && ticks >11){
                        this.setRotation();
                        ticks =0;
                    }
                }
            }
        }

        //throwable-things
        if (mm.killAura.isToggled() && mc.thePlayer.getDistanceToEntity(KillAura.target) > mm.killAura.rangeSetting.getValue() && AutoThrowthing.getBoolean() && this.throwablethings() != -1){
            mc.thePlayer.inventory.currentItem = throwablethings();

            d ++;

            if ((mc.thePlayer.inventoryContainer.getSlot(throwablethings() + 36).getStack().getItem().equals(Item.getItemById(344))) || (mc.thePlayer.inventoryContainer.getSlot(throwablethings() + 36).getStack().getItem().equals(Item.getItemById(332)))){
                if (d == 10 || KillAura.target.hurtTime == 9) {
                    rod= false;
                    mc.rightClickMouse();
                    d= 0;
                }
            }else {
                rod = true;
            }
        }
        if (mm.killAura.targets == null){
            d = 0;
        }
        //rod
        if (mm.killAura.isToggled() && mc.thePlayer.getDistanceToEntity(KillAura.target) > mm.killAura.rangeSetting.getValue() && AutoRod.getBoolean() && this.rodfindmoment() != -1 && rod) {
            mc.thePlayer.inventory.currentItem = rodfindmoment();
            if (mc.thePlayer.inventoryContainer.getSlot(rodfindmoment() + 36).getStack().getItem().equals(Item.getItemById(346))) {
                dd++;
                if (dd == 0) {
                    mc.rightClickMouse();
                }
                if (dd == delay){
                    dd = -1;
                }
                if(mc.thePlayer.getDistanceToEntity(KillAura.target) <= 8) {
                    delay = 15;
                }else if(mc.thePlayer.getDistanceToEntity(KillAura.target) <= 8) {
                    delay = 12;
                }else if(mc.thePlayer.getDistanceToEntity(KillAura.target) <= 7) {
                    delay = 10;
                }else if(mc.thePlayer.getDistanceToEntity(KillAura.target) <= 6) {
                    delay = 8;
                }else if(mc.thePlayer.getDistanceToEntity(KillAura.target) <= 5) {
                    delay = 5;
                }
            }
        }
    }
    @EventTarget
    public void onEventMove(EventMove eventMove) {
        if (this.water != null) {
            eventMove.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
        }
    }

    @EventTarget
    public void onEventJump(EventJump eventJump) {
        if (this.water != null) {
            eventJump.setYaw(Augustus.getInstance().getYawPitchHelper().realYaw);
        }
    }

    @EventTarget
    public void onEventSilentMove(EventSilentMove eventSilentMove) {
        if (this.water != null) {
            eventSilentMove.setSilent(true);
        }
    }
    private void placeWater(BlockPos pos){
        ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();
        if (heldItem != null) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
            mc.entityRenderer.itemRenderer.resetEquippedProgress2();
        }
        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), pos, EnumFacing.UP, new Vec3((double)pos.getX() + 0.5, (double)pos.getY() +1, (double)pos.getZ() + 0.5));

    }
    private int getSlotWaterBucket() {
        for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i){
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && itemStack.getItem() == Item.getItemById(326)){
                return i - 36;
            }
        }
        return -1;
    }
    private int getSlotBucket(){
        for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i){
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && itemStack.getItem() == Item.getItemById(325)){
                return i - 36;
            }
        }
        return -1;
    }
    private int rodfindmoment(){
        for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i){
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && itemStack.getItem() == Item.getItemById(346)){
                return i - 36;
            }
        }
        return -1;
    }

    private int throwablethings(){
        for(int i = 36; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); ++i){
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && (itemStack.getItem() == Item.getItemById(332) || itemStack.getItem() == Item.getItemById(344))){
                return i - 36;
            }
        }
        return -1;
    }
    private Vec3 findWaterPos() {
        BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, BlockPos> map = new HashMap<>();
        int d = (int)(5);

        for(int x = b.getX() - d; x < b.getX() + d; ++x) {
            for(int y = b.getY() - d; y < b.getY() + d; ++y) {
                for(int z = b.getZ() - d; z < b.getZ() + d; ++z) {
                    if (this.isValidBlock(new BlockPos(x, y, z))) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        Vec3 vec3 = new Vec3(x, y, z);
                        if (collectwater.getBoolean()) {
                            positions.add(vec3);
                            map.put(vec3, blockPos);
                        }
                    }
                }
            }
        }

        positions.sort(Comparator.comparingDouble(vec3x -> mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        return !positions.isEmpty() ? positions.get(0) : null;
    }
    private boolean isValidBlock(BlockPos blockPos) {
        if (blockPos != null) {
            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            IBlockState iblockstate = mc.theWorld.getBlockState(blockPos);
            return block.equals(Block.getBlockById(9)) && iblockstate.getValue(BlockLiquid.LEVEL) == 0;
        } else {
            return false;
        }
    }

    private void setRotation() {
        if (mc.currentScreen == null && (!mm.killAura.isToggled() || KillAura.target == null)) {
            mc.thePlayer.rotationYaw = this.rots[0];
            mc.thePlayer.rotationPitch = this.rots[1];
            mc.thePlayer.prevRotationYaw = this.lastRots[0];
            mc.thePlayer.prevRotationPitch = this.lastRots[1];
        }
    }
    private void setPitch() {
        if (mc.currentScreen == null && (!mm.killAura.isToggled() || KillAura.target == null)) {
            mc.thePlayer.rotationPitch = this.rots[1];
            mc.thePlayer.prevRotationPitch = this.lastRots[1];
        }
    }
}
