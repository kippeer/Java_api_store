package com.store.controller;

import com.store.dto.ProductDTO;
import com.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    void shouldReturnProductById() {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        when(productService.findProductById(1L)).thenReturn(dto);

        ResponseEntity<Page<ProductDTO>> response = productController.getProducts(
                1L, null, null, null, null, null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(dto.getId(), response.getBody().getContent().get(0).getId());
    }

    @Test
    void shouldReturnProductsByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> page = new PageImpl<>(Collections.emptyList());
        when(productService.searchProducts("test", pageable)).thenReturn(page);

        ResponseEntity<Page<ProductDTO>> response = productController.getProducts(
                null, null, "test", null, null, null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).searchProducts("test", pageable);
    }

    @Test
    void shouldReturnAllProductsWhenNoFiltersProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> page = new PageImpl<>(Collections.emptyList());
        when(productService.findAllProducts(pageable)).thenReturn(page);

        ResponseEntity<Page<ProductDTO>> response = productController.getProducts(
                null, null, null, null, null, null, null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).findAllProducts(pageable);
    }

    @Test
    void shouldCreateProduct() {
        ProductDTO input = new ProductDTO();
        input.setName("New Product");

        ProductDTO saved = new ProductDTO();
        saved.setId(1L);
        saved.setName("New Product");

        when(productService.createProduct(input)).thenReturn(saved);

        ResponseEntity<ProductDTO> response = productController.createProduct(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved.getId(), response.getBody().getId());
    }

    @Test
    void shouldUpdateProduct() {
        ProductDTO input = new ProductDTO();
        input.setName("Updated Product");

        ProductDTO updated = new ProductDTO();
        updated.setId(2L);
        updated.setName("Updated Product");

        when(productService.updateProduct(any())).thenReturn(updated);

        ResponseEntity<ProductDTO> response = productController.updateProduct(2L, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated.getId(), response.getBody().getId());
    }

    @Test
    void shouldDeleteProduct() {
        Long productId = 3L;
        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<Void> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void shouldReturnLowStockProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDTO> lowStockProducts = List.of(new ProductDTO());

        when(productService.findLowStockProducts(5)).thenReturn(lowStockProducts);

        ResponseEntity<Page<ProductDTO>> response = productController.getProducts(
                null, null, null, null, null, null, 5, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }
}
