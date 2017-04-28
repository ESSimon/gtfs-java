package com.trein.gtfs.jpa.facade.mapper.trip;

import com.everysens.rtls.commons.prebuilt.PrebuiltMapper;
import com.trein.gtfs.dto.entity.TripDto;
import com.trein.gtfs.jpa.entity.Trip;
import com.trein.gtfs.jpa.facade.mapper.stop_time.StopTimeMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {
                StopTimeMapper.class
        })
public interface TripMapper extends PrebuiltMapper<Trip, TripDto> {
}
