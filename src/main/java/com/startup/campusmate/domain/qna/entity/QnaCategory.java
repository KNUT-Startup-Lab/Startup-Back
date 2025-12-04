package com.startup.campusmate.domain.qna.entity;

public enum QnaCategory {
    AIR_CONDITIONER("에어컨"),
    WIFI("WiFi"),
    WASHING_MACHINE("세탁기"),
    NOISE("소음"),
    FACILITY("시설"),
    OTHER("기타");

    private final String displayName;

    QnaCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static QnaCategory fromDisplayName(String displayName) {
        for (QnaCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        return OTHER;
    }
}
