package com.docomodigital.exerciseapi.dal.repository;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docomodigital.exerciseapi.dal.model.InternationalPhoneCode;

@Repository
public interface InternationalPhoneCodeRepository extends JpaRepository<InternationalPhoneCode, Integer> {
    Optional<InternationalPhoneCode> findByAreaCode(String areaCode);
}
