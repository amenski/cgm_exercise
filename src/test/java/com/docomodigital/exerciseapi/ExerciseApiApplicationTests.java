package com.docomodigital.exerciseapi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.docomodigital.exerciseapi.controllers.PurchasesConstrollerTest;
import com.docomodigital.exerciseapi.services.ExternalApiServiceImplTest;
import com.docomodigital.exerciseapi.services.PaymentTransactionImplTest;

@RunWith(Suite.class)
@SuiteClasses({
    PaymentTransactionImplTest.class, 
    ExternalApiServiceImplTest.class,
    PurchasesConstrollerTest.class
    })
class ExerciseApiApplicationTests {}
