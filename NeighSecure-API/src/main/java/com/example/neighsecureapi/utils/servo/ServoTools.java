package com.example.neighsecureapi.utils.servo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
public class ServoTools {
    @Value("${adafruit.io.username}")
    private String adafruitUsername;

    @Value("${adafruit.io.key}")
    private String adafruitKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> moveServo(Map<String, Object> payload) {
        return requestToAdafruit("boton-servo", payload);
    }

    public ResponseEntity<?> movePea(Map<String, Object> payload) {
        return requestToAdafruit("boton-pea", payload);
    }

    private ResponseEntity<?> requestToAdafruit(String feedKey, Map<String, Object> payload) {
        String value = payload.get("value").toString();
        String url = String.format("https://io.adafruit.com/api/v2/%s/feeds/%s/data", adafruitUsername, feedKey);
        Map<String, String> request = Map.of("value", value);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-AIO-Key", adafruitKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok().body(Map.of("success", true));
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(Map.of("error", Objects.requireNonNull(response.getBody())));
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
