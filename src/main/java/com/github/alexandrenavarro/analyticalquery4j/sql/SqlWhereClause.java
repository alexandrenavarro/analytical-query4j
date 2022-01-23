package com.github.alexandrenavarro.analyticalquery4j.sql;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
final class SqlWhereClause {

  @Setter
  private String sqlRequest;
  private final List<Object> parameters = new ArrayList<>();
}
