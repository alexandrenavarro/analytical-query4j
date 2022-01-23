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
@ApiModel(value = "Dimension", description = "Dimension (a String)")
public final class Dimension {

  @ApiModelProperty(value = "Name of the measure", required = true)
  @NotEmpty
  private final String name;
}
