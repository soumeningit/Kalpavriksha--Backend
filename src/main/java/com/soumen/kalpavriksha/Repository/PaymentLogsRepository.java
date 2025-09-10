package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.PaymentLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PaymentLogsRepository extends JpaRepository<PaymentLogs, Integer> {}