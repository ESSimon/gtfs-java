package com.trein.gtfs.dto.entity;

import com.everysens.rtls.commons.dto.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareDto implements Identifiable {
    private Long id;
    private String fareId;
}
