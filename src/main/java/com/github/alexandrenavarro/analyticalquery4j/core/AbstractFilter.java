package com.github.alexandrenavarro.analyticalquery4j.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @Type(value = CompositeFilter.class, name = "COMPOSITE_FILTER"),
  @Type(value = DimensionValueFilter.class, name = "DIMENSION_VALUE_FILTER"),
  @Type(value = DimensionValuesFilter.class, name = "DIMENSION_VALUES_FILTER"),
  @Type(value = EmptyFilter.class, name = "EMPTY_FILTER")
})
@ApiModel(value = "AbstractFilter", description = "AbstractFilter", discriminator = "filterType", subTypes = {CompositeFilter.class, DimensionValueFilter.class, DimensionValuesFilter.class, EmptyFilter.class})
public abstract class AbstractFilter {

  @NotNull
  @ApiModelProperty(value = "Type", required = true)
  private final AbstractFilterType type;

  public boolean isEmpty() {
    return false;
  }

}
