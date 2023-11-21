package org.rent.circle.document.api.enums;

public enum Folder {
    LEASE("lease"),
    ADDENDUM("addendum");

    public final String value;

    Folder(String value) {
        this.value = value;
    }

    public static Folder fromString(String label) {
        for (Folder e : values()) {
            if (e.value.equalsIgnoreCase(label)) {
                return e;
            }
        }
        return null;
    }
}
