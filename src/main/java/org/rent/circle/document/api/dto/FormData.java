package org.rent.circle.document.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class FormData {

    @RestForm("file")
    private File data;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    @NotEmpty
    private String filename;

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    @NotEmpty
    private String mimetype;
}
