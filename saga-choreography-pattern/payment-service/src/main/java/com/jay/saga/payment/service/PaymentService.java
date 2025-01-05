package com.jay.saga.payment.service;

import com.jay.saga.commons.dto.OrderRequestDto;
import com.jay.saga.commons.dto.PaymentRequestDto;
import com.jay.saga.commons.event.OrderEvent;
import com.jay.saga.commons.event.PaymentEvent;
import com.jay.saga.commons.event.PaymentStatus;
import com.jay.saga.payment.entity.UserBalance;
import com.jay.saga.payment.entity.UserTransaction;
import com.jay.saga.payment.repository.UserBalanceRepository;
import com.jay.saga.payment.repository.UserTransactionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaymentService {


    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initUserBalanceInDb(){
        userBalanceRepository.saveAll(Stream.of(
                new UserBalance(101, 5000),
                new UserBalance(102, 3000),
                new UserBalance(103, 3000),
                new UserBalance(104, 3000),
                new UserBalance(105, 3000)).collect(Collectors.toList()));
    }

    //get the userId
    //check the balance availability
    //if the balance is sufficient ->   Payment completed and deduct amount from DB
    //if balance is not sufficient -> Cancel order event and update the balance in the DB

    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(orderRequestDto.getOrderId(),
                orderRequestDto.getUserId(), orderRequestDto.getAmount());

        return userBalanceRepository.findById(orderRequestDto.getUserId())
                .filter(ub -> ub.getPrice() > orderRequestDto.getAmount())
                .map(ub -> {
                    ub.setPrice(ub.getPrice() - orderRequestDto.getAmount());
                    userTransactionRepository.save(new UserTransaction(orderRequestDto.getOrderId(),
                            orderRequestDto.getUserId(), orderRequestDto.getAmount()));
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
                }).orElse( new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_FAILED));

    }

    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {

        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(ut -> {
                    userTransactionRepository.delete(ut);
                    userTransactionRepository.findById(ut.getUserId())
                            .ifPresent(ub -> ub.setAmount(ub.getAmount() + ut.getAmount()));
                });

    }
}
