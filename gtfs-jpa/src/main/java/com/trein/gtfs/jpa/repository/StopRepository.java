package com.trein.gtfs.jpa.repository;

import com.everysens.rtls.commons.repository.GenericRepository;
import com.trein.gtfs.jpa.entity.Stop;

public interface StopRepository extends GenericRepository<Stop, Long> {

    Stop findByStopId(String parentStation);

}
