package org.rent.circle.document.api.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FolderTest {

    @Test
    public void fromString_WhenGivenAnInvalidLabel_ShouldReturnNull() {
        // Arrange

        // Act
        Folder result = Folder.fromString("label");

        // Assert
        assertNull(result);
    }

    @Test
    public void fromString_WhenCalled_ShouldReturnFolder() {
        // Arrange

        // Act
        Folder result = Folder.fromString("lease");

        // Assert
        assertNotNull(result);
        assertEquals(Folder.LEASE, result);
    }
}