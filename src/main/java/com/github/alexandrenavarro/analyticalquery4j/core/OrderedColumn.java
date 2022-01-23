package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@EqualsAndHashCode
@ToString
@Builder
@Jacksonized
@ApiModel(value = "Ordered Column", description = "Ordered Column (a measure or a dimension with an order")
public class OrderedColumn {

  @ApiModelProperty(value = "Name of the column", required = true)
  @NotEmpty
  private final String name;

  @ApiModelProperty(value = "Order (DESC for descendant or ASC for ascendant)", required = true)
  private final Order order;

}


