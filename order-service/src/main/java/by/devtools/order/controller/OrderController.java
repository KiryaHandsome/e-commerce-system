package by.devtools.order.controller;

import by.devtools.order.dto.OrderCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {


    @PostMapping
    public ResponseEntity<?> processOrder(@Valid @RequestBody OrderCreate request) {

    }

}
