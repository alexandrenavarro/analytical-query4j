package com.github.alexandrenavarro.analyticalquery4j.sql;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class SqlQuery {

  @NotEmpty
  private String sqlRequest;

  @NotEmpty
  private List<Object> parameters;

}
