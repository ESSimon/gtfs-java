package org.opentripplanner.standalone;

import org.onebusaway.gtfs.services.GtfsRelationalDao;

public interface FareServiceFactory {
    public FareService makeFareService();
    
    public void setDao(GtfsRelationalDao dao);
    
}
