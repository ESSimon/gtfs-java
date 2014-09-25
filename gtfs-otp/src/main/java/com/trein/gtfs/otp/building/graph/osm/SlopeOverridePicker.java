package com.trein.gtfs.otp.building.graph.osm;

import com.trein.gtfs.otp.building.graph.osm.model.OSMSpecifier;

public class SlopeOverridePicker {
    private OSMSpecifier specifier;
    
    private boolean override;
    
    public SlopeOverridePicker() {
    }
    
    public SlopeOverridePicker(OSMSpecifier specifier, boolean override) {
        this.specifier = specifier;
        this.override = override;
    }
    
    public void setSpecifier(OSMSpecifier specifier) {
        this.specifier = specifier;
    }
    
    public OSMSpecifier getSpecifier() {
        return this.specifier;
    }
    
    public void setOverride(boolean override) {
        this.override = override;
    }
    
    public boolean getOverride() {
        return this.override;
    }
    
}
