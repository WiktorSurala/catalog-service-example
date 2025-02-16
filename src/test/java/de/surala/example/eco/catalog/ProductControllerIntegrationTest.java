package de.surala.example.eco.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.surala.example.eco.catalog.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        postgresqlContainer.start();
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Test
    void givenProductIds_whenFetchingProducts_thenReturnMatchingProducts() throws Exception {
        // Arrange: Prepare request body
        Map<String, List<String>> requestBody = Map.of("productIds", List.of("A00001", "A00002", "A00003"));

        // Act: Call the PUT endpoint
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/products",
                requestBody,
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<ProductResponse>>>() {});
        assertThat(responseBody).containsKey("products");

        List<ProductResponse> products = responseBody.get("products");

        assertThat(products).hasSize(3);
        assertThat(products)
                .extracting("id")
                .containsExactlyInAnyOrder("A00001", "A00002", "A00003");
    }

    @Test
    void givenMixedValidAndInvalidProductIds_whenFetchingProducts_thenReturnOnlyValidProducts() throws Exception {
        // Arrange: Prepare request body
        Map<String, List<String>> requestBody = Map.of("productIds", List.of("A00001", "A00002", "B00001"));

        // Act: Call the PUT endpoint
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/products",
                requestBody,
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<ProductResponse>>>() {});
        assertThat(responseBody).containsKey("products");

        List<ProductResponse> products = responseBody.get("products");

        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting("id")
                .containsExactlyInAnyOrder("A00001", "A00002");
    }

    @Test
    void givenOnlyInvalidProductIds_whenFetchingProducts_thenReturnEmptyResult() throws Exception {
        // Arrange: Prepare request body
        Map<String, List<String>> requestBody = Map.of("productIds", List.of("B00001", "B00002", "B00003"));

        // Act: Call the PUT endpoint
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/products",
                requestBody,
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<ProductResponse>>>() {});
        assertThat(responseBody).containsKey("products");

        List<ProductResponse> products = responseBody.get("products");

        assertThat(products).hasSize(0);
    }

    @Test
    void givenEmptyProductIds_whenFetchingProducts_thenReturnEmptyResult() throws Exception {
        // Arrange: Prepare request body
        Map<String, List<String>> requestBody = Map.of("productIds", List.of());

        // Act: Call the PUT endpoint
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/products",
                requestBody,
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<ProductResponse>>>() {});
        assertThat(responseBody).containsKey("products");

        List<ProductResponse> products = responseBody.get("products");
        assertThat(products).hasSize(0);
    }

    @Test
    void givenDuplicateProductIds_whenFetchingProducts_thenReturnUniqueProducts() throws Exception {
        // Arrange: Prepare request body
        Map<String, List<String>> requestBody = Map.of("productIds", List.of("A00001", "A00002", "A00001"));

        // Act: Call the PUT endpoint
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/products",
                requestBody,
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var responseBody = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, List<ProductResponse>>>() {});
        assertThat(responseBody).containsKey("products");

        List<ProductResponse> products = responseBody.get("products");

        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting("id")
                .containsExactlyInAnyOrder("A00001", "A00002");
    }

    @Test
    void givenPaginationParameters_whenFetchingProducts_thenReturnPaginatedResults() throws Exception {
        // Act: Call the GET endpoint with pagination parameters (page=0, size=2)
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/products?page=0&size=2",
                String.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        // Parse response
        var paginatedProductResponse = objectMapper.readValue(response.getBody(), PaginatedProductResponse.class);

        // Check
        assertThat(paginatedProductResponse.getProducts()).isNotNull();
        assertThat(paginatedProductResponse.getProducts()).hasSize(2);
        assertThat(paginatedProductResponse.getCurrentPage()).isEqualTo(0);
        assertThat(paginatedProductResponse.getTotalItems()).isEqualTo(5);
        assertThat(paginatedProductResponse.getTotalPages()).isEqualTo(3); // 5 items, 2 per page = 3 pages
    }


}
