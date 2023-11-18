package org.rent.circle.document.api.enums;

public enum Folder {
    LEASE("lease"),
    ADDENDUM("addendum");

    public final String value;

    Folder(String value) {
        this.value = value;
    }
}
