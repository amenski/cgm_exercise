package com.docomodigital.exerciseapi.dal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.docomodigital.exerciseapi.dal.model.ConstantEnum;

/**
 * Assuming that there will be different constant properties used in different services, 
 * this repository holds constants used in the application instead of putting them in files.
 * 
 * I expect that there will be an endpoint that exposes these properties using a service utilizing this repository
 * 
 * For instance, the constants could be 
 *      - PAYMENT_TYPE {F}
 *      - TRANSACTION_STATUS{OPEN, CLOSED, UNSURE...etc}
 *      - CURRENCY_TYPE {EURO, DOLLAR....etc}
 *      - CUSTOMER_TYPE {INDIVIDUAL, CORPORATE...}
 *      - PROVIDER_TYPE {...} and so on.
 * 
 * @author Amanuel
 *
 */
@Repository
public interface ConstantsEnumRepository extends JpaRepository<ConstantEnum, Integer> {

    @Query("select const from ConstantEnum const where const.enumType=:type and const.disabled=false")
	List<ConstantEnum> findEnumByTypeAndStatus(@Param("type") String type);
}
