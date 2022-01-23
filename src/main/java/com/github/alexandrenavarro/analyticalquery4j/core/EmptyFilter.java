package com.github.alexandrenavarro.analyticalquery4j.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Getter;

@Getter
@ApiModel(value = "Empty Filter", description = "Empty Filter", parent = AbstractFilter.class)
public final class EmptyFilter extends AbstractFilter {

  public EmptyFilter() {
    super(AbstractFilterType.EMPTY_FILTER);
  }

  @Override
  @JsonIgnore
  public final boolean isEmpty() {
    return true;
  }

}
