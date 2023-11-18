package org.rent.circle.document.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.dto.FormData;
import org.rent.circle.document.api.enums.Folder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ApplicationScoped
public class DocumentService {

    @Inject
    S3Client s3Client;

    @ConfigProperty(name = "document.bucket.name")
    private String bucketName;

    public List<FileObject> getFileListing(Long ownerId) {
        String folder = bucketName;

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
            .bucket(folder)
            .prefix(ownerId.toString())
            .build();

        return s3Client.listObjects(listRequest).contents().stream()
            .map(item -> new FileObject(item.key(), item.size()))
            .sorted(Comparator.comparing(FileObject::getDirectory))
            .collect(Collectors.toList());
    }

    public PutObjectResponse upload(Folder folder, FormData formData) {

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(folder.value + "/" + formData.getFilename())
            .contentType(formData.getMimetype())
            .build();

        return s3Client.putObject(request, RequestBody.fromFile(formData.getData()));
    }

    public ResponseBytes<GetObjectResponse> download(Folder folder, String file) {

        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(folder.value + "/" + file)
            .build();
        return s3Client.getObjectAsBytes(request);
    }
}
