package com.docomodigital.exerciseapi.dal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.docomodigital.exerciseapi.dal.model.ConstantEnum;

@Repository
public interface ConstantsEnumRepository extends JpaRepository<ConstantEnum, Integer> {

	List<ConstantEnum> findByEnumType(String type);
}
