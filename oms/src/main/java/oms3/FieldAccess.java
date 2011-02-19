/*
 * $Id: FieldAccess.java 20 2008-07-25 22:31:07Z od $
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *  3. This notice may not be removed or altered from any source
 *     distribution.
 */
package oms3;

import java.lang.reflect.Field;
import java.util.logging.Logger;
import oms3.Notification.DataflowEvent;
//import oms3.gen.Access;

/** Field Access.
 * 
 * @author Olaf David (olaf.david@ars.usda.gov)
 * @version $Id: FieldAccess.java 20 2008-07-25 22:31:07Z od $ 
 */


class FieldAccess implements Access {

    Notification ens;
    Field field;
    Object comp;
    FieldContent data;

    private static final Logger log = Logger.getLogger("oms3.sim");

//    Access access;

    FieldAccess(Object target, Field field, Notification ens) {
        this.field = field;
        this.comp = target;
        this.ens = ens;
        field.setAccessible(true);   // just in case
//        access = Utils.compiled(comp, field);
    }

    // called on 'out' access.
    @Override
    public FieldContent getData() {
        if (data == null) {
            data = new FieldContent();
        }
        return data;
    }

    // called in 'in' access
    @Override
    public void setData(FieldContent data) {
        // allow setting in field once only.
        // cannot have multiple sources for one @In !
        if (this.data != null) {
            throw new RuntimeException("Attempt to set @In field twice: " + comp + "." + field.getName());
        }
        this.data = data;
    }

    /**
     * Checks if this object is in a valid state.
     * @return
     */
    @Override
    public boolean isValid() {
        return data != null && field.isAccessible();
    }

    /** 
     * a field is receiving a new value (in)
     * 
     * @throws java.lang.Exception
     */
    @Override
     public void in() throws Exception {
        if (data == null) {
            log.warning("In not connected : " + toString() + ", using default.");
            return;
        }
        Object val = data.getValue();
        // fire only if there is a listener
        if (ens.shouldFire()) {
            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
//            DataflowEvent e = new DataflowEvent(ens.getController(), this, access.toObject());
            ens.fireIn(e);
            // the value might be altered
            val = e.getValue();
        }

//        access.pass((Access) val);
        setFieldValue(val);
    }
    
    /** 
     * a field is sending a new value (out)
     * 
     * @return  the object value in out
     * @throws java.lang.Exception
     */
    @Override
    public void out() throws Exception {
        Object val = getFieldValue();
//        Object val = access;
        
        if (ens.shouldFire()) {
            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
//            DataflowEvent e = new DataflowEvent(ens.getController(), this, access.toObject());
            ens.fireOut(e);
            // the value might be altered
            val = e.getValue();
        }
        // if data==null this unconsumed @Out, its OK but we do not want to set it.
        if (data != null) {
           data.setValue(val);
        }
    }

    /** Get the command belonging to this Object
     *
     * @return the command object
     */
    @Override
    public Object getComponent() {
        return comp;
    }

    /**
     * Get the Field
     * @return the field object.
     */
    @Override
    public Field getField() {
        return field;
    }

    /**
     * Get directly the value of the component field.
     * 
     * @return the field value object
     * @throws Exception if there is illegal access ot type
     */
    @Override
    final public Object getFieldValue() throws Exception {
        return field.get(comp);
    }

    /**
     * Set directly the value of a component field.
     * 
     * @param o the new value to set
     * @throws Exception if there is illegal access ot type
     */
    @Override
    final public void setFieldValue(Object o) throws Exception {
//        System.out.println("Set field " + o + " at " + comp + " " + field);
//        if (field.getName().equals("basin_soil_moist") && o == null) {
//            o = new Double(0.0);
//        }
        field.set(comp, o);
    }

    @Override
    public String toString() {
        return comp + "%" + field.getName() + " - " + data;
    }
}
