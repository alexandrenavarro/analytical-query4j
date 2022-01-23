package com.github.alexandrenavarro.analyticalquery4j.description;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public final class DimensionDescription {

  @NotEmpty
  private final String name;

  @NotEmpty
  private final String columnWithFunction;

}
