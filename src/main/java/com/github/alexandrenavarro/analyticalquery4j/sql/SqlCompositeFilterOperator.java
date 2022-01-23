package com.github.alexandrenavarro.analyticalquery4j.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum SqlCompositeFilterOperator {
  AND("AND"), OR("OR");

  private final String sqlOperator;
}
