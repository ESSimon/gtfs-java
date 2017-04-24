package com.trein.gtfs.jpa.repository;

import com.everysens.rtls.commons.repository.GenericRepository;
import com.trein.gtfs.jpa.entity.Route;

public interface RouteRepository extends GenericRepository<Route, Long> {

    Route findByRouteId(String routeId);
    
}
