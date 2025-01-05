package com.jay.saga.payment.repository;

import com.jay.saga.payment.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Integer> {
}
