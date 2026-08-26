package org.example.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.orderservice.Entity.Item;
import org.example.orderservice.Repository.IItemRepository;
import org.example.orderservice.dto.OrderItemDto;
import org.example.orderservice.dto.OrderRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class OrderFlowIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private IItemRepository itemRepository;

  private Long dynamicItemId;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();

    Item testItem = new Item();
    testItem.setName("Test Product");
    testItem.setPrice(BigDecimal.valueOf(100.0));

    Item saved = itemRepository.save(testItem);
    this.dynamicItemId = saved.getId();
  }

  @Test
  void shouldCreateOrderSuccessfully() throws Exception {
    String email = "test@example.com";

    stubFor(get(urlPathMatching("/api/users/email/.*"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":1, \"name\":\"Ivan\",\"email\":\"test@example.com\"}")));

    OrderRequestDto request = new OrderRequestDto();
    request.setUserEmail(email);

    request.setItems(List.of(new OrderItemDto(dynamicItemId, 2)));

    mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("NEW"))
            .andExpect(jsonPath("$.user.email").value(email));
  }
}