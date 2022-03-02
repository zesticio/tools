package com.zestic.common.inspection;

import javassist.bytecode.ClassFile;

import java.util.Collection;

/**
 * Interface of a class inspector. Inspects a class file and may collect elements that matches a specific criteria.
 * @param <T>
 */
public interface ClassInspector<T> {

    /**
     * Will be called repeatedly with candidate classes.
     * @param type
     * @param helper
     */
    void inspect(ClassFile type, InspectionHelper helper);

    /**
     *
     * @return the collected elements
     */
    Collection<T> getElements();
}