package com.zestic.common.inspection.inspect;

import com.google.common.collect.Sets;
import com.zestic.common.inspection.ClassInspector;
import com.zestic.common.inspection.InspectionHelper;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * An inspector that checks if a class is annotated with a specific annotation and if so collects it.
 *
 * @author hoersch
 */
public class ClassesAnnotatedWith implements ClassInspector<Class<?>> {
    private static final Logger logger = Logger.getLogger(ClassesAnnotatedWith.class);

    private Class<? extends Annotation> annotation;

    private Set<Class<?>> _matches = Sets.newHashSet();

    /**
     * @param annotation
     */
    public ClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    @Override
    public void inspect(ClassFile type, InspectionHelper helper) {
        logger.trace("Checking to see if class " + type.getName() + " matches criteria [" + toString() + "]");
        if (isAnnotationPresent(type, annotation)) {
            try {
                _matches.add(helper.loadClass(type));
            } catch (ClassNotFoundException t) {
                logger.warn("Could not load class '" + type.getName() + "'.", t);
            }
        }
    }

    private static boolean isAnnotationPresent(ClassFile type, Class<? extends Annotation> annotation) {
        AnnotationsAttribute visible = (AnnotationsAttribute) type.getAttribute(AnnotationsAttribute.visibleTag);
        if (visible == null) {
            return false;
        }
        for (javassist.bytecode.annotation.Annotation ann : visible.getAnnotations()) {
            //System.out.println("@" + ann.getTypeName());
            if (ann.getTypeName().equals(annotation.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Class<?>> getElements() {
        return _matches;
    }

    @Override
    public String toString() {
        return "annotated with @" + annotation.getSimpleName();
    }
}
