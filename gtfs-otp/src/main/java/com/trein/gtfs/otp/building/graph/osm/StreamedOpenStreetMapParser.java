package com.trein.gtfs.otp.building.graph.osm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.trein.gtfs.otp.building.graph.OpenStreetMapContentHandler;
import com.trein.gtfs.otp.building.graph.osm.model.OSMNode;
import com.trein.gtfs.otp.building.graph.osm.model.OSMNodeRef;
import com.trein.gtfs.otp.building.graph.osm.model.OSMRelation;
import com.trein.gtfs.otp.building.graph.osm.model.OSMRelationMember;
import com.trein.gtfs.otp.building.graph.osm.model.OSMTag;
import com.trein.gtfs.otp.building.graph.osm.model.OSMWay;

/**
 * @author Vincent Privat
 * @since 1.0
 */
public class StreamedOpenStreetMapParser {
    
    private static final QName qNode = new QName("node");
    private static final QName qWay = new QName("way");
    private static final QName qRelation = new QName("relation");
    private static final QName qNd = new QName("nd");
    private static final QName qMember = new QName("member");
    private static final QName qTag = new QName("tag");
    
    private static final QName qId = new QName("id");
    private static final QName qLat = new QName("lat");
    private static final QName qLon = new QName("lon");
    private static final QName qRef = new QName("ref");
    private static final QName qKey = new QName("k");
    private static final QName qVal = new QName("v");
    private static final QName qType = new QName("type");
    private static final QName qRole = new QName("role");
    
    public static void parseMap(final File path, OpenStreetMapContentHandler map) throws IOException, XMLStreamException {
        
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
        parseMap(in, map, 1);
        
        map.doneRelations();
        
        in = new BufferedInputStream(new FileInputStream(path));
        parseMap(in, map, 2);
        
        map.secondPhase();
        
        in = new BufferedInputStream(new FileInputStream(path));
        parseMap(in, map, 3);
        
        map.nodesLoaded();
    }
    
    public static void parseMap(final InputStream in, OpenStreetMapContentHandler map, int phase) throws IOException,
    XMLStreamException {
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(in);
        
        OSMRelation osmRelation = null;
        OSMNode osmNode = null;
        OSMWay osmWay = null;
        
        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement element = xmlEvent.asStartElement();
                if ((phase == 3) && element.getName().equals(qNode)) {
                    osmNode = new OSMNode();
                    osmNode.setId(Long.parseLong(element.getAttributeByName(qId).getValue()));
                    osmNode.lat = Double.parseDouble(element.getAttributeByName(qLat).getValue());
                    osmNode.lon = Double.parseDouble(element.getAttributeByName(qLon).getValue());
                    
                } else if ((phase == 2) && element.getName().equals(qWay)) {
                    osmWay = new OSMWay();
                    osmWay.setId(Long.parseLong(element.getAttributeByName(qId).getValue()));
                    
                } else if ((phase == 1) && element.getName().equals(qRelation)) {
                    osmRelation = new OSMRelation();
                    osmRelation.setId(Long.parseLong(element.getAttributeByName(qId).getValue()));
                    
                } else if ((osmRelation != null) && element.getName().equals(qMember)) {
                    OSMRelationMember relMember = new OSMRelationMember();
                    relMember.setType(element.getAttributeByName(qType).getValue());
                    relMember.setRole(element.getAttributeByName(qRole).getValue());
                    relMember.setRef(Long.parseLong(element.getAttributeByName(qRef).getValue()));
                    osmRelation.addMember(relMember);
                    
                } else if ((osmWay != null) && element.getName().equals(qNd)) {
                    OSMNodeRef nodeRef = new OSMNodeRef();
                    nodeRef.setRef(Long.parseLong(element.getAttributeByName(qRef).getValue()));
                    osmWay.addNodeRef(nodeRef);
                    
                } else if (element.getName().equals(qTag)) {
                    OSMTag tag = new OSMTag();
                    String key = element.getAttributeByName(qKey).getValue();
                    tag.setK(key.intern());
                    String value = element.getAttributeByName(qVal).getValue();
                    if (key.equals("name") || key.equals("ref") || key.equals("highway")) {
                        value = value.intern();
                    }
                    tag.setV(value);
                    if (osmNode != null) {
                        osmNode.addTag(tag);
                    } else if (osmWay != null) {
                        osmWay.addTag(tag);
                    } else if (osmRelation != null) {
                        osmRelation.addTag(tag);
                    }
                }
                
            } else if (xmlEvent.isEndElement()) {
                EndElement element = xmlEvent.asEndElement();
                if ((osmNode != null) && element.getName().equals(qNode)) {
                    map.addNode(osmNode);
                    osmNode = null;
                } else if ((osmWay != null) && element.getName().equals(qWay)) {
                    map.addWay(osmWay);
                    osmWay = null;
                } else if ((osmRelation != null) && element.getName().equals(qRelation)) {
                    map.addRelation(osmRelation);
                    osmRelation = null;
                }
            }
        }
        
        xmlEventReader.close();
    }
}
