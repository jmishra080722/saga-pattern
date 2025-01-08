package com.jay.saga.order.config;

import com.jay.saga.commons.event.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumerConfig {

    @Autowired
    private OrderStatusUpdateHandler handler;

    @Bean
    public Consumer<PaymentEvent> paymentEventConsumer(){
        System.out.println("paymentEventConsumer method");
        //Listen payment-event topic
        //Check payment status
        //if payment status is completed -> complete the order
        //if payment status is failed -> cancel the order
        return (payment ) -> handler.updateOrder(payment.getPaymentRequestDto().getOrderId(),
                po -> { po.setPaymentStatus(payment.getPaymentStatus());});
    }
}
