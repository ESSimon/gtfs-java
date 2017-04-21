package com.trein.gtfs.jpa.repository;

import com.trein.gtfs.jpa.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, Long> {

    Stop findByStopId(String parentStation);

}
