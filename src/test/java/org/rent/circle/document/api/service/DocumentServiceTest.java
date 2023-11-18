package org.rent.circle.document.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.dto.FormData;
import org.rent.circle.document.api.enums.Folder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@QuarkusTest
public class DocumentServiceTest {

    @InjectMock
    S3Client s3Client;

    @Inject
    DocumentService documentService;

    @Test
    public void upload_WhenCalled_ShouldUploadDocument() throws IOException {
        // Arrange
        Folder folder = Folder.LEASE;
        File file = File.createTempFile("test", "txt", new File("src/test/resources"));
        file.deleteOnExit();
        FormData formData = FormData.builder()
            .filename(file.getName())
            .mimetype("text")
            .data(file)
            .build();
        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();
        when(s3Client.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class))).thenReturn(
            putObjectResponse);

        // Act
        PutObjectResponse result = documentService.upload(folder, formData);

        // Assert
        assertNotNull(result);
        assertEquals(putObjectResponse, result);
    }

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
