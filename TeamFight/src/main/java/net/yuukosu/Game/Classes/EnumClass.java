package net.yuukosu.Game.Classes;

import lombok.Getter;

public enum EnumClass {
    DEFAULT("デフォルト", new DefaultClass()),
    ARCHER("アーチャー", new ArcherClass()),
    SURVIVOR("サバイバー", new SurvivorClass());

    @Getter
    private final String name;
    @Getter
    private final ItemClass itemClass;

    EnumClass(String name, ItemClass itemClass) {
        this.name = name;
        this.itemClass = itemClass;
    }
}
