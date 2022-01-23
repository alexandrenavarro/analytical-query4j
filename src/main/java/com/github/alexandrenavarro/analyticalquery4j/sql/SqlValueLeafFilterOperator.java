package com.github.alexandrenavarro.analyticalquery4j.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum SqlValueLeafFilterOperator {
  EQUALS("="), NOT_EQUALS("<>"), LESS_THAN("<"), LESS_THAN_EQUAL("<="), GREATER_THAN(">"), GREAT_THAN_EQUAL(">="),  LIKE("LIKE"), NOT_LIKE ("NOT LIKE");
  private final String sqlOperator;
}
