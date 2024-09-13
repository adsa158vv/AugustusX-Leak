package net.augustus.utils.skid.vestige;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@Getter
@AllArgsConstructor
public class BlockInfo {

    private BlockPos pos;
    private EnumFacing facing;

}