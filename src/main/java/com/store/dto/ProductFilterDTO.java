package com.store.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductFilterDTO {
    private String name;
    private String description;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}