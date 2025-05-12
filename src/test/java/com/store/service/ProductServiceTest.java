package com.store.service;

import com.store.dto.ProductDTO;
import com.store.entity.Product;
import com.store.mapper.ProductMapper;
import com.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve buscar todos os produtos paginados")
    void shouldFindAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(new Product());
        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));
        when(productMapper.toDTO(any())).thenReturn(new ProductDTO());

        Page<ProductDTO> result = productService.findAllProducts(pageable);

        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar produto por ID existente")
    void shouldFindProductById() {
        Long id = 1L;
        Product product = new Product();
        ProductDTO dto = new ProductDTO();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.findProductById(id);

        assertNotNull(result);
        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente por ID")
    void shouldThrowWhenProductNotFound() {
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> productService.findProductById(id));

        assertEquals("Product not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar um novo produto")
    void shouldCreateProduct() {
        ProductDTO dto = new ProductDTO();
        Product product = new Product();
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    void shouldUpdateProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        Product product = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toEntity(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(dto);

        ProductDTO result = productService.updateProduct(dto);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Deve deletar produto existente")
    void shouldDeleteProduct() {
        Long id = 1L;
        when(productRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(id));
        verify(productRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar produto inexistente")
    void shouldThrowOnDeleteIfProductNotExists() {
        Long id = 100L;
        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.deleteProduct(id));
    }
}
