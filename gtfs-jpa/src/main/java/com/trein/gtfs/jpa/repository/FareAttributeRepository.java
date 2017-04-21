package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.dto.entity.FareAttribute;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FareAttributeRepository extends JpaRepository<FareAttribute, Long> {
    
}
