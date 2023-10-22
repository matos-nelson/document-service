package org.rent.circle.document.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FileObjectTest {

    @Test
    public void FileObject_WhenGivenAKeyThatDoesNotContainForwardSlash_ShouldSetDirectoryAsRoot() {
        // Arrange
        String key = "file.txt";
        Long size = 10L;

        // Act
        FileObject result = new FileObject(key, size);

        // Assert
        assertEquals("/", result.getDirectory());
        assertEquals(key, result.getKey());
        assertEquals(size, result.getSize());
        assertEquals(key, result.getFileName());
    }

    @Test
    public void FileObject_WhenGivenAKeyThatIsADirectoryWithFileName_ShouldSetFields() {
        // Arrange
        String folder = "/folder/";
        String fileName = "file.txt";
        String key = folder + fileName;
        Long size = 10L;

        // Act
        FileObject result = new FileObject(key, size);

        // Assert
        assertEquals(folder, result.getDirectory());
        assertEquals(key, result.getKey());
        assertEquals(size, result.getSize());
        assertEquals(fileName, result.getFileName());
    }
}
