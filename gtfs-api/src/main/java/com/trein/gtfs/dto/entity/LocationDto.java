package com.trein.gtfs.dto.entity;

public class LocationDto {
    private double latitude;
    private double longitude;
    
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

}
