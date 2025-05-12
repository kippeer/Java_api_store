package com.store.service;

import com.store.dto.ProductDTO;
import com.store.entity.Product;
import com.store.mapper.ProductMapper;
import com.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ProductDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        return productMapper.toDTO(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable)
                .map(productMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCase(category, pageable)
                .map(productMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(productMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        // Validate product exists
        productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productDTO.getId()));

        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }
}