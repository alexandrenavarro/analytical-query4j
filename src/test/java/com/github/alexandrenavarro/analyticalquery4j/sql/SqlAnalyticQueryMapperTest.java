package com.github.alexandrenavarro.analyticalquery4j.sql;


import static org.assertj.core.api.Assertions.assertThat;

import com.github.alexandrenavarro.analyticalquery4j.core.AnalyticalQuery;
import com.github.alexandrenavarro.analyticalquery4j.core.CompositeFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.CompositeFilterOperator;
import com.github.alexandrenavarro.analyticalquery4j.core.Dimension;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValueFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValueFilterOperator;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValuesFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValuesFilterOperator;
import com.github.alexandrenavarro.analyticalquery4j.core.EmptyFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.Measure;
import com.github.alexandrenavarro.analyticalquery4j.core.Order;
import com.github.alexandrenavarro.analyticalquery4j.core.OrderedColumn;
import com.github.alexandrenavarro.analyticalquery4j.description.CubeDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.DimensionDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.FactTableDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.MeasureDescription;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class SqlAnalyticQueryMapperTest {

  // Sales Cube Description
  private final CubeDescription cubeDescription = CubeDescription.builder()
    .measureDescriptions(List.of(
      MeasureDescription.builder()
        .name("amount")
        .columnWithFunction("amount")
        .aggregable(false)
        .build(),
      MeasureDescription.builder()
        .name("sumOfAmount")
        .columnWithFunction("SUM(amount)")
        .aggregable(true)
        .build(),
      MeasureDescription.builder()
        .name("countOfSales")
        .columnWithFunction("COUNT(*)")
        .aggregable(true)
        .build()
    ))
    .dimensionDescriptions(List.of(DimensionDescription.builder()
      .name("model")
      .columnWithFunction("model")
      .build()))
    .factTableDescription(FactTableDescription.builder()
      .factTable("SALES_TABLE")
      .build())
    .build();

  private final SqlAnalyticalQueryMapper sqlAnalyticQueryMapper = new SqlAnalyticalQueryMapper(cubeDescription);

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndNoDimensionAndEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(Collections.emptyList())
      .filter(new EmptyFilter())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount\nFROM SALES_TABLE\nWHERE 1 = 1\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(new EmptyFilter())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE 1 = 1\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndEmptyFilterAndOrderColumnsAndOffsetAndLimit() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(new EmptyFilter())
      .orderedColumns(List.of(OrderedColumn.builder()
          .name("amount")
          .order(Order.DESC)
          .build(),
        OrderedColumn.builder()
          .name("model")
          .order(Order.ASC)
          .build()))
      .offset(0L)
      .limit(10000L)
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE 1 = 1\nORDER BY amount DESC, model ASC\nOFFSET ?\nLIMIT ?\n");
    assertThat(sqlQuery.getParameters()).hasSize(2);
    assertThat(sqlQuery.getParameters().get(0)).isEqualTo(0L);
    assertThat(sqlQuery.getParameters().get(1)).isEqualTo(10000L);
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndOnDimensionValueFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(DimensionValueFilter.builder()
        .dimension(Dimension.builder()
          .name("model")
          .build())
        .operator(DimensionValueFilterOperator.EQUALS)
        .value("Model one")
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE model = ?\n");
    assertThat(sqlQuery.getParameters()).hasSize(1).contains("Model one");
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndOnDimensionValuesFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(DimensionValuesFilter.builder()
        .dimension(Dimension.builder()
          .name("model")
          .build())
        .operator(DimensionValuesFilterOperator.IN)
        .values(List.of("Model one", "Model two"))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE model IN (?, ?)\n");
    assertThat(sqlQuery.getParameters()).hasSize(2).contains("Model one", "Model two");
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndOnCompositeFilterWithEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(CompositeFilter.builder()
        .operator(CompositeFilterOperator.AND)
        .filters(List.of(new EmptyFilter(), new EmptyFilter()))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE (1 = 1 AND 1 = 1)\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneNonAggregatedMeasureAndOneDimensionAndOneCompositeFilterWithDimensionValuesFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("amount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(CompositeFilter.builder()
        .operator(CompositeFilterOperator.OR)
        .filters(List.of(DimensionValuesFilter.builder()
          .dimension(Dimension.builder()
            .name("model")
            .build())
          .operator(DimensionValuesFilterOperator.IN)
          .values(List.of("Model one", "Model two"))
          .build(), DimensionValuesFilter.builder()
          .dimension(Dimension.builder()
            .name("model")
            .build())
          .operator(DimensionValuesFilterOperator.IN)
          .values(List.of("Model three", "Model four"))
          .build()))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT amount AS amount, model AS model\nFROM SALES_TABLE\nWHERE (model IN (?, ?) OR model IN (?, ?))\n");
    assertThat(sqlQuery.getParameters()).hasSize(4).contains("Model one", "Model two", "Model three", "Model four");
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndNoDimensionAndEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(Collections.emptyList())
      .filter(new EmptyFilter())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo("SELECT SUM(amount) AS sumOfAmount\nFROM SALES_TABLE\nWHERE 1 = 1\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndOneDimensionAndEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(new EmptyFilter())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT SUM(amount) AS sumOfAmount, model AS model\nFROM SALES_TABLE\nWHERE 1 = 1\nGROUP BY model\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndOneDimensionAndOnDimensionValueFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(DimensionValueFilter.builder()
        .dimension(Dimension.builder()
          .name("model")
          .build())
        .operator(DimensionValueFilterOperator.EQUALS)
        .value("Model one")
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT SUM(amount) AS sumOfAmount, model AS model\nFROM SALES_TABLE\nWHERE model = ?\nGROUP BY model\n");
    assertThat(sqlQuery.getParameters()).hasSize(1).contains("Model one");
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndOneDimensionAndOneDimensionValuesFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(DimensionValuesFilter.builder()
        .dimension(Dimension.builder()
          .name("model")
          .build())
        .operator(DimensionValuesFilterOperator.IN)
        .values(List.of("Model one", "Model two"))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT SUM(amount) AS sumOfAmount, model AS model\nFROM SALES_TABLE\nWHERE model IN (?, ?)\nGROUP BY model\n");
    assertThat(sqlQuery.getParameters()).hasSize(2).contains("Model one", "Model two");
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndOneDimensionAndOnCompositeFilterWithEmptyFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(CompositeFilter.builder()
        .operator(CompositeFilterOperator.AND)
        .filters(List.of(new EmptyFilter(), new EmptyFilter()))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT SUM(amount) AS sumOfAmount, model AS model\nFROM SALES_TABLE\nWHERE (1 = 1 AND 1 = 1)\nGROUP BY model\n");
    assertThat(sqlQuery.getParameters()).isEmpty();
  }

  @Test
  void shouldMapSqlQueryWhenOneAggregatedMeasureAndOneDimensionAndOneCompositeFilterWithDimensionValuesFilter() {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      .measures(List.of(Measure.builder()
        .name("sumOfAmount")
        .build()))
      .dimensions(List.of(Dimension.builder()
        .name("model")
        .build()))
      .filter(CompositeFilter.builder()
        .operator(CompositeFilterOperator.OR)
        .filters(List.of(DimensionValuesFilter.builder()
          .dimension(Dimension.builder()
            .name("model")
            .build())
          .operator(DimensionValuesFilterOperator.IN)
          .values(List.of("Model one", "Model two"))
          .build(), DimensionValuesFilter.builder()
          .dimension(Dimension.builder()
            .name("model")
            .build())
          .operator(DimensionValuesFilterOperator.IN)
          .values(List.of("Model three", "Model four"))
          .build()))
        .build())
      .build();

    final SqlQuery sqlQuery = sqlAnalyticQueryMapper.mapToSqlQuery(analyticalQuery);
    assertThat(sqlQuery.getSqlRequest()).isEqualTo(
      "SELECT SUM(amount) AS sumOfAmount, model AS model\nFROM SALES_TABLE\nWHERE (model IN (?, ?) OR model IN (?, ?))\nGROUP BY model\n");
    assertThat(sqlQuery.getParameters()).hasSize(4).contains("Model one", "Model two", "Model three", "Model four");
  }

}
