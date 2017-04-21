package com.trein.gtfs.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareDto {
    private long id;
    private String fareId;

    public FareDto(String fareId) {
        this.fareId = fareId;
    }

    public long getId() {
        return this.id;
    }

    public String getFareId() {
        return this.fareId;
    }
}
