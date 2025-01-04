package com.jay.saga.order.service;

import com.jay.saga.commons.dto.OrderRequestDto;
import com.jay.saga.commons.event.OrderStatus;
import com.jay.saga.order.entity.PurchaseOrder;
import com.jay.saga.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder order = orderRepository.save(convertDtoToEntity(orderRequestDto));
        orderRequestDto.setOrderId(order.getId());

        //Produce kafka event with status order created
        orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
        return order;
    }

    public List<PurchaseOrder> getAllOrders(){
        return orderRepository.findAll();
    }

    private PurchaseOrder convertDtoToEntity(OrderRequestDto dto){
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUserId(dto.getUserId());
        purchaseOrder.setProductId(dto.getProductId());
        purchaseOrder.setPrice(dto.getAmount());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }
}
