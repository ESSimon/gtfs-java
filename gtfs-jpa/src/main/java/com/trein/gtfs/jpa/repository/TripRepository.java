package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Trip findByTripId(String tripId);
    
}
