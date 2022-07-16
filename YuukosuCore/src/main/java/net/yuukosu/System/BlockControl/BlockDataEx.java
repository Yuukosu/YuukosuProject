package net.yuukosu.System.BlockControl;

import lombok.Getter;
import net.yuukosu.Utils.DatabaseUtils;
import net.yuukosu.YuukosuCore;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockDataEx extends BlockData {

    @Getter
    private final Location location;

    public BlockDataEx(Location location) {
        this.location = location;
    }

    public BlockDataEx(Location location, BlockData blockData) {
        super(blockData.getMaterial(), blockData.getData());
        this.location = location;
    }

    public BlockDataEx(Block block) {
        super(block);
        this.location = block.getLocation();
    }

    public void placeBlock() {
        super.place(this.location);
    }

    public void breakBlock(boolean animation) {
        Block block = this.location.getBlock();

        if (block.hasMetadata("CLICKABLE_BLOCK")) {
            block.removeMetadata("CLICKABLE_BLOCK", YuukosuCore.getInstance());
        }

        if (animation) {
            block.breakNaturally();
            return;
        }

        block.setType(Material.AIR);
    }

    public Location getCenterLocation() {
        return new Location(this.location.getWorld(), this.location.getX() + 0.5D, this.location.getY(), this.location.getZ() + 0.5D);
    }

    @Override
    public Document toDocument() {
        Document doc = super.toDocument();
        doc.put("LOCATION", DatabaseUtils.toDocument(this.location));

        return doc;
    }

    public static BlockDataEx toBlockDataEx(Document document) {
        BlockDataEx blockDataEx = null;

        if (document.containsKey("LOCATION")) {
            blockDataEx = new BlockDataEx(DatabaseUtils.toLocation((Document) document.get("LOCATION")), BlockData.toBlockData(document));
        }

        return blockDataEx;
    }

    public boolean equals(BlockDataEx blockDataEx) {
        return this.location == blockDataEx.getLocation() && this.getMaterial() == blockDataEx.getMaterial() && this.getData() == blockDataEx.getData();
    }
}
