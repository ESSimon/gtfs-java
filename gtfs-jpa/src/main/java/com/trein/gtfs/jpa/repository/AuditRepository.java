package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.app.Audit;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuditRepository extends JpaRepository<Audit, Long> {
    
}
