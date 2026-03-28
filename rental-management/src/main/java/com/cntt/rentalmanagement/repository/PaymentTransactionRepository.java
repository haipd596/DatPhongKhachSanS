package com.cntt.rentalmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cntt.rentalmanagement.domain.models.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
