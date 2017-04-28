package com.trein.gtfs.jpa.service.calendar;

import com.everysens.rtls.commons.prebuilt.PrebuiltService;
import com.trein.gtfs.jpa.entity.Calendar;
import com.trein.gtfs.jpa.repository.calendar.CalendarRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CalendarService extends PrebuiltService<Calendar> implements ICalendarService {
    private final CalendarRepository repository;

    @Autowired
    public CalendarService(CalendarRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public List<Calendar> findPossibleBy(DateTime date) {
        // The locale should be UK !
        String dayOfWeek = date.dayOfWeek().getAsText(Locale.UK).toLowerCase();
        List<Calendar> all = repository.findAll((root, query, cb) -> cb.and(
                cb.between(cb.literal(date), root.get("startDate"), root.get("endDate")),
                cb.equal(root.get(dayOfWeek), true)
        ));

        return all;
    }
}
