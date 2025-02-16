package de.surala.example.eco.catalog;
import de.surala.example.eco.catalog.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedProductResponse {
    private List<ProductResponse> products;
    private int currentPage;
    private long totalItems;
    private int totalPages;

}
