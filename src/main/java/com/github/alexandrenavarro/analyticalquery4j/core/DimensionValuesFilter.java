package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
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
@ApiModel(value = "Dimension Values Filter", description = "Dimension Values Filter", parent = AbstractFilter.class)
public final class DimensionValuesFilter extends AbstractFilter {

  @NotNull
  @ApiModelProperty(value = "Dimension", required = true)
  private final Dimension dimension;

  @NotNull
  @ApiModelProperty(value = "Operator", required = true)
  private final DimensionValuesFilterOperator operator;

  @ApiModelProperty(value = "Values", required = true)
  @NotEmpty
  private final List<String> values;

  @Builder
  public DimensionValuesFilter(Dimension dimension, DimensionValuesFilterOperator operator, List<String> values) {
    super(AbstractFilterType.DIMENSION_VALUES_FILTER);
    this.dimension = dimension;
    this.operator = operator;
    this.values = values;
  }

}
