package com.github.alexandrenavarro.analyticalquery4j.core;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Order", description = "Order (ASC for Ascendant and DESC for Descendant)")
public enum Order {
  ASC,
  DESC
}
