package org.openapitools.openapidiff.core;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.io.File;
import java.util.List;
import org.openapitools.openapidiff.core.compare.OpenApiDiff;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class OpenApiCompare {

  private static final OpenAPIParser PARSER = new OpenAPIParser();
  private static final ParseOptions OPTIONS = new ParseOptions();

  static {
    OPTIONS.setResolve(true);
  }

  private OpenApiCompare() {}

  /**
   * compare two openapi doc
   *
   * @param oldContent old api-doc location:Json or Http
   * @param newContent new api-doc location:Json or Http
   * @return Comparison result
   */
  public static ChangedOpenApi fromContents(String oldContent, String newContent) {
    return fromContents(oldContent, newContent, null, new Configuration());
  }

  public static ChangedOpenApi fromContents(
      String oldContent, String newContent, Configuration aConfiguration) {
    return fromContents(oldContent, newContent, null, aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldContent old api-doc location:Json or Http
   * @param newContent new api-doc location:Json or Http
   * @param auths
   * @return Comparison result
   */
  public static ChangedOpenApi fromContents(
      String oldContent, String newContent, List<AuthorizationValue> auths) {
    return fromSpecifications(readContent(oldContent, auths), readContent(newContent, auths));
  }

  public static ChangedOpenApi fromContents(
      String oldContent,
      String newContent,
      List<AuthorizationValue> auths,
      Configuration aConfiguration) {
    return fromSpecifications(
        readContent(oldContent, auths), readContent(newContent, auths), aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldFile old api-doc file
   * @param newFile new api-doc file
   * @return Comparison result
   */
  public static ChangedOpenApi fromFiles(File oldFile, File newFile) {
    return fromFiles(oldFile, newFile, null, new Configuration());
  }

  public static ChangedOpenApi fromFiles(File oldFile, File newFile, Configuration aConfiguration) {
    return fromFiles(oldFile, newFile, null, aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldFile old api-doc file
   * @param newFile new api-doc file
   * @param auths
   * @return Comparison result
   */
  public static ChangedOpenApi fromFiles(
      File oldFile, File newFile, List<AuthorizationValue> auths) {
    return fromLocations(oldFile.getAbsolutePath(), newFile.getAbsolutePath(), auths);
  }

  public static ChangedOpenApi fromFiles(
      File oldFile, File newFile, List<AuthorizationValue> auths, Configuration aConfiguration) {
    return fromLocations(
        oldFile.getAbsolutePath(), newFile.getAbsolutePath(), auths, aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldLocation old api-doc location (local or http)
   * @param newLocation new api-doc location (local or http)
   * @return Comparison result
   */
  public static ChangedOpenApi fromLocations(String oldLocation, String newLocation) {
    return fromLocations(oldLocation, newLocation, null, new Configuration());
  }

  public static ChangedOpenApi fromLocations(
      String oldLocation, String newLocation, Configuration aConfiguration) {
    return fromLocations(oldLocation, newLocation, null, aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldLocation old api-doc location (local or http)
   * @param newLocation new api-doc location (local or http)
   * @param auths
   * @return Comparison result
   */
  public static ChangedOpenApi fromLocations(
      String oldLocation, String newLocation, List<AuthorizationValue> auths) {
    return fromSpecifications(readLocation(oldLocation, auths), readLocation(newLocation, auths));
  }

  public static ChangedOpenApi fromLocations(
      String oldLocation,
      String newLocation,
      List<AuthorizationValue> auths,
      Configuration aConfiguration) {
    return fromSpecifications(
        readLocation(oldLocation, auths), readLocation(newLocation, auths), aConfiguration);
  }

  /**
   * compare two openapi doc
   *
   * @param oldSpec old api-doc specification
   * @param newSpec new api-doc specification
   * @return Comparison result
   */
  public static ChangedOpenApi fromSpecifications(OpenAPI oldSpec, OpenAPI newSpec) {
    return OpenApiDiff.compare(
        notNull(oldSpec, "old"), notNull(newSpec, "new"), new Configuration());
  }

  public static ChangedOpenApi fromSpecifications(
      OpenAPI oldSpec, OpenAPI newSpec, Configuration aConfiguration) {
    return OpenApiDiff.compare(notNull(oldSpec, "old"), notNull(newSpec, "new"), aConfiguration);
  }

  private static OpenAPI notNull(OpenAPI spec, String type) {
    if (spec == null) {
      throw new RuntimeException(String.format("Cannot read %s OpenAPI spec", type));
    }
    return spec;
  }

  private static OpenAPI readContent(String content, List<AuthorizationValue> auths) {
    return PARSER.readContents(content, auths, OPTIONS).getOpenAPI();
  }

  private static OpenAPI readLocation(String location, List<AuthorizationValue> auths) {
    return PARSER.readLocation(location, auths, OPTIONS).getOpenAPI();
  }

  public record Configuration(
      boolean ignoreSecurity,
      boolean ignoreDescription,
      boolean ignoreResponseHeader,
      String ignorePathRegex) {

    public Configuration() {
      this(false, false, false, null);
    }

    public boolean isMatchingPathRegex(String aPath) {
      return ignorePathRegex == null || !aPath.matches(ignorePathRegex);
    }
  }
}
