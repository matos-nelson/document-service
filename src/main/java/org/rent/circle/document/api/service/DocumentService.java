package org.rent.circle.document.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.dto.FormData;
import org.rent.circle.document.api.enums.Folder;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ApplicationScoped
@Slf4j
public class DocumentService {

    @Inject
    S3Client s3Client;

    @Inject
    S3Presigner s3Presigner;

    @ConfigProperty(name = "document.bucket.name")
    private String bucketName;

    public List<FileObject> getFileListing(@NotNull String userId) {

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
            .bucket(bucketName)
            .prefix(userId)
            .build();

        return s3Client.listObjects(listRequest).contents().stream()
            .map(item -> new FileObject(item.key().replace(userId + "/", ""), item.size()))
            .filter(item -> !item.getFileName().isBlank())
            .sorted(Comparator.comparing(FileObject::getDirectory))
            .collect(Collectors.toList());
    }

    public PutObjectResponse upload(@NotNull String userId, Folder folder, FormData formData) {

        String key = userId + "/" + folder.value + "/" + formData.getFilename();
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(formData.getMimetype())
            .build();

        return s3Client.putObject(request, RequestBody.fromFile(formData.getData()));
    }

    public ResponseBytes<GetObjectResponse> download(@NotNull String userId, Folder folder, String file) {

        String key = userId + "/" + folder.value + "/" + file;
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        try {
            return s3Client.getObjectAsBytes(request);
        } catch (AwsServiceException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public URL generateUrl(@NotNull String userId, Folder folder, String filename) {

        String key = userId + "/" + folder.value + "/" + filename;
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain")
            .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(objectRequest)
            .build();

        return s3Presigner.presignPutObject(putObjectPresignRequest).url();
    }
}
