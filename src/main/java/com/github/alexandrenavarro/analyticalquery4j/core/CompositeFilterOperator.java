package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Composite Filter Operator", description = "Composite Filter Operator (AND or OR)")
public enum CompositeFilterOperator {
  AND, OR;
}
