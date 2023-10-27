package org.rent.circle.document.api.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.rent.circle.document.api.S3TestResource;

@QuarkusTest
@TestHTTPEndpoint(DocumentResource.class)
@QuarkusTestResource(S3TestResource.class)
public class DocumentResourceTest {

    @Test
    public void GET_WhenCalled_ShouldReturnFileListing() {
        // Arrange
        long ownerId = 123L;

        // Act
        // Assert
        given()
            .contentType("application/json")
            .when()
            .get("/owner/" + ownerId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .body("[0].key", is("123/file.txt"),
                "[0].directory", is("123/"),
                "[0].fileName", is("file.txt"),
                "[0].size", is(11));
    }
}
