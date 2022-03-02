package com.zestic.common.inspection.inspect;

import com.google.common.collect.Sets;
import com.zestic.common.inspection.ClassInspector;
import com.zestic.common.inspection.InspectionHelper;
import javassist.bytecode.ClassFile;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * An inspector that checks if a class is matching a condition and if so collects it.
 *
 * @param <T>
 * @author hoersch
 */
abstract class ClassesMatching<T> implements ClassInspector<Class<? extends T>> {
    private static final Logger logger = Logger.getLogger(ClassesMatching.class);

    private final Set<Class<? extends T>> _matches = Sets.newHashSet();

    protected ClassesMatching() {
    }

    @Override
    public final void inspect(ClassFile type, InspectionHelper helper) {
        logger.trace("Checking to see if class " + type.getName() + " matches criteria [" + toString() + "]");
        try {
            InspectionHelper.ClassInfo classInfo = helper.toClassInfo(type);
            for (; ; ) {
                if (isMatch(classInfo)) {
                    @SuppressWarnings("unchecked")
                    Class<T> loadedClass = (Class<T>) helper.loadClass(type);
                    _matches.add(loadedClass);
                    return;
                }

                if (classInfo.getSuperclass().equals(Object.class.getName())) {
                    return;
                }
                classInfo = helper.getClassInfo(classInfo.getSuperclass());
            }
        } catch (ClassNotFoundException e) {
            logger.warn("Could not load class '" + type.getName() + "'.", e);
        } catch (IOException e) {
            logger.warn("Could not load class file.", e);
        }
    }

    protected abstract boolean isMatch(InspectionHelper.ClassInfo potentialMatch);

    @Override
    public final Collection<Class<? extends T>> getElements() {
        return _matches;
    }

    @Override
    public abstract String toString();
}
