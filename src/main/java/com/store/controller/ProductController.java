package com.store.controller;

import com.store.dto.ProductDTO;
import com.store.dto.ProductFilterDTO;
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
    @Operation(summary = "Get products with optional filtering", description = "Returns products based on provided filters")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @Parameter(description = "Product ID (optional)") @RequestParam(required = false) Long id,
            @Parameter(description = "Description (optional)") @RequestParam(required = false) String description,
            @Parameter(description = "Active status (optional)") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Search keyword (optional)") @RequestParam(required = false) String keyword,
            @Parameter(description = "Category (optional)") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price (optional)") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (optional)") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Low stock threshold (optional)") @RequestParam(required = false) Integer lowStockThreshold,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        // Se um ID específico for solicitado
        if (id != null) {
            ProductDTO product = productService.findProductById(id);
            return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(product), pageable, 1));
        }

        // Preparar o filtro para enviar ao service
        ProductFilterDTO filterDTO = new ProductFilterDTO();
        filterDTO.setDescription(description);
        filterDTO.setCategory(category);
        filterDTO.setMinPrice(minPrice);
        filterDTO.setMaxPrice(maxPrice);
        filterDTO.setActive(active);
        filterDTO.setPage(pageable.getPageNumber());
        filterDTO.setSize(pageable.getPageSize());

        // Buscar produtos com base nos filtros
        Page<ProductDTO> products = productService.findProductsByFilters(filterDTO);

        // Se keyword for fornecida, fazer uma pesquisa por palavra-chave
        if (keyword != null) {
            products = productService.searchProducts(keyword, pageable);
        }

        // Se a quantidade de produtos de baixo estoque for solicitada
        if (lowStockThreshold != null) {
            List<ProductDTO> lowStockProducts = productService.findLowStockProducts(lowStockThreshold);
            return ResponseEntity.ok(new PageImpl<>(lowStockProducts, pageable, lowStockProducts.size()));
        }

        return ResponseEntity.ok(products);
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
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
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
