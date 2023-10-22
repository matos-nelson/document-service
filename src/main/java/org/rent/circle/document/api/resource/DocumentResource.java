package org.rent.circle.document.api.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rent.circle.document.api.dto.FileObject;
import org.rent.circle.document.api.service.DocumentService;

@AllArgsConstructor
@Path("/document")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class DocumentResource {

    private final DocumentService documentService;

    @GET
    public List<FileObject> listFiles() {
        return documentService.getFileListing();
    }
}
