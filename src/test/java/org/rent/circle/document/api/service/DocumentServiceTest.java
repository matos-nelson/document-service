package org.rent.circle.document.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.dto.FormData;
import org.rent.circle.document.api.enums.Folder;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@QuarkusTest
public class DocumentServiceTest {

    @InjectMock
    S3Client s3Client;

    @InjectMock
    S3Presigner s3Presigner;

    @Inject
    DocumentService documentService;

    @Test
    public void upload_WhenGivenUserIdIsNull_ShouldThrowException() {
        // Arrange

        // Act
        // Assert
        assertThrows(ConstraintViolationException.class, () ->
            documentService.upload(null, Folder.LEASE, FormData.builder().build()));

    }

    @Test
    public void upload_WhenCalled_ShouldUploadDocument() throws IOException {
        // Arrange
        String userId = "abc123";
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
        PutObjectResponse result = documentService.upload(userId, folder, formData);

        // Assert
        assertNotNull(result);
        assertEquals(putObjectResponse, result);
    }

    @Test
    public void download_WhenGivenUserIdIsNull_ShouldThrowException() {
        // Arrange

        // Act
        // Assert
        assertThrows(ConstraintViolationException.class, () ->
            documentService.download(null, Folder.LEASE, "file.txt"));

    }

    @Test
    public void download_WhenFileDoesNotExist_ShouldReturnNull() {
        // Arrange
        String userId = "abc123";
        doThrow(AwsServiceException.builder().build()).when(s3Client)
            .getObjectAsBytes(Mockito.any(GetObjectRequest.class));

        // Act
        ResponseBytes<GetObjectResponse> result = documentService.download(userId, Folder.LEASE, "file.txt");

        // Assert
        assertNull(result);
    }

    @Test
    public void download_WhenCalled_ShouldReturnDocument() {
        // Arrange
        String userId = "abc123";
        byte[] bytes = new byte[10];
        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(getObjectResponse, bytes);
        when(s3Client.getObjectAsBytes(Mockito.any(GetObjectRequest.class))).thenReturn(responseBytes);

        // Act
        ResponseBytes<GetObjectResponse> result = documentService.download(userId, Folder.LEASE, "file.txt");

        // Assert
        assertNotNull(result);
        assertEquals(responseBytes, result);
    }

    @Test
    public void getFileListing_WhenGivenUserIdIsNull_ShouldThrowException() {
        // Arrange

        // Act
        // Assert
        assertThrows(ConstraintViolationException.class, () ->
            documentService.getFileListing(null));

    }

    @Test
    public void getFileListing_WhenCalled_ShouldReturnFileListing() {
        // Arrange
        String userId = "abc123";
        S3Object s3Object = S3Object.builder().key("file.txt").size(100L).build();
        ListObjectsResponse listObjectsResponse = ListObjectsResponse.builder()
            .contents(Collections.singleton(s3Object))
            .build();
        when(s3Client.listObjects(Mockito.any(ListObjectsRequest.class))).thenReturn(listObjectsResponse);

        // Act
        List<FileObject> result = documentService.getFileListing(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void generateUrl_WhenGivenUserIdIsNull_ShouldThrowException() {
        // Arrange

        // Act
        // Assert
        assertThrows(ConstraintViolationException.class, () ->
            documentService.generateUrl(null, Folder.LEASE, "file.txt"));

    }

    @Test
    public void generateUrl_WhenCalled_ShouldReturnUrl() throws URISyntaxException, MalformedURLException {
        // Arrange
        String userId = "abc123";
        URL url = new URI("http://localhost:4200").toURL();
        PresignedPutObjectRequest classUnderTestSpy = Mockito.spy(PresignedPutObjectRequest.builder()
            .expiration(Instant.now())
            .isBrowserExecutable(false)
            .signedHeaders(Collections.singletonMap("username1", new ArrayList<>()))
            .httpRequest(SdkHttpRequest.builder()
                .protocol("http")
                .host("localhost")
                .method(SdkHttpMethod.GET)
                .build())
            .build());
        when(classUnderTestSpy.url()).thenReturn(url);
        when(s3Presigner.presignPutObject(Mockito.any(PutObjectPresignRequest.class))).thenReturn(classUnderTestSpy);

        // Act
        URL result = documentService.generateUrl(userId, Folder.LEASE, "test.txt");

        // Assert
        assertNotNull(result);
        assertEquals(url, result);
    }
}
