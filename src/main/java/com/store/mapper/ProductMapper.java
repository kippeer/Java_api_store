package com.store.mapper;

import com.store.dto.ProductDTO;
import com.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOList(List<Product> products);

    Product toEntity(ProductDTO productDTO);

    void updateEntityFromDTO(ProductDTO productDTO, @MappingTarget Product product);
}