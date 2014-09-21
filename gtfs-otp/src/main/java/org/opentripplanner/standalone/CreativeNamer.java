package org.opentripplanner.standalone;

/**
 * A CreativeNamer makes up names for ways that don't have one in the OSM data set. It does this by
 * substituting the values of OSM tags into a template.
 */
public class CreativeNamer {
    
    /**
     * A creative name pattern is a template which may contain variables of the form {{tag_name}}.
     * When a way's creative name is created, the value of its tag tag_name is substituted for the
     * variable. For example, "Highway with surface {{surface}}" might become
     * "Highway with surface gravel"
     */
    private String creativeNamePattern;
    
    public CreativeNamer(String pattern) {
        this.creativeNamePattern = pattern;
    }
    
    public CreativeNamer() {
    }
    
    public String generateCreativeName(OSMWithTags way) {
        return TemplateLibrary.generate(this.creativeNamePattern, way);
    }
    
    public void setCreativeNamePattern(String creativeNamePattern) {
        this.creativeNamePattern = creativeNamePattern;
    }
    
    public String getCreativeNamePattern() {
        return this.creativeNamePattern;
    }
    
}
