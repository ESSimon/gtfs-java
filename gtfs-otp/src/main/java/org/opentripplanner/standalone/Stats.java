package org.opentripplanner.standalone;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * num may be 0 if there are no observations. num will become 1 when adding a scalar or another
 * Stats.
 */
class Stats implements Cloneable {

    public int min = 0;
    public int avg = 0;
    public int max = 0;
    public int num = 0;
    
    public Stats() {
    }
    
    /** Copy constructor. */
    public Stats(Stats other) {
        if (other != null) {
            this.min = other.min;
            this.avg = other.avg;
            this.max = other.max;
        }
    }
    
    /**
     * Adds another Stats into this one in place. This is intended to combine them in series, as for
     * legs of a journey. It is not really correct for the average, but min and max values hold and
     * avg is still a useful indicator.
     * 
     * @return void to avoid thinking that a new object is created.
     */
    public void add(Stats s) { // TODO maybe should be called 'chain' rather than add
        this.min += s.min;
        this.max += s.max;
        this.avg += s.avg; // This only makes sense when adding successive legs TODO think through
                           // in depth
        this.num = 1; // Num is poorly defined once addition has occurred
    }
    
    /** Like add(Stats) but min, max, and avg are all equal. */
    public void add(int x) {
        this.min += x;
        this.avg += x;
        this.max += x;
        this.num = 1; // it's poorly defined here
    }
    
    /** */
    public void add(Collection<StreetSegment> segs) {
        if ((segs == null) || segs.isEmpty()) { return; }
        List<Integer> times = Lists.newArrayList();
        for (StreetSegment seg : segs) {
            times.add(seg.time);
        }
        Stats s = new Stats(times);
        add(s);
    }
    
    /**
     * Combines another Stats into this one in place. This considers the two Stats to be parallel,
     * as for various trips or patterns making up a single leg of a journey. In this case, the
     * weighted average is correctly computed.
     * 
     * @return void to avoid thinking that a new object is created.
     */
    public void merge(Stats other) {
        if (other.min < this.min) {
            this.min = other.min;
        }
        if (other.max > this.max) {
            this.max = other.max;
        }
        this.avg = ((this.avg * this.num) + (other.avg * other.num)) / (this.num + other.num); // TODO
                                                                                               // should
                                                                                               // be
                                                                                               // float
                                                                                               // math
    }

    /**
     * Build a composite Stats out of a bunch of other Stats. They are combined in parallel, as in
     * merge(Stats).
     */
    public Stats(Iterable<Stats> stats) {
        this.min = Integer.MAX_VALUE;
        this.num = 0;
        for (Stats other : stats) {
            if (other.min < this.min) {
                this.min = other.min;
            }
            if (other.max > this.max) {
                this.max = other.max;
            }
            this.avg += other.avg * other.num;
            this.num += other.num;
        }
        this.avg /= this.num; // TODO should perhaps be float math
    }
    
    /** Construct a Stats containing the min, max, average, and count of the given ints. */
    public Stats(Collection<Integer> ints) {
        if ((ints == null) || ints.isEmpty()) { throw new AssertionError("Stats are undefined if there are no values."); }
        this.min = Integer.MAX_VALUE;
        double accumulated = 0;
        for (int i : ints) {
            if (i > this.max) {
                this.max = i;
            }
            if (i < this.min) {
                this.min = i;
            }
            accumulated += i;
        }
        this.num = ints.size();
        this.avg = (int) (accumulated / this.num);
    }

    public void dump() {
        System.out.printf("min %d avg %d max %d\n", this.min, this.avg, this.max);
    }

    /** Scan through all trips on this pattern and summarize those that are running. */
    public static Stats create(TripPattern pattern, int stop0, int stop1, TimeWindow window) {
        Stats s = new Stats();
        s.min = Integer.MAX_VALUE;
        s.num = 0;
        // TODO maybe we should prefilter the triptimes so we aren't constantly iterating over the
        // trips whose service is not running
        for (TripTimes tripTimes : pattern.scheduledTimetable.tripTimes) {
            int depart = tripTimes.getDepartureTime(stop0);
            int arrive = tripTimes.getArrivalTime(stop1);
            if (window.includes(depart) && window.includes(arrive) && window.servicesRunning.get(tripTimes.serviceCode)) {
                int t = arrive - depart;
                if (t < s.min) {
                    s.min = t;
                }
                if (t > s.max) {
                    s.max = t;
                }
                s.avg += t;
                ++s.num;
            }
        }
        if (s.num > 0) {
            s.avg /= s.num;
            return s;
        }
        /* There are no running trips within the time range, on the given serviceIds. */
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("avg=%.1f min=%.1f max=%.1f", this.avg / 60.0, this.min / 60.0, this.max / 60.0);
    }
}
