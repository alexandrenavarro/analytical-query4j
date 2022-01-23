package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@EqualsAndHashCode
@ToString
@Builder // FIXME Not normal, need for serialization, to investigate
@Jacksonized
@ApiModel(value = "Dimension Value Filter", description = "Dimension Value Filter", parent = AbstractFilter.class)
public final class DimensionValueFilter extends AbstractFilter {

  @NotNull
  @ApiModelProperty(value = "Dimension", required = true)
  private final Dimension dimension;

  @NotNull
  @ApiModelProperty(value = "Operator", required = true)
  private final DimensionValueFilterOperator operator;

  @NotEmpty
  @ApiModelProperty(value = "Value", required = true)
  private final String value;

  @Builder
  public DimensionValueFilter(Dimension dimension, DimensionValueFilterOperator operator, String value) {
    super(AbstractFilterType.DIMENSION_VALUE_FILTER);
    this.dimension = dimension;
    this.operator = operator;
    this.value = value;
  }
}
