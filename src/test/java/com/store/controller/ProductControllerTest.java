package com.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.dto.ProductDTO;
import com.store.dto.ProductFilterDTO;
import com.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO sampleProductDTO;
    private final Long PRODUCT_ID = 1L;

    @BeforeEach
    public void setup() {
        sampleProductDTO = new ProductDTO();
        sampleProductDTO.setId(PRODUCT_ID);
        sampleProductDTO.setName("Test Product");
        sampleProductDTO.setPrice(new BigDecimal("29.99"));
        // Set other required properties for a valid ProductDTO
    }

    @Test
    @WithMockUser
    public void getProducts_withNoFilters_shouldReturnAllProducts() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDTO> productList = Collections.singletonList(sampleProductDTO);
        PageImpl<ProductDTO> productPage = new PageImpl<>(productList, pageable, productList.size());
        
        when(productService.findProductsByFilters(any(ProductFilterDTO.class), any(Pageable.class)))
            .thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
        
        verify(productService).findProductsByFilters(any(ProductFilterDTO.class), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void createProduct_asAdmin_shouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(sampleProductDTO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.name").value("Test Product"));
        
        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    public void createProduct_asClient_shouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(sampleProductDTO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID));
        
        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"OPERATOR"})
    public void createProduct_asOperator_shouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(sampleProductDTO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID));
        
        verify(productService).createProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void createProduct_asUser_shouldBeForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isForbidden());
        
        verify(productService, never()).createProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateProduct_withValidData_shouldReturnUpdatedProduct() throws Exception {
        // Arrange
        when(productService.updateProduct(any(ProductDTO.class))).thenReturn(sampleProductDTO);

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID));
        
        verify(productService).updateProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void updateProduct_asUser_shouldBeForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleProductDTO)))
                .andExpect(status().isForbidden());
        
        verify(productService, never()).updateProduct(any(ProductDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void deleteProduct_asAdmin_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(PRODUCT_ID);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(productService).deleteProduct(PRODUCT_ID);
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    public void deleteProduct_asClient_shouldBeForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/products/{id}", PRODUCT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        
        verify(productService, never()).deleteProduct(any());
    }
}