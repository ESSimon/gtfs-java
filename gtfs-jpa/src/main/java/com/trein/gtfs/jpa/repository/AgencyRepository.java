package com.trein.gtfs.jpa.repository;

import com.everysens.rtls.commons.repository.GenericRepository;
import com.trein.gtfs.jpa.entity.Agency;

public interface AgencyRepository extends GenericRepository<Agency, Long> {
    
    Agency findByAgencyId(String id);
}
