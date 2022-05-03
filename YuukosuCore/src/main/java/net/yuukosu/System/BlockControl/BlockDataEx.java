package net.yuukosu.System.BlockControl;

import lombok.Getter;
import net.yuukosu.Utils.DatabaseUtils;
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
        Block block = this.location.getBlock();
        block.setType(this.getMaterial());
        block.setData(this.getData());
        block.getState().update();
    }

    public void breakBlock(boolean animation) {
        if (animation) {
            this.location.getBlock().breakNaturally();
            return;
        }

        this.location.getBlock().setType(Material.AIR);
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
