package org.openapitools.openapidiff.sample.boundary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.Markdown2HtmlRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Path("/resources/delta/html")
public class DeltaResource {

    private static final Logger log = LoggerFactory.getLogger(DeltaResource.class);

    //TODO openapi
    @Operation(summary = "get difference between two openApi-files")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response find(@MultipartForm DeltaInput aInput)
            throws IOException {
        String oldFileContent = Files.readString(aInput.oldFile.toPath());
        String newFileContent = Files.readString(aInput.newFile.toPath());
        ChangedOpenApi result
                = OpenApiCompare.fromContents(
                        oldFileContent,
                        newFileContent,
                        null,
                        new OpenApiCompare.Configuration(
                                "on".equals(aInput.ignoreSecurity),
                                "on".equals(aInput.ignoreDescriptions),
                                "on".equals(aInput.ignoreResponseHeader),
                                aInput.filterPath));
        String html = new Markdown2HtmlRender().render(result);
        return Response.ok(html, MediaType.TEXT_HTML).build();
    }

    public static class DeltaInput {

        @FormParam("oldFile")
        public File oldFile;
        @FormParam("newFile")
        public File newFile;
        @FormParam("filterPath")
        String filterPath;
        @FormParam("ignoreDescriptions")
        String ignoreDescriptions;
        @FormParam("ignoreResponseHeader")
        String ignoreResponseHeader;
        @FormParam("ignoreSecurity")
        String ignoreSecurity;

    }
}
