package de.surala.example.eco.catalog.controller;

import de.surala.example.eco.catalog.dto.ProductRequest;
import de.surala.example.eco.catalog.dto.ProductResponse;
import de.surala.example.eco.catalog.services.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ItemService itemService;

    public ProductController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Map<String, List<ProductResponse>>> getProducts(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(itemService.getProductsByProductRequest(productRequest));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,    // Default page is 0
            @RequestParam(defaultValue = "10") int size    // Default size is 10
    ) {
        return ResponseEntity.ok(itemService.getProductsByPagination(page, size));
    }

}

