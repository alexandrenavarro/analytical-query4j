package com.github.alexandrenavarro.analyticalquery4j.sql;

import com.github.alexandrenavarro.analyticalquery4j.core.AbstractFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.AnalyticalQuery;
import com.github.alexandrenavarro.analyticalquery4j.core.CompositeFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValueFilter;
import com.github.alexandrenavarro.analyticalquery4j.core.DimensionValuesFilter;
import com.github.alexandrenavarro.analyticalquery4j.description.CubeDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.DimensionDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.MeasureDescription;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class SqlAnalyticalQueryMapper {

  private final CubeDescription cubeDescription;

  public SqlQuery mapToSqlQuery(final AnalyticalQuery analyticalQuery) {

    // TODO validate

    final StringBuilder sqlRequest = new StringBuilder();
    final List<Object> parameters = new ArrayList<>();
    // SELECT
    sqlRequest.append("SELECT ");
    // Measures
    sqlRequest.append(analyticalQuery.getMeasures()
      .stream()
      .map(e -> cubeDescription.getMeasureDescriptionByName(e.getName()))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .map(this::mapMeasureToSqlColumn)
      .collect(Collectors.joining(",")));
    // Dimensions
    if (!analyticalQuery.getDimensions().isEmpty()) {
      sqlRequest.append(", ");
      sqlRequest.append(analyticalQuery.getDimensions()
        .stream()
        .map(e -> cubeDescription.getDimensionDescriptionByName(e.getName()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::mapDimensionToSqlColumn)
        .collect(Collectors.joining(",")));
    }
    sqlRequest.append("\n");

    // FROM
    // Fact table
    sqlRequest.append("FROM ");
    sqlRequest.append(cubeDescription.getFactTableDescription().getFactTable());
    sqlRequest.append("\n");

    // WHERE
    // Filter
    if (analyticalQuery.getFilter() != null) {
      sqlRequest.append("WHERE ");
      final SqlWhereClause sqlWhereClause = mapToWhereClause(analyticalQuery.getFilter(), new SqlWhereClause());
      sqlRequest.append(sqlWhereClause.getSqlRequest());
      parameters.addAll(sqlWhereClause.getParameters());
      sqlRequest.append("\n");
    }

    // GROUP BY
    if (!analyticalQuery.getDimensions().isEmpty() && cubeDescription.getAggregatedMeasureDescriptions()
      .containsAll(analyticalQuery.getMeasures())) {
      sqlRequest.append("GROUP BY ");
      sqlRequest.append(analyticalQuery.getDimensions().stream()
        .map(e -> cubeDescription.getDimensionDescriptionByName(e.getName()))
        .map(Optional::get)
        .filter(Objects::nonNull)
        .map(DimensionDescription::getColumnWithFunction)
        .collect(Collectors.joining(", ")));
      sqlRequest.append("\n");
    }

    // ORDER BY
    if (analyticalQuery.getOrderedColumns() != null && !analyticalQuery.getOrderedColumns().isEmpty()) {
      sqlRequest.append("ORDER BY ");
      sqlRequest.append(analyticalQuery.getOrderedColumns()
        .stream()
        .map(e -> e.getName() + " " + SqlOrder.valueOf(e.getOrder().name()).getSqlOperator())
        .collect(Collectors.joining(", ")));
      sqlRequest.append("\n");
    }

    // OFFSET
    if (analyticalQuery.getOffset() != null) {
      sqlRequest.append("OFFSET ?");
      parameters.add(analyticalQuery.getOffset());
      sqlRequest.append("\n");
    }

    // LIMIT
    if (analyticalQuery.getOffset() != null) {
      sqlRequest.append("LIMIT ?");
      parameters.add(analyticalQuery.getLimit());
      sqlRequest.append("\n");
    }

    return SqlQuery.builder()
      .sqlRequest(sqlRequest.toString())
      .parameters(parameters)
      .build();
  }

  public SqlQuery mapToCountSqlQuery(final AnalyticalQuery analyticalQuery) {
    final SqlQuery sqlQuery = mapToSqlQuery(analyticalQuery);
    return SqlQuery.builder()
      .sqlRequest("SELECT COUNT(*) FROM ( " + sqlQuery + " ) AS view")
      .parameters(sqlQuery.getParameters())
      .build();
  }

  private SqlWhereClause mapToWhereClause(final AbstractFilter filter, final SqlWhereClause sqlWhereClause) {
    if (filter instanceof CompositeFilter) {
      final CompositeFilter compositeFilter = (CompositeFilter) filter;
      final String requestSql = "(" + compositeFilter.getFilters().stream()
        .map(e -> mapToWhereClause(e, sqlWhereClause))
        .map(SqlWhereClause::getSqlRequest)
        .collect(Collectors.joining(" " + SqlCompositeFilterOperator.valueOf(compositeFilter.getOperator().name()).getSqlOperator() + " "))
        + ")";
      sqlWhereClause.setSqlRequest(requestSql);
    } else if (filter instanceof DimensionValueFilter) {
      final DimensionValueFilter dimensionValueFilter = (DimensionValueFilter) filter;
      final String requestSql = cubeDescription.getDimensionDescriptionByName(dimensionValueFilter.getDimension().getName())
        .orElseThrow(() -> new IllegalArgumentException("Incorrect dimension name : " + dimensionValueFilter.getDimension().getName()))
        .getName() + " " + SqlValueLeafFilterOperator.valueOf(dimensionValueFilter.getOperator().name())
        .getSqlOperator() + " ?";
      sqlWhereClause.setSqlRequest(requestSql);
      sqlWhereClause.getParameters().add(dimensionValueFilter.getValue());
    } else if (filter instanceof DimensionValuesFilter) {
      final DimensionValuesFilter dimensionValuesFilter = (DimensionValuesFilter) filter;
      final String requestSql = cubeDescription.getDimensionDescriptionByName(dimensionValuesFilter.getDimension().getName())
        .orElseThrow(() -> new IllegalArgumentException("Incorrect dimension name : " + dimensionValuesFilter.getDimension().getName()))
        .getName() + " " + SqlValuesLeafFilterOperator.valueOf(dimensionValuesFilter.getOperator().name())
        .getSqlOperator() + " (" + String.join(", ", dimensionValuesFilter.getValues().stream().map(e -> "?").collect(Collectors.toList()))
        + ")";
      sqlWhereClause.setSqlRequest(requestSql);
      sqlWhereClause.getParameters().addAll(dimensionValuesFilter.getValues());
    } else {
      sqlWhereClause.setSqlRequest("1 = 1");
    }
    return sqlWhereClause;
  }

  private String mapMeasureToSqlColumn(final MeasureDescription measureDescription) {
    return measureDescription.getColumnWithFunction() + " AS " + measureDescription.getName();
  }

  private String mapDimensionToSqlColumn(final DimensionDescription dimensionDescription) {
    return dimensionDescription.getColumnWithFunction() + " AS " + dimensionDescription.getName();
  }
}
