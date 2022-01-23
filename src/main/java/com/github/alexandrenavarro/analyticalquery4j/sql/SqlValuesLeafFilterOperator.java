package com.github.alexandrenavarro.analyticalquery4j.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum SqlValuesLeafFilterOperator {
  IN("IN"), NOT_IN("NOT IN");

  private final String sqlOperator;
}
