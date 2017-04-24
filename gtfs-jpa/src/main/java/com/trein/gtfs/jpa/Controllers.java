package com.trein.gtfs.jpa;

import com.everysens.rtls.annotation.RtlsController;
import com.everysens.rtls.annotation.RtlsControllers;
import com.trein.gtfs.dto.entity.*;
import com.trein.gtfs.jpa.entity.*;
import com.trein.gtfs.jpa.repository.*;

@RtlsControllers({
        @RtlsController(entity = Agency.class, dto = AgencyDto.class, repository = AgencyRepository.class),
        @RtlsController(entity = CalendarDate.class, dto = CalendarDateDto.class),
        @RtlsController(entity = Calendar.class, dto = CalendarDto.class),
        @RtlsController(entity = Fare.class, dto = FareDto.class),
        @RtlsController(entity = FareRule.class, dto = FareRuleDto.class),
        @RtlsController(entity = FeedInfo.class, dto = FeedInfoDto.class),
        @RtlsController(entity = Frequency.class, dto = FrequencyDto.class),
        @RtlsController(entity = Route.class, dto = RouteDto.class, repository = RouteRepository.class),
        @RtlsController(entity = Shape.class, dto = ShapeDto.class, repository = ShapeRepository.class),
        @RtlsController(entity = Stop.class, dto = StopDto.class, repository = StopRepository.class),
        @RtlsController(entity = StopTime.class, dto = StopTimeDto.class),
        @RtlsController(entity = Transfer.class, dto = TransferDto.class),
        @RtlsController(entity = Trip.class, dto = TripDto.class, repository = TripRepository.class),
})
public class Controllers {
}
