package com.programmingtechie.orderservice.Services;

import com.programmingtechie.orderservice.DTOs.InventoryResponse;
import com.programmingtechie.orderservice.DTOs.OrderLineItemsDto;
import com.programmingtechie.orderservice.DTOs.OrderRequest;
import com.programmingtechie.orderservice.Entities.Order;
import com.programmingtechie.orderservice.Entities.OrderLineItems;
import com.programmingtechie.orderservice.Repositories.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {


    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
//        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
//                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto)).toList();
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //Call Inventory service, and place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
//                .uri("http://localhost:8082/api/inventory",
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block(); //Synchronous request

        assert inventoryResponseArray != null;
        boolean allProductsInStock  = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock); //Learn this skill

        if(allProductsInStock){
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
