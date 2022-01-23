package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "Dimension Values Filter Operator", description = "Dimension Values Filter Operator")
public enum DimensionValuesFilterOperator {
  IN, NOT_IN;
}
