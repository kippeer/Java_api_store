package com.store.mapper;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "items", target = "items")
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOList(List<Order> orders);

    @Mapping(source = "productId", target = "product.id")
    OrderItem toOrderItem(OrderDTO.OrderItemDTO itemDTO);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderDTO.OrderItemDTO toOrderItemDTO(OrderItem orderItem);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true) // se você não estiver atualizando os itens aqui
    void updateOrderFromDTO(OrderDTO dto, @MappingTarget Order order);
}