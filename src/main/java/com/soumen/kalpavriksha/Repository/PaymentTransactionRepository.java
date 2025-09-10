package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
}