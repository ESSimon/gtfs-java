package com.trein.gtfs.dto.entity;

import com.everysens.rtls.commons.dto.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Exceptions for the service IDs defined in the calendar.txt file. If calendar_dates.txt includes
 * ALL dates of service, this file may be specified instead of calendar.txt.<br>
 * <br>
 * The calendar_dates table allows you to explicitly activate or disable service IDs by date. You
 * can use it in two ways. Recommended: Use calendar_dates.txt in conjunction with calendar.txt,
 * where calendar_dates.txt defines any exceptions to the default service categories defined in the
 * calendar.txt file. If your service is generally regular, with a few changes on explicit dates
 * (for example, to accomodate special event services, or a school schedule), this is a good
 * approach. Alternate: Omit calendar.txt, and include ALL dates of service in calendar_dates.txt.
 * If your schedule varies most days of the month, or you want to programmatically output service
 * dates without specifying a normal weekly schedule, this approach may be preferable.
 *
 * @author trein
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDateDto implements Identifiable {
    private Long id;
    private String serviceId;
    private DateTime date;
    private ExceptionType exceptionType;

    /**
     * service_id Required The service_id contains an ID that uniquely identifies a set of dates
     * when a service exception is available for one or more routes. Each (service_id, date) pair
     * can only appear once in calendar_dates.txt. If the a service_id value appears in both the
     * calendar.txt and calendar_dates.txt files, the information in calendar_dates.txt modifies the
     * service information specified in calendar.txt. This field is referenced by the trips.txt
     * file.
     *
     * @return service's id related to current calendar date.
     */
    public String getServiceId() {
        return this.serviceId;
    }

    /**
     * date Required The date field specifies a particular date when service availability is
     * different than the norm. You can use the exception_type field to indicate whether service is
     * available on the specified date. The date field's value should be in YYYYMMDD format.
     *
     * @return date identifying current entry.
     */
    public DateTime getDate() {
        return this.date;
    }

    /**
     * exception_type Required The exception_type indicates whether service is available on the date
     * specified in the date field.
     *
     * @return service exception's type for current date.
     * @see ExceptionType refer to documentation for more clarification.
     */
    public ExceptionType getExceptionType() {
        return this.exceptionType;
    }
}
