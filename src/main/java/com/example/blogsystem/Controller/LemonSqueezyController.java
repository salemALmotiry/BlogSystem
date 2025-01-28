package com.example.blogsystem.Controller;

import com.example.blogsystem.Service.LemonSqueezyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class LemonSqueezyController {


    private final LemonSqueezyService lemonSqueezyService;

    @GetMapping("/products")
    public ResponseEntity getProduct(){

        return ResponseEntity.status(200).body(lemonSqueezyService.getProducts());
    }

    @PostMapping("/checkout/{productId}")
    public ResponseEntity<String> createCheckout(@PathVariable String productId) {
        String checkoutUrl = lemonSqueezyService.createCheckout(productId);
        return ResponseEntity.ok(checkoutUrl);
    }

    @PostMapping("/webhook-buy")
    public ResponseEntity<String> handleWebhook(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Object> body) {
        System.out.println(body);
        lemonSqueezyService.processWebhook(headers, body);

        return ResponseEntity.ok("OK");
    }
}
