package net.yuukosu.Utils;

import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DatabaseUtils {

    public static Location toLocation(Document document) {
        return new Location(
                Bukkit.getWorld(document.getString("WORLD")),
                document.getDouble("X"),
                document.getDouble("Y"),
                document.getDouble("Z"),
                document.getDouble("YAW").floatValue(),
                document.getDouble("PITCH").floatValue()
        );
    }

    public static Document toDocument(Location location) {
        Document document = new Document();
        document.put("WORLD", location.getWorld().getName());
        document.put("X", location.getX());
        document.put("Y", location.getY());
        document.put("Z", location.getZ());
        document.put("YAW", location.getYaw());
        document.put("PITCH", location.getPitch());

        return document;
    }

    public static UpdateOptions getUpdateOptions() {
        return new UpdateOptions().upsert(true);
    }
}
