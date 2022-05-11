package net.yuukosu.System;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.yuukosu.Utils.DatabaseUtils;
import net.yuukosu.YuukosuCore;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class InviteCodeManager {

    @Getter
    private final List<InviteCode> inviteCodes = new ArrayList<>();

    public InviteCodeManager() {
    }

    public InviteCode generateCode() {
        InviteCode inviteCode = new InviteCode();

        while (this.containsCode(inviteCode)) {
            inviteCode = new InviteCode();
        }

        this.inviteCodes.add(inviteCode);
        this.save();

        return inviteCode;
    }

    public void removeInviteCode(int index) {
        this.inviteCodes.remove(index);
    }

    public InviteCode getInviteCode(String code) {
        this.load();
        return this.inviteCodes.stream().filter(inviteCode -> inviteCode.getCode().equals(code)).findFirst().orElse(null);
    }

    public void save() {
        MongoCollection<Document> collection = YuukosuCore.getCoreDataCollection();
        Document doc = new Document();
        List<Document> list = new ArrayList<>();

        this.inviteCodes.forEach(inviteCode -> list.add(inviteCode.toDocument()));
        doc.put("INVITE_CODES", list);
        collection.updateOne(Filters.eq("INVITE_CODES"), new Document("$set", doc), DatabaseUtils.getUpdateOptions());
    }

    @SuppressWarnings("unchecked")
    public void load() {
        MongoCollection<Document> collection = YuukosuCore.getCoreDataCollection();
        Document doc = collection.find(Filters.eq("INVITE_CODES")).first();

        if (doc != null) {
            ((List<Document>) doc.get("INVITE_CODES")).forEach(document -> {
                InviteCode inviteCode = InviteCode.toInviteCode(document);

                if (this.inviteCodes.stream().noneMatch(inviteCode::equals)) {
                    this.inviteCodes.add(inviteCode);
                }
            });
        }
    }

    public boolean containsCode(InviteCode inviteCode) {
        return this.inviteCodes.stream().anyMatch(inviteCode1 -> inviteCode.getCode().equals(inviteCode1.getCode()));
    }

    public boolean checkInviteCode(InviteCode inviteCode) {
        this.load();
        return inviteCode != null && this.inviteCodes.stream().anyMatch(inviteCode1 -> inviteCode.getCode().equals(inviteCode1.getCode()) && inviteCode.getTime() == inviteCode1.getTime());
    }
}
