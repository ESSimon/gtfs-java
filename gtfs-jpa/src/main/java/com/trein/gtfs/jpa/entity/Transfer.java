package com.trein.gtfs.jpa.entity;

import com.everysens.rtls.commons.entity.RtlsEntity;
import com.trein.gtfs.dto.entity.TransferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Rules for making connections at transfer points between routes.<br>
 * <br>
 * Trip planners normally calculate transfer points based on the relative proximity of stops in each
 * route. For potentially ambiguous stop pairs, or transfers where you want to specify a particular
 * choice, use transfers.txt to define additional rules for making connections between routes.
 *
 * @author trein
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "gtfs_transfers")
//@Cache(region = "entity", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transfer extends RtlsEntity<Transfer> {
    @ManyToOne
    @JoinColumn(name = "from_stop", nullable = false)
    private Stop fromStop;
    
    @ManyToOne
    @JoinColumn(name = "to_stop", nullable = false)
    private Stop toStop;

    @Column(name = "transfer_type")
    private TransferType transferType;

    @Column(name = "min_transfer")
    private long minTransferTimeSecs;

    @Override
    protected Transfer me() {
        return this;
    }

    /**
     * from_stop_id Required The from_stop_id field contains a stop ID that identifies a stop or
     * station where a connection between routes begins. Stop IDs are referenced from the stops.txt
     * file. If the stop ID refers to a station that contains multiple stops, this transfer rule
     * applies to all stops in that station.
     */
    public Stop getFromStop() {
        return this.fromStop;
    }

    /**
     * to_stop_id Required The to_stop_id field contains a stop ID that identifies a stop or station
     * where a connection between routes ends. Stop IDs are referenced from the stops.txt file. If
     * the stop ID refers to a station that contains multiple stops, this transfer rule applies to
     * all stops in that station.
     */
    public Stop getToStop() {
        return this.toStop;
    }

    /**
     * transfer_type Required The transfer_type field specifies the type of connection for the
     * specified (from_stop_id, to_stop_id) pair. Valid values for this field are:
     *
     * <pre>
     * 0 or (empty) - This is a recommended transfer point between two routes.
     * 1 - This is a timed transfer point between two routes. The departing vehicle is expected
     * to wait for the arriving one, with sufficient time for a passenger to transfer between routes.
     * 2 - This transfer requires a minimum amount of time between arrival and departure to ensure
     * a connection. The time required to transfer is specified by min_transfer_time.
     * 3 - Transfers are not possible between routes at this location.
     * </pre>
     */
    public TransferType getTransferType() {
        return this.transferType;
    }

    /**
     * min_transfer_time Optional When a connection between routes requires an amount of time
     * between arrival and departure (transfer_type=2), the min_transfer_time field defines the
     * amount of time that must be available in an itinerary to permit a transfer between routes at
     * these stops. The min_transfer_time must be sufficient to permit a typical rider to move
     * between the two stops, including buffer time to allow for schedule variance on each route.
     * The min_transfer_time value must be entered in seconds, and must be a non-negative integer.
     */
    public long getMinTransferTimeSecs() {
        return this.minTransferTimeSecs;
    }

}
