package de.surala.example.eco.catalog.services;

import de.surala.example.eco.catalog.dto.ProductRequest;
import de.surala.example.eco.catalog.dto.ProductResponse;
import de.surala.example.eco.catalog.entity.Item;
import de.surala.example.eco.catalog.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemIdGenerator itemIdGenerator;

    public ItemService(ItemRepository itemRepository, ItemIdGenerator itemIdGenerator) {
        this.itemRepository = itemRepository;
        this.itemIdGenerator = itemIdGenerator;
    }

    public Map<String, List<ProductResponse>> getProductsByProductRequest(ProductRequest productRequest) {
        List<String> productIds = productRequest.getProductIds();

        // Fetch products from the database
        List<Item> items = itemRepository.findAllById(productIds);

        // Map items to the response structure
        List<ProductResponse> products = items.stream()
                .map(item -> new ProductResponse(item.getId(), item.getName(), item.getPrice(), getStock(item.getId())))
                .toList();

        // Build the response
        return Map.of("products", products);
    }

    public Map<String, Object> getProductsByPagination(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemPage = itemRepository.findAll(pageable);

        // Convert items to DTOs
        List<ProductResponse> products = itemPage.getContent().stream()
                .map(item -> new ProductResponse(item.getId(), item.getName(), item.getPrice(), getStock(item.getId())))
                .toList();

        // Create response with pagination metadata
        return Map.of(
                "products", products,
                "currentPage", itemPage.getNumber(),
                "totalItems", itemPage.getTotalElements(),
                "totalPages", itemPage.getTotalPages()
        );
    }

    private Integer getStock(String itemId) {
        // TODO: Add InventoryService to fetch inventory from Items
        return 100; // Placeholder value
    }

    public Item saveItem(Item item) {
        if (item.getId() == null) {
            item.setId(itemIdGenerator.generateId());
        }
        return itemRepository.save(item);
    }
}
