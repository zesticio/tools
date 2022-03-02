package com.zestic.common.inspection.inspect;

import com.zestic.common.inspection.InspectionHelper;

/**
 * An inspector that checks if a class is assignable from a given class and if
 * so collects it.
 *
 * @param <T>
 * @author hoersch
 */
public class ClassesAssignableFrom<T> extends ClassesMatching<T> {

    private final Class<T> clazz;

    /**
     * @param clazz
     */
    public ClassesAssignableFrom(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected boolean isMatch(InspectionHelper.ClassInfo potentialMatch) {
        return clazz.equals(Object.class) || potentialMatch.getName().equals(clazz.getName()) || potentialMatch.getSuperclass().equals(clazz.getName());
    }

    @Override
    public String toString() {
        return "classes assignable from " + clazz.getSimpleName();
    }
}
