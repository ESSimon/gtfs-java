package com.trein.gtfs.jpa.entity;

import com.everysens.rtls.commons.util.geometry.GeometryUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    public static Location from(Point point) {
        return Location.builder().latitude(point.getY()).longitude(point.getX()).build();
    }

    /**
     * stop_lat Required The stop_lat field contains the latitude of a stop or station. The field
     * value must be a valid WGS 84 latitude.
     *
     * @return latitude coordinate for this location.
     */
    public double getLat() {
        return this.latitude;
    }

    /**
     * stop_lon Required The stop_lon field contains the longitude of a stop or station. The field
     * value must be a valid WGS 84 longitude value from -180 to 180.
     *
     * @return longitude coordinate for this location.
     */
    public double getLng() {
        return this.longitude;
    }

    public Point toPoint() {
        return GeometryUtil.getPoint(longitude, latitude);
    }

    public Coordinate toCoordinate() {
        return new Coordinate(longitude, latitude);
    }
}
