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
import oms3.Notification.DataflowEvent;
//import oms3.gen.Access;

/** Field Access.
 * 
 * @author Olaf David (olaf.david@ars.usda.gov)
 * @version $Id: FieldAccess.java 20 2008-07-25 22:31:07Z od $ 
 */
class AsyncFieldAccess implements Access {

    Access fa;

//    Access access;
    AsyncFieldAccess(Access fa) {
        this.fa = fa;
    }

    /**
     * Checks if this object is in a valid state.
     * @return
     */
    @Override
    public boolean isValid() {
        return fa.isValid();
    }

//    boolean canConnect(FieldObjectAccess other) {
//        return other.field.getType().isAssignableFrom(field.getType());
//    }
    /** 
     * a field is receiving a new value (in)
     * 
     * @throws java.lang.Exception
     */
    @Override
    public void in() throws Exception {
        if (fa.getData() == null) {
            throw new RuntimeException("Not connected: " + toString());
        }
        // async call
//        Object val = fa.getData().getValue0();
        Object val = fa.getData().getShadow();

        // fire only if there is a listener
//        if (ens.shouldFire()) {
//            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
////            DataflowEvent e = new DataflowEvent(ens.getController(), this, access.toObject());
//            ens.fireIn(e);
//            // the value might be altered
//            val = e.getValue();
//        }

//        access.pass((Access) val);
        fa.setFieldValue(val);
    }

    /** 
     * a field is sending a new value (out)
     * 
     * @throws java.lang.Exception
     */
    @Override
    public void out() throws Exception {
        Object val = fa.getFieldValue();
//        Object val = access;

//        if (ens.shouldFire()) {
//            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
////            DataflowEvent e = new DataflowEvent(ens.getController(), this, access.toObject());
//            ens.fireOut(e);
//            // the value might be altered
//            val = e.getValue();
//        }
        // if data==null this unconsumed @Out, its OK but we do not want to set it.
        if (fa.getData() != null) {
            fa.getData().setValue0(val);
        }
        fa.out();
    }

    /** Get the command belonging to this Object
     *
     * @return the command object
     */
    @Override
    public Object getComponent() {
        return fa.getComponent();
    }

    /**
     * Get the Field
     * @return the field object.
     */
    @Override
    public Field getField() {
        return fa.getField();
    }

    @Override
    public String toString() {
        return "AsyncFieldAccess [" + fa.toString() + "]";
    }

     @Override
    public Object getFieldValue() throws Exception {
        return fa.getFieldValue();
    }

    @Override
    public void setFieldValue(Object o) throws Exception {
        fa.setFieldValue(o);
    }

    @Override
    public FieldContent getData() {
        return fa.getData();
    }

    @Override
    public void setData(FieldContent data) {
        fa.setData(data);
    }
}
