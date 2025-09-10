package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {}