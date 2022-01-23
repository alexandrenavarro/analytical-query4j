package com.github.alexandrenavarro.analyticalquery4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexandrenavarro.analyticalquery4j.AnalyticalQuerySampleApp.Sales;
import com.github.alexandrenavarro.analyticalquery4j.core.AnalyticalQuery;
import com.github.alexandrenavarro.analyticalquery4j.core.Dimension;
import com.github.alexandrenavarro.analyticalquery4j.core.Measure;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AnalyticalQuerySampleAppITTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldReturnA2xxStatus() throws Exception {
    final AnalyticalQuery analyticalQuery = AnalyticalQuery.builder()
      //            .measures(List.of(Measure.builder()
      //              .name("amount")
      //              .build()))
      .measures(List.of(
        Measure.builder()
          .name("sumOfAmount")
          .build(),
        Measure.builder()
          .name("countOfSales")
          .build()
      ))
      .dimensions(List.of(
        Dimension.builder()
          .name("model")
          .build()
//        , Dimension.builder()
//          .name("salesDate")
//          .build())
      ))
      //      .dimensions(List.of())
      .build();
    String requestBody = objectMapper.writeValueAsString(analyticalQuery);
    final MvcResult mvcResult = this.mockMvc.perform(post("/sales/search")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn();

    final List<Sales> sales = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<List<Sales>>(){});
    assertThat(sales).hasSize(2);
    assertThat(sales.get(0).getSumOfAmount()).isEqualTo(BigDecimal.valueOf(60000));
    assertThat(sales.get(0).getCountOfSales()).isEqualTo(BigDecimal.valueOf(2));
    assertThat(sales.get(0).getModel()).isEqualTo("Model1");

    assertThat(sales.get(1).getSumOfAmount()).isEqualTo(BigDecimal.valueOf(20000));
    assertThat(sales.get(1).getCountOfSales()).isEqualTo(BigDecimal.valueOf(1));
    assertThat(sales.get(1).getModel()).isEqualTo("Model2");


  }


}
