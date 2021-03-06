package com.trein.gtfs.dto.entity;

import com.everysens.rtls.commons.dto.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Dates for service IDs using a weekly schedule. Specify when service starts and ends, as well as
 * days of the week where service is available.
 *
 * @author trein
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDto implements Identifiable {
    private Long id;
    private String serviceId;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private DateTime startDate;
    private DateTime endDate;

    /**
     * service_id Required The service_id contains an ID that uniquely identifies a set of dates
     * when service is available for one or more routes. Each service_id value can appear at most
     * once in a calendar.txt file. This value is dataset unique. It is referenced by the trips.txt
     * file.
     */
    public String getServiceId() {
        return this.serviceId;
    }

    /**
     * monday Required The monday field contains a binary value that indicates whether the service
     * is valid for all Mondays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Mondays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Mondays in the date range.
     * </pre>
     *
     * Note: You may list exceptions for particular dates, such as holidays, in the
     * calendar_dates.txt file.
     */
    public boolean isAvailableOnMonday() {
        return this.monday;
    }

    /**
     * tuesday Required The tuesday field contains a binary value that indicates whether the service
     * is valid for all Tuesdays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Tuesdays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Tuesdays in the date range.
     * </pre>
     *
     * Note: You may list exceptions for particular dates, such as holidays, in the
     * calendar_dates.txt file.
     */
    public boolean isAvailableOnTuesday() {
        return this.tuesday;
    }

    /**
     * wednesday Required The wednesday field contains a binary value that indicates whether the
     * service is valid for all Wednesdays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Wednesdays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Wednesdays in the date range.
     * Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
     */
    public boolean isAvailableOnWednesday() {
        return this.wednesday;
    }

    /**
     * thursday Required The thursday field contains a binary value that indicates whether the
     * service is valid for all Thursdays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Thursdays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Thursdays in the date range.
     * Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
     */
    public boolean isAvailableOnThursday() {
        return this.thursday;
    }

    /**
     * friday Required The friday field contains a binary value that indicates whether the service
     * is valid for all Fridays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Fridays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Fridays in the date range.
     * </pre>
     *
     * Note: You may list exceptions for particular dates, such as holidays, in the
     * calendar_dates.txt file
     */
    public boolean isAvailableOnFriday() {
        return this.friday;
    }

    /**
     * saturday Required The saturday field contains a binary value that indicates whether the
     * service is valid for all Saturdays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Saturdays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Saturdays in the date range.
     * </pre>
     *
     * Note: You may list exceptions for particular dates, such as holidays, in the
     * calendar_dates.txt file.
     */
    public boolean isAvailableOnSaturday() {
        return this.saturday;
    }

    /**
     * sunday Required The sunday field contains a binary value that indicates whether the service
     * is valid for all Sundays.
     *
     * <pre>
     *     A value of 1 indicates that service is available for all Sundays in the date range. (The date range is specified using the start_date and end_date fields.)
     *     A value of 0 indicates that service is not available on Sundays in the date range.
     * </pre>
     *
     * Note: You may list exceptions for particular dates, such as holidays, in the
     * calendar_dates.txt file.
     */
    public boolean isAvailableOnSunday() {
        return this.sunday;
    }

    /**
     * start_date Required The start_date field contains the start date for the service. The
     * start_date field's value should be in YYYYMMDD format.
     */
    public DateTime getStartDate() {
        return this.startDate;
    }

    /**
     * end_date Required The end_date field contains the end date for the service. This date is
     * included in the service interval. The end_date field's value should be in YYYYMMDD format.
     */
    public DateTime getEndDate() {
        return this.endDate;
    }

}
