package org.openapitools.openapidiff.core.model.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.openapitools.openapidiff.core.model.Changed;
import org.openapitools.openapidiff.core.model.DiffContext;
import org.openapitools.openapidiff.core.model.DiffResult;

public final class ChangedMaxLength implements Changed {
  @JsonIgnore private final Integer oldValue;
  @JsonIgnore private final Integer newValue;
  @JsonIgnore private final DiffContext context;

  @Override
  public DiffResult isChanged() {
    if (Objects.equals(oldValue, newValue)) {
      return DiffResult.NO_CHANGES;
    }
    if (context.isRequest() && (newValue == null || oldValue != null && oldValue <= newValue)
        || context.isResponse() && (oldValue == null || newValue != null && newValue <= oldValue)) {
      return DiffResult.COMPATIBLE;
    }
    return DiffResult.INCOMPATIBLE;
  }

  public ChangedMaxLength(
      final Integer oldValue, final Integer newValue, final DiffContext context) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.context = context;
  }

  public Integer getOldValue() {
    return this.oldValue;
  }

  public Integer getNewValue() {
    return this.newValue;
  }

  public DiffContext getContext() {
    return this.context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangedMaxLength that = (ChangedMaxLength) o;
    return Objects.equals(oldValue, that.oldValue)
        && Objects.equals(newValue, that.newValue)
        && Objects.equals(context, that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldValue, newValue, context);
  }

  @java.lang.Override
  public java.lang.String toString() {
    return "ChangedMaxLength(oldValue="
        + this.getOldValue()
        + ", newValue="
        + this.getNewValue()
        + ", context="
        + this.getContext()
        + ")";
  }
}
