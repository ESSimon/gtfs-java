package com.trein.gtfs.jpa.entity;

import com.everysens.rtls.commons.entity.RtlsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "gtfs_fares")
//@Cache(region = "entity", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Fare extends RtlsEntity<Fare> {
    @Column(name = "o_fare_id", nullable = false)
    private String fareId;

    @Override
    protected Fare me() {
        return this;
    }

}
