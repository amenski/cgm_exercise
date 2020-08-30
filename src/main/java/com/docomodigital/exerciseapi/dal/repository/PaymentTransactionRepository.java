package com.docomodigital.exerciseapi.dal.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docomodigital.exerciseapi.dal.model.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    
    List<PaymentTransaction> findByPhoneNumber(String phoneNumber);
}