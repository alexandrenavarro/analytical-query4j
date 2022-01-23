package com.github.alexandrenavarro.analyticalquery4j.core;

import static com.github.alexandrenavarro.analyticalquery4j.core.AbstractFilterType.COMPOSITE_FILTER;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@ApiModel(value = "Composite Filter", description = "Composite Filter", parent = AbstractFilter.class)
public final class CompositeFilter extends AbstractFilter {

  @NotNull
  @ApiModelProperty(value = "Operator", required = true)
  private final CompositeFilterOperator operator;

  @NotNull
  @Size(min = 2)
  @ApiModelProperty(value = "Filters", required = true)
  private final List<AbstractFilter> filters;

  @Builder
  public CompositeFilter(final CompositeFilterOperator operator, final List<AbstractFilter> filters) {
    super(COMPOSITE_FILTER);
    this.operator = operator;
    this.filters = filters;
  }

}
