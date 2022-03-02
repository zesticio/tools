package com.zestic.common.inspection.inspect;

import com.zestic.common.inspection.InspectionHelper;

/**
 * An inspector that checks if a class is implementing a given interface and
 * collects it.
 *
 * @param <T>
 * @author hoersch
 */
public class ClassesImplementing<T> extends ClassesMatching<T> {

    private final Class<T> iface;

    /**
     * @param iface
     */
    public ClassesImplementing(Class<T> iface) {
        this.iface = iface;
    }

    @Override
    protected boolean isMatch(InspectionHelper.ClassInfo potentialMatch) {
        for (String implementedInterface : potentialMatch.getInterfaces()) {
            if (implementedInterface.equals(iface.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "classes implementing " + iface.getSimpleName();
    }
}
