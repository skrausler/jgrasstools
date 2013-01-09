/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jgrasstools.gears.modules.r.mapcalc;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.UI;

import org.geotools.coverage.grid.GridCoverage2D;
import org.jaitools.imageutils.ImageUtils;
import org.jaitools.jiffle.Jiffle;
import org.jaitools.jiffle.runtime.AffineCoordinateTransform;
import org.jaitools.jiffle.runtime.CoordinateTransform;
import org.jaitools.jiffle.runtime.JiffleDirectRuntime;
import org.jaitools.jiffle.runtime.JiffleExecutor;
import org.jaitools.jiffle.runtime.JiffleExecutorResult;
import org.jaitools.jiffle.runtime.JiffleProgressListener;
import org.jgrasstools.gears.libs.exceptions.ModelsIllegalargumentException;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.jgrasstools.gears.utils.coverage.CoverageUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

@Description("Module for doing raster map algebra.")
@Documentation("OmsMapcalc.html")
@Author(name = "Andrea Antonello", contact = "http://www.hydrologis.com")
@Keywords("OmsMapcalc, Raster, OmsCutOut")
@Label(JGTConstants.RASTERPROCESSING)
@Name("mapcalc")
@Status(Status.CERTIFIED)
@License("General Public License Version 3 (GPLv3)")
public class OmsMapcalc extends JGTModel {

    @Description("The maps that are used in the calculation.")
    @In
    public List<GridCoverage2D> inRasters;

    @Description("The function to process.")
    @UI(JGTConstants.MULTILINE_UI_HINT + "10," + JGTConstants.MAPCALC_UI_HINT)
    @In
    public String pFunction;

    @Description("The resulting map picked from the inserted function.")
    @Out
    public GridCoverage2D outRaster = null;

    private HashMap<String, Double> regionParameters = null;

    private CoordinateReferenceSystem crs;

    private Rectangle2D worldBounds;

    private long updateInterval;
    private long totalCount = 100;

    @SuppressWarnings("nls")
    @Execute
    public void process() throws Exception {
        if (!concatOr(outRaster == null, doReset)) {
            return;
        }

        String script = pFunction;
        script = script.trim();

        Jiffle jiffle = new Jiffle();
        jiffle.setScript(script);
        jiffle.compile();
        JiffleDirectRuntime jiffleRuntime = jiffle.getRuntimeInstance();

        CoordinateTransform jiffleCRS = null;

        // gather maps
        for( GridCoverage2D mapGC : inRasters ) {
            if (regionParameters == null) {
                regionParameters = CoverageUtilities.getRegionParamsFromGridCoverage(mapGC);
                crs = mapGC.getCoordinateReferenceSystem();

                worldBounds = mapGC.getEnvelope2D().getBounds2D();
                Rectangle gridBounds = mapGC.getGridGeometry().getGridRange2D().getBounds();

                jiffleCRS = getTransform(worldBounds, gridBounds);

                double xRes = regionParameters.get(CoverageUtilities.XRES).doubleValue();
                double yRes = regionParameters.get(CoverageUtilities.YRES).doubleValue();
                jiffleRuntime.setWorldByResolution(worldBounds, xRes, yRes);
            }
            RenderedImage renderedImage = mapGC.getRenderedImage();
            // add map
            String name = mapGC.getName().toString();
            jiffleRuntime.setSourceImage(name, renderedImage, jiffleCRS);
        }
        if (regionParameters == null) {
            throw new ModelsIllegalargumentException("No map has been supplied.", this.getClass().getSimpleName());
        }
        int nCols = regionParameters.get(CoverageUtilities.COLS).intValue();
        int nRows = regionParameters.get(CoverageUtilities.ROWS).intValue();
        long pixelsNum = (long) nCols * nRows;

        if (pixelsNum < totalCount) {
            totalCount = pixelsNum;
        }
        updateInterval = pixelsNum / totalCount;

        String destName = jiffleRuntime.getDestinationVarNames()[0];
        WritableRenderedImage destImg = ImageUtils.createConstantImage(nCols, nRows, 0d);
        jiffleRuntime.setDestinationImage(destName, destImg, jiffleCRS);

        // create the executor
        JiffleExecutor executor = new JiffleExecutor();
        WaitingListener listener = new WaitingListener();
        executor.addEventListener(listener);
        listener.setNumTasks(1);

        executor.submit(jiffleRuntime, new JiffleProgressListener(){
            private long count = 0;
            public void update( long done ) {
                if (count == done) {
                    pm.worked(1);
                    count = count + updateInterval;
                }
            }

            public void start() {
                pm.beginTask("Processing maps...", (int) totalCount);
            }

            public void setUpdateInterval( double propPixels ) {
            }

            public void setUpdateInterval( long numPixels ) {
            }

            public void setTaskSize( long numPixels ) {
                count = updateInterval;
            }

            public long getUpdateInterval() {
                if (updateInterval == 0) {
                    return 1;
                }
                return updateInterval;
            }

            public void finish() {
                pm.done();
            }
        });
        listener.await();

        JiffleExecutorResult result = listener.getResults().get(0);
        Map<String, RenderedImage> imgMap = result.getImages();
        RenderedImage destImage = imgMap.get(destName);
        outRaster = CoverageUtilities.buildCoverage(destName, destImage, regionParameters, crs);
        executor.shutdown();
    }

    private static CoordinateTransform getTransform( Rectangle2D worldBounds, Rectangle imageBounds ) {
        if (worldBounds == null || worldBounds.isEmpty()) {
            throw new IllegalArgumentException("worldBounds must not be null or empty");
        }
        if (imageBounds == null || imageBounds.isEmpty()) {
            throw new IllegalArgumentException("imageBounds must not be null or empty");
        }

        double xscale = (imageBounds.getMaxX() - imageBounds.getMinX()) / (worldBounds.getMaxX() - worldBounds.getMinX());

        double xoff = imageBounds.getMinX() - xscale * worldBounds.getMinX();

        double yscale = (imageBounds.getMaxY() - imageBounds.getMinY()) / (worldBounds.getMaxY() - worldBounds.getMinY());

        double yoff = imageBounds.getMinY() - yscale * worldBounds.getMinY();

        return new AffineCoordinateTransform(new AffineTransform(xscale, 0, 0, yscale, xoff, yoff));
    }

}