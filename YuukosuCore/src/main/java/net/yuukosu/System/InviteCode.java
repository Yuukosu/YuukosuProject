package net.yuukosu.System;

import lombok.Getter;
import org.bson.Document;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class InviteCode {

    @Getter
    private final String code;
    @Getter
    private final long time;

    public InviteCode() {
        UUID uuid = UUID.randomUUID();
        String[] split = uuid.toString().split("-");
        StringBuilder code = new StringBuilder();

        for (String s : split) {
            code.append(s.charAt(new Random().nextInt(s.length())));
        }

        this.code = code.toString();
        this.time = new Date().getTime();
    }

    public InviteCode(Document document) {
        this.code = document.getString("CODE");
        this.time = document.getLong("TIME");
    }

    public Document toDocument() {
        Document doc = new Document();
        doc.put("CODE", this.code);
        doc.put("TIME", this.time);

        return doc;
    }

    public static InviteCode toInviteCode(Document document) {
        return new InviteCode(document);
    }

    public boolean equals(InviteCode inviteCode) {
        return inviteCode != null && this.code.equals(inviteCode.getCode()) && this.time == inviteCode.getTime();
    }
}
