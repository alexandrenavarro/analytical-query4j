package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ApiModel(value = "Dimension Value Filter Operator", description = "Dimension Value Filter Operator")
public enum DimensionValueFilterOperator {
  EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREAT_THAN_EQUAL, LIKE, NOT_LIKE;
}
