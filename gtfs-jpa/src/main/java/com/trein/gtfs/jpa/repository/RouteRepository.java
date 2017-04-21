package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Route findByRouteId(String routeId);
    
}
