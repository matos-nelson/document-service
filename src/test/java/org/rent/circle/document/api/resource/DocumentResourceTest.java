package org.rent.circle.document.api.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.rent.circle.document.api.S3TestResource;
import org.rent.circle.document.api.annotaton.AuthUser;
import org.rent.circle.document.api.enums.Folder;

@QuarkusTest
@TestHTTPEndpoint(DocumentResource.class)
@QuarkusTestResource(S3TestResource.class)
@AuthUser
public class DocumentResourceTest {


    @Test
    public void GET_WhenCalled_ShouldReturnFileListing() {
        // Arrange

        // Act
        // Assert
        given()
            .contentType("application/json")
            .when()
            .get("list")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("size()", is(1),
                "[0].key", is(Folder.LEASE.value + "/file.txt"),
                "[0].directory", is(Folder.LEASE.value + "/"),
                "[0].fileName", is("file.txt"),
                "[0].size", is(12));
    }

    @Test
    public void PUT_UploadFile_WhenValidationFails_ShouldReturnBadRequest() {
        // Arrange
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
            .fileName("file.txt")
            .mimeType("text/plain")
            .build();

        // Act
        // Assert
        given()
            .multiPart(multiPartSpecification)
            .formParam("filename", "file.txt")
            .when()
            .put("upload/folder/" + Folder.LEASE.value)
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void PUT_UploadFile_WhenCalled_ShouldUploadFile() {
        // Arrange
        MultiPartSpecification multiPartSpecification = new MultiPartSpecBuilder("File content".getBytes())
            .fileName("file.txt")
            .mimeType("text/plain")
            .build();

        // Act
        // Assert
        given()
            .multiPart(multiPartSpecification)
            .formParam("filename", "file.txt")
            .formParam("mimetype", "text/plain")
            .when()
            .put("upload/folder/" + Folder.LEASE.value)
            .then()
            .statusCode(HttpStatus.SC_CREATED);
    }

    @Test
    public void GET_DownloadFile_WhenCalled_ShouldReturnFile() {
        // Arrange
        String fileContent = "File Content";
        String filename = "file.txt";

        // Act
        // Assert
        given()
            .when()
            .get("/folder/" + Folder.LEASE.value + "/file/" + filename)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body(is(fileContent));
    }

    @Test
    public void GET_generateUploadUrl_WhenCalled_ShouldReturnUrl() {
        // Arrange
        String filename = "test.txt";

        // Act
        // Assert
        given()
            .contentType("application/json")
            .when()
            .get("generate/upload/url/folder/" + Folder.LEASE.value + "/file/" + filename)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body(notNullValue());
    }
}
