package com.zestic.common.inspection;

import javassist.bytecode.ClassFile;

import java.io.IOException;

/**
 * Helper that is passed to class inspectors when called to inspect a ClassFile.
 */
public interface InspectionHelper {

    /**
     * Loads a class.
     *
     * @param type
     * @return the class
     * @throws ClassNotFoundException
     */
    Class<?> loadClass(ClassFile type) throws ClassNotFoundException;

    /**
     * Gets a ClassInfo of a named class.
     *
     * @param name
     * @return class info
     * @throws IOException
     * @throws ClassNotFoundException
     */
    ClassInfo getClassInfo(String name) throws IOException, ClassNotFoundException;

    /**
     * Converts the given ClassFile to a ClassInfo object.
     *
     * @param type
     * @return a class info object
     */
    ClassInfo toClassInfo(ClassFile type);

    /**
     * Simple wrapper of classes, may be backed by real classes or just
     * {@link ClassFile}.
     */
    public interface ClassInfo {
        /**
         * @return name of the class
         */
        public String getName();

        /**
         * @return name of the super class
         */
        public String getSuperclass();

        /**
         * @return all interfaces of this class
         */
        public String[] getInterfaces();
    }

}
