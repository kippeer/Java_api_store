package com.store.controller;

import com.store.dto.ProductDTO;
import com.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get products with optional filtering",
            description = "Returns products based on provided filters")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @Parameter(description = "Product ID (optional)") @RequestParam(required = false) Long id,
            @Parameter(description = "Active status (optional)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Search keyword (optional)") @RequestParam(required = false) String keyword,
            @Parameter(description = "Category (optional)") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price (optional)") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (optional)") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Low stock threshold (optional)") @RequestParam(required = false) Integer lowStockThreshold,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        // If specific product ID is requested
        if (id != null) {
            ProductDTO product = productService.findProductById(id);
            return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(product), pageable, 1));
        }

        // Search by keyword
        if (keyword != null) {
            return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
        }

        // Filter by category
        if (category != null) {
            return ResponseEntity.ok(productService.findProductsByCategory(category, pageable));
        }

        // Filter by price range
        if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(productService.findProductsByPriceRange(minPrice, maxPrice, pageable));
        }

        // Filter by active status
        if (active != null) {
            return ResponseEntity.ok(productService.findActiveProducts(pageable));
        }

        // Get low stock products
        if (lowStockThreshold != null) {
            List<ProductDTO> lowStockProducts = productService.findLowStockProducts(lowStockThreshold);
            return ResponseEntity.ok(new PageImpl<>(lowStockProducts, pageable, lowStockProducts.size()));
        }

        // Default: return all products
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'OPERATOR')")
    @Operation(summary = "Create product", description = "Creates a new product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.createProduct(productDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'OPERATOR')")
    @Operation(summary = "Update product", description = "Updates an existing product")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO
    ) {
        productDTO.setId(id);
        return ResponseEntity.ok(productService.updateProduct(productDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}