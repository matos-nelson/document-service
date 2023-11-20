package org.rent.circle.document.api.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import java.net.URL;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.dto.FormData;
import org.rent.circle.document.api.enums.Folder;
import org.rent.circle.document.api.service.DocumentService;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@AllArgsConstructor
@Path("/document")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class DocumentResource {

    private final DocumentService documentService;

    @PUT
    @Path("upload/folder/{folder}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@NotNull Folder folder, @Valid FormData formData) {
        PutObjectResponse response = this.documentService.upload(folder, formData);
        if (response == null) {
            return Response.serverError().build();
        }

        return Response.ok().status(Status.CREATED).build();
    }

    @GET
    @Path("folder/{folder}/file/{file}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@NotNull Folder folder, @NotBlank String file) {
        ResponseBytes<GetObjectResponse> objectBytes = documentService.download(folder, file);
        ResponseBuilder response = Response.ok(objectBytes.asUtf8String());
        response.header("Content-Disposition", "attachment;filename=" + file);
        response.header("Content-Type", objectBytes.response().contentType());
        return response.build();
    }

    @GET
    @Path("upload/url/folder/{folder}/file/{file}")
    public URL generateUploadUrl(@NotNull Folder folder, @NotBlank String file) {
        return documentService.generateUrl(folder, file);
    }

    @GET
    @Path("owner/{id}")
    public List<FileObject> listFiles(@NotNull @PathParam("id") Long id) {
        return documentService.getFileListing(id);
    }
}
