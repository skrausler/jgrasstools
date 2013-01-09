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
package org.jgrasstools.modules;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_AUTHORCONTACTS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_AUTHORNAMES;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_DOCUMENTATION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_KEYWORDS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_LABEL;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_LICENSE;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_NAME;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_STATUS;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_inPlan_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_inTca_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_outAb_DESCRIPTION;
import static org.jgrasstools.hortonmachine.i18n.HortonMessages.OMSAB_outB_DESCRIPTION;
import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Documentation;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;
import oms3.annotations.UI;

import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.hortonmachine.modules.geomorphology.ab.OmsAb;

@Description(OMSAB_DESCRIPTION)
@Documentation(OMSAB_DOCUMENTATION)
@Author(name = OMSAB_AUTHORNAMES, contact = OMSAB_AUTHORCONTACTS)
@Keywords(OMSAB_KEYWORDS)
@Label(OMSAB_LABEL)
@Name(OMSAB_NAME)
@Status(OMSAB_STATUS)
@License(OMSAB_LICENSE)
public class Ab extends OmsAb {
    @Description(OMSAB_inTca_DESCRIPTION)
    @UI(JGTConstants.FILEIN_UI_HINT)
    @In
    public String inTca = null;

    @Description(OMSAB_inPlan_DESCRIPTION)
    @UI(JGTConstants.FILEIN_UI_HINT)
    @In
    public String inPlan = null;

    @Description(OMSAB_outAb_DESCRIPTION)
    @UI(JGTConstants.FILEOUT_UI_HINT)
    @In
    public String outAb = null;

    @Description(OMSAB_outB_DESCRIPTION)
    @UI(JGTConstants.FILEOUT_UI_HINT)
    @In
    public String outB = null;

    @Execute
    public void process() throws Exception {
        OmsAb ab = new OmsAb();
        ab.inTca = getRaster(inTca);
        ab.inPlan = getRaster(inPlan);
        ab.pm = pm;
        ab.process();
        dumpRaster(ab.outAb, outAb);
        dumpRaster(ab.outB, outB);
    }

}
