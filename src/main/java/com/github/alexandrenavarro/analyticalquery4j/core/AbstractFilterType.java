package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "AbstractFilterType", description = "AbstractFilterType")
public enum AbstractFilterType {
  COMPOSITE_FILTER,
  EMPTY_FILTER,
  DIMENSION_VALUE_FILTER,
  DIMENSION_VALUES_FILTER;
}
