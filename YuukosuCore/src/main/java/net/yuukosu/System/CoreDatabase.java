package net.yuukosu.System;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

public class CoreDatabase {

    @Getter
    private final MongoClient client;
    @Getter
    private final MongoDatabase database;

    public CoreDatabase(String host, int port, String database) {
        this.client = new MongoClient(new ServerAddress(host, port));
        this.database = this.client.getDatabase(database);
    }

    @SuppressWarnings("deprecation")
    public CoreDatabase(String host, int port, String database, String user, char[] password) {
        MongoCredential credential = MongoCredential.createScramSha1Credential(user, database, password);
        this.client = new MongoClient(new ServerAddress(host, port), Collections.singletonList(credential));
        this.database = this.client.getDatabase(database);
    }

    public MongoDatabase getDatabase(String database) {
        return this.client.getDatabase(database);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return this.database.getCollection(collectionName);
    }
}
