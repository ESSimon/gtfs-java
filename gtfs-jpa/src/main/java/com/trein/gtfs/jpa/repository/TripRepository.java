package com.trein.gtfs.jpa.repository;

import com.everysens.rtls.commons.repository.GenericRepository;
import com.trein.gtfs.jpa.entity.Trip;

public interface TripRepository extends GenericRepository<Trip, Long> {

    Trip findByTripId(String tripId);
    
}
