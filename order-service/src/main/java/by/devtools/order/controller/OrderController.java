package by.devtools.order.controller;

import by.devtools.domain.OrderDto;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.service.OrderService;
import by.devtools.order.util.JsonUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreate request) {
        OrderDto order = orderService.createOrder(request);
        kafkaTemplate.send("order-created-topic", JsonUtil.toJson(order));
        return ResponseEntity
                .created(URI.create("/api/v1/orders/" + order.getId()))
                .body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer id) {
        OrderDto response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }
}
