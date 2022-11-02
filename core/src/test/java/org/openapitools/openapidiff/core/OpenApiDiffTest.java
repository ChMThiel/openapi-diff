package org.openapitools.openapidiff.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openapitools.openapidiff.core.TestUtils.assertOpenApiAreEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;
import org.openapitools.openapidiff.core.model.ChangedOperation;
import org.openapitools.openapidiff.core.model.Endpoint;
import org.openapitools.openapidiff.core.output.HtmlRender;
import org.openapitools.openapidiff.core.output.JsonRender;
import org.openapitools.openapidiff.core.output.Markdown2HtmlRender;
import org.openapitools.openapidiff.core.output.MarkdownRender;
import org.openapitools.openapidiff.core.output.YamlRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiDiffTest {

  private final String OPENAPI_DOC1 = "petstore_v2_1.yaml";
  private final String OPENAPI_DOC2 = "petstore_v2_2.yaml";
  private final String OPENAPI_EMPTY_DOC = "petstore_v2_empty.yaml";
  private static final Logger log = LoggerFactory.getLogger(OpenApiDiffTest.class);

  @Test
  public void shouldCompareSmom4_1_andRC5() throws IOException {
    OpenApiCompare.Configuration configuration =
        new OpenApiCompare.Configuration(true, true, true, ".*/q/.*");
    ChangedOpenApi changedOpenApi =
        OpenApiCompare.fromLocations("openapi_4_1_org.yaml", "openapi_rc5_org.yaml", configuration);
    //            OpenApiCompare.fromLocations("openapi_rc5_org.yaml", "openapi_rc5_org.yaml");
    //    assertThat(changedOpenApi.getChangedElements()).isNotEmpty();
    String md2Html = new Markdown2HtmlRender().render(changedOpenApi);
    final Path pathMd2Html = Path.of("smom_md_delta.html");
    try (FileWriter fw = new FileWriter(pathMd2Html.toFile())) {
      log.info("Writing to file " + pathMd2Html);
      fw.write(md2Html);
    }
    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path pathHtml = Path.of("smom_delta.html");
    try (FileWriter fw = new FileWriter(pathHtml.toFile())) {
      log.info("Writing to file " + pathHtml);
      fw.write(html);
    }
    String render =
        new MarkdownRender()
            //            .setShowChangedMetadata(true)
            .render(changedOpenApi);
    final Path pathMd = Path.of("smom_delta.md");
    try (FileWriter fw = new FileWriter(pathMd.toFile())) {
      fw.write(render);
    }
    // TODO throw OOM
    //    new JsonRender().renderToFile(changedOpenApi, "smom_delta.json");
    // TODO leads to > 10GB files...
    new YamlRender().renderToFile(changedOpenApi, "smom_delta.yaml");
  }

  @Test
  public void testEqual() {
    assertOpenApiAreEquals(OPENAPI_DOC2, OPENAPI_DOC2);
  }

  @Test
  public void testNewApi(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_EMPTY_DOC, OPENAPI_DOC2);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(newEndpoints).isNotEmpty();
    assertThat(missingEndpoints).isEmpty();
    assertThat(changedEndPoints).isEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testNewApi.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDeprecatedApi(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_EMPTY_DOC);
    List<Endpoint> newEndpoints = changedOpenApi.getNewEndpoints();
    List<Endpoint> missingEndpoints = changedOpenApi.getMissingEndpoints();
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(newEndpoints).isEmpty();
    assertThat(missingEndpoints).isNotEmpty();
    assertThat(changedEndPoints).isEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testDeprecatedApi.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiff(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    List<ChangedOperation> changedEndPoints = changedOpenApi.getChangedOperations();
    assertThat(changedEndPoints).isNotEmpty();

    String html =
        new HtmlRender("Changelog", "http://deepoove.com/swagger-diff/stylesheets/demo.css")
            .render(changedOpenApi);
    final Path path = tempDir.resolve("testDiff.html");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(html);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiffAndMarkdown(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new MarkdownRender().render(diff);
    final Path path = tempDir.resolve("testDiff.md");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(render);
    }
    assertThat(path).isNotEmptyFile();
  }

  @Test
  public void testDiffAndJson(@TempDir Path tempDir) throws IOException {
    ChangedOpenApi diff = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
    String render = new JsonRender().render(diff);
    final Path path = tempDir.resolve("testDiff.json");
    try (FileWriter fw = new FileWriter(path.toFile())) {
      fw.write(render);
    }
    assertThat(path).isNotEmptyFile();
  }
}
