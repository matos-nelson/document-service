package org.rent.circle.document.api.dto;

import lombok.Getter;

@Getter
public class FileObject {

    private final String key;
    private final String directory;
    private final String fileName;
    private final Long size;

    public FileObject(String key, Long size) {
        this.size = size;
        this.key = key;

        if (!key.contains("/")) {
            this.directory = "/";
            this.fileName = key;
        } else {
            int index = key.lastIndexOf('/');
            this.directory = key.substring(0, index + 1);
            this.fileName = key.substring(index + 1);
        }
    }
}
