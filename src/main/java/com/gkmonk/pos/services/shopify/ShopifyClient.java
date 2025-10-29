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

    private static final String CUSTOMER_QUERY = """
            query FindCustomersByPhone($q: String!, $first: Int = 10, $after: String) {
                  customers(first: $first, after: $after, query: $q) {
                    pageInfo { hasNextPage endCursor }
                    edges {
                      node {
                        id
                        legacyResourceId
                        displayName
                        email
                        phone
                        state
                        tags
                        createdAt
                        updatedAt
                        defaultAddress {
                          id
                          name
                          phone
                          address1
                          address2
                          city
                          province
                          country
                          zip
                        }
                        addresses {
                          id
                          name
                          phone
                          address1
                          address2
                          city
                          province
                          country
                          zip
                        }
                      }
                    }
                  }
                }
        """;

    @PostConstruct
    public void init() {
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

    public Mono<String> findCustomersByPhone(String rawPhone) {
        // try both with and without +91, plus a wildcard fallback
        String normalized = rawPhone.replaceAll("[^0-9+]", "");
        String q = "phone:" + normalized + " OR phone:+91" + normalized + "";
        Map<String, Object> body = Map.of("query", CUSTOMER_QUERY, "variables", Map.of("q", q, "first", 10));

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Map> findProductById(String productGID) {
        String graphqlQuery = """
        {
          product(id: "%s") {
            id
            title
            description
            status
            tags
            totalInventory
            variants(first: 5) {
              edges {
                node {
                  id
                  title
                  sku
                  price
                  inventoryQuantity
                }
              }
            }
          }
        }
        """.formatted(productGID);

        Map<String, String> body = Map.of("query", graphqlQuery);


        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
