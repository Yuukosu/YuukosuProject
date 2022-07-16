package net.yuukosu.System;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.yuukosu.Utils.DatabaseUtils;
import net.yuukosu.YuukosuHub;
import org.bson.Document;
import org.bukkit.Location;

public class HubData {

    @Setter
    @Getter
    private Location spawn;

    public HubData() {
    }

    public void load() {
        MongoCollection<Document> collection = YuukosuHub.getHubDataCollection();
        Document spawnDoc = collection.find(Filters.eq("SPAWN")).first();

        if (spawnDoc != null && !spawnDoc.isEmpty()) {
            this.spawn = DatabaseUtils.toLocation(spawnDoc);
        }
    }

    public void save() {
        MongoCollection<Document> collection = YuukosuHub.getHubDataCollection();

        if (this.spawn != null) {
            Document spawnDoc = DatabaseUtils.toDocument(this.spawn);
            collection.updateOne(Filters.eq("SPAWN"), new Document("$set", spawnDoc), DatabaseUtils.getUpdateOptions());
        }
    }
}
