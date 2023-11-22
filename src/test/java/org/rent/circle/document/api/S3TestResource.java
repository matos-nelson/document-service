package org.rent.circle.document.api;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.rent.circle.document.api.enums.Folder;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.EnabledService;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3TestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName LOCALSTACK_IMAGE_NAME = DockerImageName.parse("localstack/localstack");
    private LocalStackContainer container;

    @Override
    public Map<String, String> start() {

        DockerClientFactory.instance().client();

        try {

            container = new LocalStackContainer(LOCALSTACK_IMAGE_NAME).withServices(Service.S3);
            container.start();

            URI endpointOverride = container.getEndpointOverride(EnabledService.named(Service.S3.getName()));

            StaticCredentialsProvider staticCredentials = StaticCredentialsProvider
                .create(AwsBasicCredentials.create("test-key", "test-secret"));

            String bucketName = "test";

            try (S3Client client = S3Client.builder()
                .endpointOverride(endpointOverride)
                .credentialsProvider(staticCredentials)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(Region.US_EAST_1).build()) {

                CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

                client.createBucket(bucketRequest);

                PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("test_user/" + Folder.LEASE.value + "/file.txt")
                    .build();

                PutObjectRequest folderObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("test_user/")
                    .build();

                client.putObject(objectRequest, RequestBody.fromString("File Content"));
                client.putObject(folderObjectRequest, RequestBody.fromString(""));
            }

            Map<String, String> properties = new HashMap<>();
            properties.put("quarkus.s3.endpoint-override", endpointOverride.toString());
            properties.put("document.bucket.name", bucketName);

            return properties;
        } catch (Exception e) {
            throw new RuntimeException("Could not start localstack server", e);
        }
    }

    @Override
    public void stop() {
        if (container != null) {
            container.close();
        }
    }
}