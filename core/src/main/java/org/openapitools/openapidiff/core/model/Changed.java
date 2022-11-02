package org.openapitools.openapidiff.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;

public interface Changed {
  static DiffResult result(Changed changed) {
    return Optional.ofNullable(changed).map(Changed::isChanged).orElse(DiffResult.NO_CHANGES);
  }

  DiffResult isChanged();

  @JsonIgnore
  default boolean isCompatible() {
    return isChanged().isCompatible();
  }

  @JsonIgnore
  default boolean isIncompatible() {
    return isChanged().isIncompatible();
  }

  @JsonIgnore
  default boolean isUnchanged() {
    return isChanged().isUnchanged();
  }

  @JsonIgnore
  default boolean isDifferent() {
    return isChanged().isDifferent();
  }
}
