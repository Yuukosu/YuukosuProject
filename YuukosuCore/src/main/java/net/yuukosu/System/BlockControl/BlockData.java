package net.yuukosu.System.BlockControl;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockData {

    @Setter
    @Getter
    private Material material;
    @Setter
    @Getter
    private byte data;

    public BlockData() {
        this.material = Material.AIR;
        this.data = 0;
    }

    public BlockData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public BlockData(Block block) {
        this.material = block.getType();
        this.data = block.getData();
    }

    public void place(Location location) {
        Block block = location.getBlock();
        block.setType(this.material);
        block.setData(this.data);

        block.getState().update();
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.put("MATERIAL", this.material.name());
        doc.put("DATA", this.data);

        return doc;
    }

    public static BlockData toBlockData(Document doc) {
        return new BlockData(Material.valueOf(doc.getString("MATERIAL")), doc.getInteger("DATA").byteValue());
    }
}
