package com.example.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api")
public class PayToFosaService {

    private final WebClient webClient;

    public PayToFosaService(WebClient insecureWebClient) {
        this.webClient = insecureWebClient;
    }
        
    @Value("${cbs.generate-token-url}")
    private String tokenUrl;
    
    @Value("${cbs.basic-authorization-token}")
    private String basicAuthToken;

    @GetMapping("/getaccesstoken")
    public String getAccessToken(){

        System.out.println("CBS_GENERATE_TOKEN_URL = " + tokenUrl);

        Map<String, Object> result = webClient.post()
                .uri(tokenUrl)
                .header("Authorization", "Basic " + basicAuthToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (result != null && result.containsKey("access_token")) {
            return result.get("access_token").toString();
        }
        throw new RuntimeException("Failed to retrieve access token");  
    }
}
