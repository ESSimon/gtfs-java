package com.trein.gtfs.jpa.facade.mapper.stop_time;

import com.everysens.rtls.commons.prebuilt.PrebuiltMapper;
import com.trein.gtfs.dto.entity.StopTimeDto;
import com.trein.gtfs.jpa.entity.StopTime;
import com.trein.gtfs.jpa.facade.mapper.trip.TripMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring",
        uses = {
                TripMapper.class
        })
public interface StopTimeMapper extends PrebuiltMapper<StopTime, StopTimeDto> {
}
