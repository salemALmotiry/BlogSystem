package com.example.blogsystem.Service;

import com.example.blogsystem.Api.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;


@Service
public class LemonSqueezyService {

    private final WebClient webClient;

    @Value("${lemon.api.key}")
    private String apiKey;

    @Value("${lemon.api.base.url}")
    private String baseUrl;

    @Value("${LEMON_SQUEEZY_STORE_ID}")
    String storeId ;

    @Value("${lemon.squeezy.webhook.secret}")
    private String webhookSecret;

    public LemonSqueezyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.lemonsqueezy.com/v1")
                .defaultHeader("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5NGQ1OWNlZi1kYmI4LTRlYTUtYjE3OC1kMjU0MGZjZDY5MTkiLCJqdGkiOiJiZmExZGU1MjE1ODBkMTRkMjE2MjhkNzgzMTE1YzJkNDFhNzhjOWRmNjFjZTA3NTBkNWM1OTQ4Y2Q0MzI4MWMxMjM2YTc0ODViNjdmYWM1YyIsImlhdCI6MTczNzg5MjU1Ni42NzE1ODcsIm5iZiI6MTczNzg5MjU1Ni42NzE1OSwiZXhwIjoyMDUzNDI1MzU2LjYxMDI4NSwic3ViIjoiNDI4MjAxOCIsInNjb3BlcyI6W119.BCGfV9RIq62vYw-4Z_swU4xY6_OU6aKw6joADe0rdKnHbC-Y3F3-YBKskoNy1-O8N_kG0D4WdPOLKbfvkP7CJ1JMbixFrUGd-2fi6DLWUfmcil2sN0mpo3zTgrC59cyjZuApUbaBvk21IQlxPCWJdGDOr9KuloI37Qgxgq9KdNdMhqQg6-gtCE_vR9k3xgWArseLva-bo-VVcRWqcv3BPfHvv4bcVnFT3_xrDvuTHJwt-eZApk9S4Nh_U3peaEDfONB4LatOCl-F3lWehqq8XhhjzHf5mXsVmFk3xabhyFZctVpU4eopjiTRdlxJI6FSyPQ_v-EcgSV09SoQzD1wD6emrTOj_VTa5OPC0ZGie9jBSIBeqHh6tg5l-39FIl9rAfWEHFX6fbvu67XQsGmney46ohlNga7OUDsg6_hyGX1zWUUQ2KYNOimf6mex9e4J6_zk3_EekI26w_oW0_uqWZtX3WLv4L2MG_-xeW4qlANPM4AAa-T9A94923EEQOd0")
                .defaultHeader("Accept", "application/vnd.api+json")
                .defaultHeader("Content-Type", "application/vnd.api+json")
                .build();
    }

    public String getProducts() {
        String response = webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray products = jsonResponse.getJSONArray("data");
        StringBuilder productDetails = new StringBuilder();

        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);

            // جلب الـ ID مباشرة من الكائن الأساسي
            String id = product.optString("id", "N/A");

            // جلب الخصائص الأخرى من كائن "attributes"
            JSONObject attributes = product.getJSONObject("attributes");

            String largeThumbUrl = attributes.has("large_thumb_url") && !attributes.isNull("large_thumb_url")
                    ? attributes.optString("large_thumb_url", "N/A")
                    : "N/A";
            String name = attributes.optString("name", "N/A");
            String description = attributes.optString("description", "N/A");
            String priceFormatted = attributes.optString("price_formatted", "N/A");
            String buyNowUrl = attributes.optString("buy_now_url", "N/A");

            productDetails.append("ID: ").append(id).append("\n") // إضافة الـ ID إلى النص الناتج
                    .append("Name: ").append(name).append("\n")
                    .append("Description: ").append(description).append("\n")
                    .append("Price: ").append(priceFormatted).append("\n")
                    .append("Buy Now URL: ").append(buyNowUrl).append("\n")
                    .append("Large Thumbnail URL: ").append(largeThumbUrl).append("\n")
                    .append("\n");
        }

        return productDetails.toString();
    }


    public String createCheckout(String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new ApiException("productId is required");
        }

        if (storeId == null || storeId.isEmpty()) {
            throw new ApiException("LEMON_SQUEEZY_STORE_ID environment variable is required");
        }

        String payload = String.format("""
                {
                   "data": {
                     "type": "checkouts",
                     "attributes": {
                       "checkout_data": {
                         "custom": {
                           "user_id": "123"
                         }
                       }
                     },
                     "relationships": {
                       "store": {
                         "data": {
                           "type": "stores",
                           "id": "%s"
                         }
                       },
                       "variant": {
                         "data": {
                           "type": "variants",
                           "id": "%s"
                         }
                       }
                     }
                   }
                 }
        """, storeId, 673036);

        System.out.println("Payload: " + payload); // Log the payload

        String response = webClient.post()
                .uri("/checkouts")
                .bodyValue(payload)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    System.out.println("Client error: " + clientResponse.statusCode());
                    return Mono.error(new ApiException("Client error: " + clientResponse.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    System.out.println("Server error: " + clientResponse.statusCode());
                    return Mono.error(new ApiException("Server error: " + clientResponse.statusCode()));
                })
                .bodyToMono(String.class)
                .block();
        System.out.println("Response: " + response); // Log the response

        return extractCheckoutUrl(response);
    }


    private String extractCheckoutUrl(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse
                    .getJSONObject("data")
                    .getJSONObject("attributes")
                    .getString("url");
        } catch (Exception e) {
            throw new ApiException("Failed to parse checkout URL from response");
        }
    }


    public void processWebhook(HttpHeaders headers, Map<String, Object> body) {
        try {

            String eventType = headers.getFirst("X-Event-Name");
            if (eventType == null || eventType.isEmpty()) {
                throw new ApiException("Missing event type");
            }

            String signature = headers.getFirst("X-Signature");
            if (signature == null || signature.isEmpty()) {
                throw new ApiException("Missing signature");
            }

            String payload = new ObjectMapper().writeValueAsString(body);
            if (!isSignatureValid(payload, signature)) {
                throw new ApiException("Invalid signature");
            }

            if ("order_created".equals(eventType)) {
                handleOrderCreatedEvent(body);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // Log errors and handle them gracefully (optional: add a logger instead of printStackTrace)
        }
    }

    private boolean isSignatureValid(String payload, String signature) {
        try {
            // Decode the secret key
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            // Generate HMAC digest
            byte[] hmacBytes = mac.doFinal(payload.getBytes());
            String computedSignature = Base64.getEncoder().encodeToString(hmacBytes);

            // Compare computed signature with the one received
            return MessageDigest.isEqual(computedSignature.getBytes(), signature.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void handleOrderCreatedEvent(Map<String, Object> body) {
        Map<String, Object> meta = (Map<String, Object>) body.get("meta");
        Map<String, Object> customData = (Map<String, Object>) meta.get("custom_data");
        String userId = (String) customData.get("user_id");

        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        boolean isSuccessful = "paid".equals(attributes.get("status"));

        // Perform the necessary business logic
        System.out.printf("User ID: %s, Payment Successful: %s%n", userId, isSuccessful);
    }

    }

