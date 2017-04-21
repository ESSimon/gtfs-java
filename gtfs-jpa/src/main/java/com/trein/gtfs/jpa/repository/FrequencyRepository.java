package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrequencyRepository extends JpaRepository<Frequency, Long> {
    
}
