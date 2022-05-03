package net.yuukosu.System.BlockControl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class MultiBlock {

    @Getter
    private final List<BlockDataEx> blocks = new ArrayList<>();

    public MultiBlock() {
    }

    public void breakInsertedBlocks(boolean animation) {
        this.blocks.forEach(block -> block.breakBlock(animation));
    }

    public void place(boolean skipAir) {
        if (skipAir && this.blocks.stream().allMatch(block -> block.getMaterial() == Material.AIR)) {
            return;
        }

        this.blocks.forEach(block -> {
            Location location = block.getLocation();

            if (skipAir && location.getBlock().getType() == Material.AIR) {
                return;
            }

            block.placeBlock();
        });
    }

    public void addBlock(BlockDataEx block) {
        this.blocks.add(block);
    }

    public void removeBlock(int index) {
        this.blocks.remove(index);
    }

    public void insertAroundBlock(Location center, int rx, int ry, int rz) {
        this.blocks.clear();
        this.getAroundBlocks(center, rx, ry, rz).forEach(block -> this.blocks.add(new BlockDataEx(block)));
    }

    public List<Block> getAroundBlocks(Location center, int rx, int ry, int rz) {
        List<Block> blocks = new ArrayList<>();

        for (int x = center.getBlockX() - rx; x < center.getBlockX() + rx; x++) {
            for (int y = center.getBlockY() - ry; y < center.getBlockY() + ry; y++) {
                for (int z = center.getBlockZ() - rz; z < center.getBlockZ() + rz; z++) {
                    blocks.add(new Location(center.getWorld(), x, y, z).getBlock());
                }
            }
        }

        return blocks;
    }
}
