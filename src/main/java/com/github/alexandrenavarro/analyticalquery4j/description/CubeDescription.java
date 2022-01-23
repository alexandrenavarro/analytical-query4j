package com.github.alexandrenavarro.analyticalquery4j.description;

import com.github.alexandrenavarro.analyticalquery4j.core.Measure;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public final class CubeDescription {

  @NotEmpty
  private final List<MeasureDescription> measureDescriptions;

  @NotEmpty
  private final List<DimensionDescription> dimensionDescriptions;

  @NotEmpty
  private final FactTableDescription factTableDescription;

  public List<Measure> getAggregatedMeasureDescriptions() {
    return measureDescriptions
      .stream()
      .filter(MeasureDescription::isAggregable)
      .map(e -> Measure.builder()
        .name(e.getName())
        .build())
      .collect(Collectors.toList());
  }

  public Optional<MeasureDescription> getMeasureDescriptionByName(final String name) {
    return measureDescriptions.stream()
      .filter(e -> e.getName().equals(name))
      .findFirst();
  }

  public Optional<DimensionDescription> getDimensionDescriptionByName(final String name) {
    return dimensionDescriptions.stream()
      .filter(e -> e.getName()
        .equals(name)).findFirst();
  }

}
