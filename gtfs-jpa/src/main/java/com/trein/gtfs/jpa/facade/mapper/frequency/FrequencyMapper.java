package com.trein.gtfs.jpa.facade.mapper.frequency;

import com.everysens.rtls.commons.prebuilt.PrebuiltMapper;
import com.trein.gtfs.dto.entity.FrequencyDto;
import com.trein.gtfs.jpa.entity.Frequency;
import com.trein.gtfs.jpa.facade.mapper.trip.TripMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {
                TripMapper.class
        })
public interface FrequencyMapper extends PrebuiltMapper<Frequency, FrequencyDto> {
}
