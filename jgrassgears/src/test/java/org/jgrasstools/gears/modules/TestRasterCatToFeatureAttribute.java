/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgrasstools.gears.modules;

import java.util.HashMap;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.jgrasstools.gears.libs.monitor.PrintStreamProgressMonitor;
import org.jgrasstools.gears.modules.v.rastercattofeatureattribute.RasterCatToFeatureAttribute;
import org.jgrasstools.gears.utils.HMTestCase;
import org.jgrasstools.gears.utils.HMTestMaps;
import org.jgrasstools.gears.utils.coverage.CoverageUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TestRasterCatToFeatureAttribute extends HMTestCase {
    public void testMapcalc() throws Exception {

        double[][] elevationData = HMTestMaps.outPitData;
        HashMap<String, Double> envelopeParams = HMTestMaps.envelopeParams;
        CoordinateReferenceSystem crs = HMTestMaps.crs;
        GridCoverage2D elevationCoverage = CoverageUtilities.buildCoverage("elevation",
                elevationData, envelopeParams, crs, true);

        FeatureCollection<SimpleFeatureType, SimpleFeature> inFC = HMTestMaps.testFC;

        PrintStreamProgressMonitor pm = new PrintStreamProgressMonitor(System.out, System.err);

        RasterCatToFeatureAttribute rc2fa = new RasterCatToFeatureAttribute();
        rc2fa.pm = pm;
        rc2fa.inCoverage = elevationCoverage;
        rc2fa.inFC = inFC;
        rc2fa.fNew = "elev";
        rc2fa.process();

        FeatureCollection<SimpleFeatureType, SimpleFeature> outMap = rc2fa.outGeodata;

        FeatureIterator<SimpleFeature> features = outMap.features();
        while( features.hasNext() ) {
            SimpleFeature feature = features.next();
            Object attribute = feature.getAttribute("elev");
            double value = ((Number) attribute).doubleValue();

            Object catObj = feature.getAttribute("cat");
            int cat = ((Number) catObj).intValue();
            if (cat == 1) {
                assertEquals(800.0, value, 0.000001);
            } else if (cat == 2) {
                assertEquals(1500.0, value, 0.000001);
            } else if (cat == 3) {
                assertEquals(700.0, value, 0.000001);
            }
        }

    }

}