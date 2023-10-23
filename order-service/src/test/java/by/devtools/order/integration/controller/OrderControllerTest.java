package by.devtools.order.integration.controller;

import by.devtools.domain.OrderDto;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.integration.BaseIntegrationTest;
import by.devtools.order.service.OrderService;
import by.devtools.order.service.impl.KafkaProducer;
import by.devtools.order.util.JsonUtil;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    @WithMockUser
    void check_createOrder_should_return_expectedResponse() throws Exception {
        String url = "/api/v1/orders";
        OrderCreate request = TestData.getOrderCreate();
        OrderDto expected = TestData.getOrderDto();

        doReturn(expected)
                .when(orderService)
                .createOrder(request);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.productCount").value(expected.getProductCount()))
                .andExpect(jsonPath("$.customerId").value(expected.getCustomerId()))
                .andExpect(jsonPath("$.productId").value(expected.getProductId()))
                .andExpect(jsonPath("$.totalPrice").value(expected.getTotalPrice()))
                .andExpect(jsonPath("$.orderStatus").value(expected.getOrderStatus()))
                .andExpect(jsonPath("$.paymentStatus").value(expected.getPaymentStatus()))
                .andExpect(jsonPath("$.inventoryStatus").value(expected.getInventoryStatus()));

        verify(orderService).createOrder(TestData.getOrderCreate());
        verify(kafkaProducer).sendMessage(eq("order-created-topic"), eq(expected));
    }

    @Test
    void check_createOrder_should_return_forbiddenStatus() throws Exception {
        String url = "/api/v1/orders";
        OrderCreate request = TestData.getOrderCreate();

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void check_getOrderById_should_return_expectedResponse() throws Exception {
        OrderDto expected = TestData.getOrderDto();
        String url = "/api/v1/orders/" + expected.getId();

        doReturn(expected)
                .when(orderService)
                .getOrder(expected.getId());

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.productCount").value(expected.getProductCount()))
                .andExpect(jsonPath("$.customerId").value(expected.getCustomerId()))
                .andExpect(jsonPath("$.productId").value(expected.getProductId()))
                .andExpect(jsonPath("$.totalPrice").value(expected.getTotalPrice()))
                .andExpect(jsonPath("$.orderStatus").value(expected.getOrderStatus()))
                .andExpect(jsonPath("$.paymentStatus").value(expected.getPaymentStatus()))
                .andExpect(jsonPath("$.inventoryStatus").value(expected.getInventoryStatus()));

        verify(orderService).getOrder(eq(expected.getId()));
    }
}

