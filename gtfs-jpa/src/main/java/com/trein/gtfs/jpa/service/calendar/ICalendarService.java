package com.trein.gtfs.jpa.service.calendar;

import com.everysens.rtls.commons.service.IGenericService;
import com.trein.gtfs.jpa.entity.Calendar;
import org.joda.time.DateTime;

import java.util.List;

public interface ICalendarService extends IGenericService<Calendar> {
    List<Calendar> findPossibleBy(DateTime date);
}