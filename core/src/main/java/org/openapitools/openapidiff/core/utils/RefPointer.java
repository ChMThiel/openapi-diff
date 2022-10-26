package org.openapitools.openapidiff.core.utils;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefPointer<T> {

  private static final Logger log = LoggerFactory.getLogger(RefPointer.class);
  public static final String BASE_REF = "#/components/";
  private RefType refType;

  public RefPointer(RefType refType) {
    this.refType = refType;
  }

  public T resolveRef(Components components, T t, String ref) {
    if (ref != null) {
      String refName = getRefName(ref);
      String[] split = ref.split("/");
      String usedRefType = split[2];
      if (!Objects.equals(usedRefType, refType.getName())) {
        log.warn("ref " + ref + " is not a " + refType);
      }
      refType =
          Arrays.stream(RefType.values())
              .filter(r -> r.getName().equals(usedRefType))
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException("ref invalid " + usedRefType));
      T result = getMap(components).get(refName);
      if (result == null) {
        throw new IllegalArgumentException(String.format("ref '%s' doesn't exist.", ref));
      }
      if (result.getClass() != t.getClass()) {
        if (result instanceof Schema) {
          Schema schema = (Schema) result;
          if (t instanceof Header) {
            Header th = (Header) t;
            th.set$ref(schema.get$ref());
            return (T) th;
          } else if (t instanceof ApiResponse) {
            ApiResponse th = (ApiResponse) t;
            th.set$ref(schema.get$ref());
            return (T) th;
          }
        }
      }
      return result;
    }
    return t;
  }

  @SuppressWarnings("unchecked")
  private Map<String, T> getMap(Components components) {
    switch (refType) {
      case REQUEST_BODIES:
        return (Map<String, T>) components.getRequestBodies();
      case RESPONSES:
        return (Map<String, T>) components.getResponses();
      case PARAMETERS:
        return (Map<String, T>) components.getParameters();
      case SCHEMAS:
        return (Map<String, T>) components.getSchemas();
      case HEADERS:
        return (Map<String, T>) components.getHeaders();
      case SECURITY_SCHEMES:
        return (Map<String, T>) components.getSecuritySchemes();
      default:
        throw new IllegalArgumentException("Not mapped for refType: " + refType);
    }
  }

  private String getBaseRefForType(String type) {
    return String.format("%s%s/", BASE_REF, type);
  }

  public String getRefName(String ref) {
    if (ref == null) {
      return null;
    }
    if (refType == RefType.SECURITY_SCHEMES) {
      return ref;
    }

    //    final String baseRef = getBaseRefForType(refType.getName());
    //    if (!ref.startsWith(baseRef)) {
    //      throw new IllegalArgumentException("Invalid ref: " + ref);
    //    }
    //    return ref.substring(baseRef.length());
    return ref.substring(ref.lastIndexOf("/") + 1);
  }
}
