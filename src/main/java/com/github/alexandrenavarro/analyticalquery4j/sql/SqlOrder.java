package com.github.alexandrenavarro.analyticalquery4j.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SqlOrder {
  ASC("ASC"),
  DESC("DESC");

  private final String sqlOperator;
}
