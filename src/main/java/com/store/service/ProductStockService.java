package com.store.service;

import com.store.entity.Product;
import com.store.exceptions.InsufficientStockException;
import com.store.exceptions.ProductNotFoundException;
import com.store.exceptions.ProductUnavailableException;
import com.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStockService {
    private final ProductRepository productRepository;

    public Product getAndValidateProduct(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        validateProductAvailability(product, quantity);
        updateProductStock(product, quantity);

        return product;
    }

    private void validateProductAvailability(Product product, Integer quantity) {
        if (!product.getActive()) {
            throw new ProductUnavailableException(product.getName());
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(product.getName());
        }
    }

    private void updateProductStock(Product product, Integer quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }
}
