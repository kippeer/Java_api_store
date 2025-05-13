package com.store.controller;

import com.store.dto.ProductDTO;
import com.store.dto.ProductFilterDTO;
import com.store.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerParamTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void getProducts_shouldUseFilterAndPageable() {
        // Arrange
        ProductFilterDTO filterDto = new ProductFilterDTO();
        Pageable pageable = mock(Pageable.class);
        
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(new BigDecimal("29.99"));
        
        PageImpl<ProductDTO> expectedPage = new PageImpl<>(
            Collections.singletonList(productDTO), pageable, 1);
        
        when(productService.findProductsByFilters(eq(filterDto), eq(pageable))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductDTO>> response = productController.getProducts(filterDto, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("Test Product", response.getBody().getContent().get(0).getName());
        
        verify(productService).findProductsByFilters(eq(filterDto), eq(pageable));
    }

    @Test
    public void createProduct_shouldReturnCreatedProduct() {
        // Arrange
        ProductDTO inputProductDTO = new ProductDTO();
        inputProductDTO.setName("New Product");
        inputProductDTO.setPrice(new BigDecimal("19.99"));
        
        ProductDTO createdProductDTO = new ProductDTO();
        createdProductDTO.setId(1L);
        createdProductDTO.setName("New Product");
        createdProductDTO.setPrice(new BigDecimal("19.99"));
        
        when(productService.createProduct(inputProductDTO)).thenReturn(createdProductDTO);

        // Act
        ResponseEntity<ProductDTO> response = productController.createProduct(inputProductDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdProductDTO, response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("New Product", response.getBody().getName());
        
        verify(productService).createProduct(inputProductDTO);
    }

    @Test
    public void updateProduct_shouldSetIdAndReturnUpdatedProduct() {
        // Arrange
        Long productId = 1L;
        ProductDTO inputProductDTO = new ProductDTO();
        inputProductDTO.setName("Updated Product");
        inputProductDTO.setPrice(new BigDecimal("24.99"));
        
        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(productId);
        updatedProductDTO.setName("Updated Product");
        updatedProductDTO.setPrice(new BigDecimal("24.99"));
        
        when(productService.updateProduct(inputProductDTO)).thenReturn(updatedProductDTO);

        // Act
        ResponseEntity<ProductDTO> response = productController.updateProduct(productId, inputProductDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProductDTO, response.getBody());
        assertEquals(productId, inputProductDTO.getId());
        
        verify(productService).updateProduct(inputProductDTO);
    }

    @Test
    public void deleteProduct_shouldReturnNoContent() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(productId);

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(productService).deleteProduct(productId);
    }
}