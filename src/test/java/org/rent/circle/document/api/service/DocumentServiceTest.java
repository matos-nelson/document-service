package org.rent.circle.document.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rent.circle.document.api.dto.FileObject;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@QuarkusTest
public class DocumentServiceTest {

    @InjectMock
    S3Client s3Client;

    @Inject
    DocumentService documentService;

    @Test
    public void getFileListing_WhenCalled_ShouldReturnFileListing() {
        // Arrange
        long ownerId = 123L;
        S3Object s3Object = S3Object.builder().key("file.txt").size(100L).build();
        ListObjectsResponse listObjectsResponse = ListObjectsResponse.builder()
            .contents(Collections.singleton(s3Object))
            .build();
        when(s3Client.listObjects(Mockito.any(ListObjectsRequest.class))).thenReturn(listObjectsResponse);

        // Act
        List<FileObject> result = documentService.getFileListing(ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
