package org.openapitools.openapidiff.core.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import java.io.IOException;
import java.nio.file.Paths;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class YamlRender implements Render {
  private final ObjectMapper objectMapper;

  public YamlRender() {
      objectMapper = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER));
      objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
      objectMapper.findAndRegisterModules();
  }

  @Override
  public String render(ChangedOpenApi diff) {
    try {
      return objectMapper.writeValueAsString(diff);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not serialize diff as JSON", e);
    }
  }

  public void renderToFile(ChangedOpenApi diff, String file) {
    try {
      objectMapper.writeValue(Paths.get(file).toFile(), diff);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not serialize diff as JSON", e);
    } catch (IOException e) {
      throw new RuntimeException("Could not write to JSON file", e);
    }
  }
}
