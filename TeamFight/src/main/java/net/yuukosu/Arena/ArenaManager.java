package net.yuukosu.Arena;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.yuukosu.Game.EnumTeam;
import net.yuukosu.System.BlockControl.BlockDataEx;
import net.yuukosu.TeamFight;
import net.yuukosu.Utils.DatabaseUtils;
import org.bson.Document;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String mapName;
    @Setter
    @Getter
    private Location lobby;
    @Getter
    private final List<Location> chestLocations = new ArrayList<>();
    @Getter
    private final Map<EnumTeam, ArenaTeamData> arenaTeamData = new HashMap<>();

    public ArenaManager(String name) {
        this.name = name;
        this.mapName = name;
        this.arenaTeamData.put(EnumTeam.RED, new ArenaTeamData());
        this.arenaTeamData.put(EnumTeam.BLUE, new ArenaTeamData());
    }

    public void load() {
        MongoCollection<Document> collection = TeamFight.getTeamFightArenaCollection();
        Document baseDoc = collection.find(Filters.eq(this.name)).first();

        if (baseDoc != null && baseDoc.containsKey(this.name)) {
            Document doc = (Document) baseDoc.get(this.name);

            if (doc != null) {
                if (doc.containsKey("MAP_NAME")) {
                    this.mapName = doc.getString("MAP_NAME");
                }

                if (doc.containsKey("LOBBY")) {
                    this.lobby = DatabaseUtils.toLocation((Document) doc.get("LOBBY"));
                }

                if (doc.containsKey("CHESTS_DATA")) {
                    @SuppressWarnings("unchecked")
                    List<Document> list = (List<Document>) doc.get("CHESTS_DATA");
                    list.forEach(document -> this.chestLocations.add(DatabaseUtils.toLocation(document)));
                }

                if (doc.containsKey("TEAM_DATA")) {
                    Document teamDocs = (Document) doc.get("TEAM_DATA");

                    for (EnumTeam enumTeam : EnumTeam.values()) {
                        if (teamDocs.containsKey(enumTeam.name())) {
                            if (!this.isRegistered(enumTeam)) {
                                this.registerTeam(enumTeam);
                            }

                            Document teamDoc = (Document) teamDocs.get(enumTeam.name());

                            if (teamDoc.containsKey("CENTER")) {
                                this.arenaTeamData.get(enumTeam).setCenter(DatabaseUtils.toLocation((Document) teamDoc.get("CENTER")));
                            }

                            if (teamDoc.containsKey("SPAWNS")) {
                                @SuppressWarnings("unchecked")
                                List<Document> spawns = (List<Document>) teamDoc.get("SPAWNS");

                                spawns.forEach(document -> this.arenaTeamData.get(enumTeam).addSpawn(DatabaseUtils.toLocation(document)));
                            }

                            continue;
                        }

                        this.unregisterTeam(enumTeam);
                    }
                }
            }
        }
    }

    public void save() {
        TeamFight.getInstance().getConfig().set("ARENA_NAME", this.name);
        TeamFight.getInstance().saveConfig();

        MongoCollection<Document> collection = TeamFight.getTeamFightArenaCollection();
        Document doc = new Document();
        Document teamDocs = new Document();

        if (this.mapName != null) {
            doc.put("MAP_NAME", this.mapName);
        }

        if (this.lobby != null) {
            doc.put("LOBBY", DatabaseUtils.toDocument(this.lobby));
        }

        List<Document> chestsDataDocs = new ArrayList<>();
        this.chestLocations.forEach(chestsData -> chestsDataDocs.add(DatabaseUtils.toDocument(chestsData)));

        doc.put("CHESTS_DATA", chestsDataDocs);

        for (Map.Entry<EnumTeam, ArenaTeamData> entry : this.arenaTeamData.entrySet()) {
            Document teamDoc = new Document();

            if (entry.getValue().getCenter() != null) {
                teamDoc.put("CENTER", DatabaseUtils.toDocument(entry.getValue().getCenter()));
            }

            List<Document> spawnDocs = new ArrayList<>();

            for (Location location : entry.getValue().getSpawns()) {
                spawnDocs.add(new Document(DatabaseUtils.toDocument(location)));
            }

            teamDoc.put("SPAWNS", spawnDocs);
            teamDocs.put(entry.getKey().name(), teamDoc);
        }

        doc.put("TEAM_DATA", teamDocs);

        collection.updateOne(Filters.eq(this.name), new Document("$set", new Document(this.name, doc)), DatabaseUtils.getUpdateOptions());
    }

    public void registerTeam(EnumTeam gameTeam) {
        this.arenaTeamData.put(gameTeam, new ArenaTeamData());
    }

    public void unregisterTeam(EnumTeam gameTeam) {
        this.arenaTeamData.remove(gameTeam);
    }

    public ArenaTeamData getTeamArenaData(EnumTeam enumTeam) {
        return this.arenaTeamData.get(enumTeam);
    }

    public void addChestLocation(Location location) {
        this.chestLocations.add(location);
    }

    public void removeChestLocation(Location location) {
        this.chestLocations.remove(location);
    }

    public boolean containsChestLocation(Location location) {
        return this.chestLocations.contains(location);
    }

    public boolean isRegistered(EnumTeam gameTeam) {
        return this.arenaTeamData.containsKey(gameTeam);
    }

    public boolean complete() {
        return this.name != null && this.lobby != null && this.arenaTeamData.size() >= 2 && this.arenaTeamData.values().stream().allMatch(ArenaTeamData::complete);
    }
}
