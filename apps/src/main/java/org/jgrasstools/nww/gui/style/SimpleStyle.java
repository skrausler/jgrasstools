package org.jgrasstools.nww.gui.style;

import java.awt.Color;

import gov.nasa.worldwind.render.markers.BasicMarkerShape;

public class SimpleStyle {
    public Color fillColor = Color.blue; 
    public double fillOpacity = 0.7;
    
    public Color strokeColor = Color.blue; 
    public double strokeWidth = 3.0; 

    public String shapeType = BasicMarkerShape.SPHERE; 
    public double shapeSize = 3.0; 
}
