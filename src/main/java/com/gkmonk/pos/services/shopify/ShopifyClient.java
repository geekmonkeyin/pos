package com.gkmonk.pos.services.shopify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Service
public class ShopifyClient {

    @Autowired
    private WebClient.Builder builder;

    //@Value("${shopify.shop}")
    private String shop;

    //@Value("${shopify.token}")
    private String token;

   // @Value("${shopify.apiVersion}")
    private String apiVersion;

    private WebClient webClient;

    @PostConstruct
    void init() {
        shop = "bad22a-2";
        apiVersion = "2025-07";
        //token = System.getenv().get("shopify.token");
        token  = System.getenv().get("shopify_shortToken");
       // shortToken  = System.getenv().get("shopify.shortToken");
        this.webClient = builder
                .baseUrl("https://" + shop + ".myshopify.com/admin/api/" + apiVersion + "/graphql.json")
                .defaultHeader("X-Shopify-Access-Token", token)
                .build();
    }


    public Mono<Map> post(String query, Map<String, Object> variables) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", query, "variables", variables))
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(ex -> ex instanceof WebClientResponseException
                                        && ((WebClientResponseException) ex).getStatusCode().value() == 429)
                );
    }
}
