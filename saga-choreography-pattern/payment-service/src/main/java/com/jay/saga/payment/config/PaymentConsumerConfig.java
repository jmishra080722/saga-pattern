package com.jay.saga.payment.config;

import com.jay.saga.commons.event.OrderEvent;
import com.jay.saga.commons.event.OrderStatus;
import com.jay.saga.commons.event.PaymentEvent;
import com.jay.saga.payment.service.PaymentService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class PaymentConsumerConfig {

    @Autowired
    private PaymentService paymentService;

    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor(){
        return orderEventFlux -> orderEventFlux.flatMap(this :: paymentProcess);
    }

    private Mono<PaymentEvent> paymentProcess(OrderEvent orderEvent) {
        //get the userId
        //check the balance availability
        //if the balance is sufficient ->   Payment completed and deduct amount from DB
        //if balance is not sufficient -> Cancel order event and update the balance in the DB

        if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())){
            return Mono.fromSupplier(() -> this.paymentService.newOrderEvent(orderEvent));
        }else{
            return Mono.fromRunnable(() -> this.paymentService.cancelOrderEvent(orderEvent));
        }
    }
}
