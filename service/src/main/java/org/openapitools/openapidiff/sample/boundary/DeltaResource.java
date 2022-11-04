package org.openapitools.openapidiff.sample.boundary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.output.JsonRender;
import org.openapitools.openapidiff.core.output.Markdown2HtmlRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;
import org.openapitools.openapidiff.core.output.YamlRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Path("/resources/delta")
public class DeltaResource {

  private static final Logger log = LoggerFactory.getLogger(DeltaResource.class);

  // TODO openapi
  @Operation(summary = "get difference between two openApi-files")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @RequestBody(
      content = {
        @Content(
            mediaType = javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA,
            schema = @Schema(implementation = DeltaInput.class))
      })
  public Response find(@MultipartForm DeltaInput aInput) throws IOException {
    String oldFileContent = Files.readString(aInput.oldFile.toPath());
    String newFileContent = Files.readString(aInput.newFile.toPath());
    ChangedOpenApi result =
        OpenApiCompare.fromContents(
            oldFileContent,
            newFileContent,
            null,
            new OpenApiCompare.Configuration(
                "on".equals(aInput.ignoreSecurity),
                "on".equals(aInput.ignoreDescriptions),
                "on".equals(aInput.ignoreResponseHeader),
                aInput.filterPath));
    return switch (aInput.format) {
      case "html" -> Response.ok(new Markdown2HtmlRender().render(result), MediaType.TEXT_HTML)
          .build();
      case "markdown" -> Response.ok(new MarkdownRender().render(result), MediaType.TEXT_PLAIN)
          .build();
      case "yaml" -> Response.ok(new YamlRender().render(result), MediaType.TEXT_PLAIN).build();
      case "json" -> Response.ok(new JsonRender().render(result), MediaType.APPLICATION_JSON)
          .build();
      default -> Response.ok(new Markdown2HtmlRender().render(result), MediaType.TEXT_HTML).build();
    };
  }

  public static class DeltaInput {

    @Schema(
        type = SchemaType.STRING,
        format = "binary",
        description = "the older version of an openApi-file")
    @FormParam("oldFile")
    public File oldFile;

    @Schema(
        type = SchemaType.STRING,
        format = "binary",
        description = "the new version of an openApi-file")
    @FormParam("newFile")
    public File newFile;

    @Schema(
        description = "ignore all PATHs in the openApiFile matching given regular expression",
        defaultValue = ".*/q/.*")
    @FormParam("filterPath")
    String filterPath;

    @Schema(
        description = "ignore all description in delta-evaluation",
        enumeration = {"on", "off"},
        required = true,
        defaultValue = "on")
    @FormParam("ignoreDescriptions")
    String ignoreDescriptions;

    @Schema(
        description = "ignore all response header in delta-evaluation",
        enumeration = {"on", "off"},
        required = true,
        defaultValue = "on")
    @FormParam("ignoreResponseHeader")
    String ignoreResponseHeader;

    @Schema(
        description = "ignore all security-infos in delta-evaluation",
        enumeration = {"on", "off"},
        required = true,
        defaultValue = "on")
    @FormParam("ignoreSecurity")
    String ignoreSecurity;

    @Schema(
        description = "output format",
        enumeration = {"html", "markdown", "json", "yaml"},
        required = true,
        defaultValue = "html")
    @FormParam("format")
    @DefaultValue("html")
    String format;

    @Override
    public String toString() {
      return "DeltaInput{"
          + "oldFile="
          + oldFile
          + ", "
          + "newFile="
          + newFile
          + ", "
          + "filterPath="
          + filterPath
          + ", "
          + "ignoreDescriptions="
          + ignoreDescriptions
          + ", "
          + "ignoreResponseHeader="
          + ignoreResponseHeader
          + ", "
          + "ignoreSecurity="
          + ignoreSecurity
          + ", "
          + "format="
          + format
          + '}';
    }
  }
}
