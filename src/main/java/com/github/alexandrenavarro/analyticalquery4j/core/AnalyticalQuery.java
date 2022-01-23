package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@Builder
@Jacksonized
@ApiModel(value = "Analytical Query", description = "Analytical Query")
public final class AnalyticalQuery {

  // TODO validate at least one element of the 2 lists, all must aggregated elements on not

  @ApiModelProperty(value = "List of Measures", required = true)
  @NotNull
  private final List<Measure> measures;

  @ApiModelProperty(value = "List of Dimensions", required = true)
  @NotNull
  private final List<Dimension> dimensions;

  @ApiModelProperty(value = "Filter")
  @Valid
  private AbstractFilter filter;

  @ApiModelProperty(value = "List of Ordered Column (a measure or a dimension with an order)")
  private List<OrderedColumn> orderedColumns;

  @ApiModelProperty(value = "Offset (Offset of your results)")
  private Long offset;

  @ApiModelProperty(value = "Limit (Maximum of results)")
  private Long limit;

}
