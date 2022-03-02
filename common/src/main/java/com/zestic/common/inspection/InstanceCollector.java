package com.zestic.common.inspection;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.zestic.common.utils.ClassInspectionUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;

/**
 * Collecting instances of a given interface. Instances can be Enum constants. Normal classes are just constructed with their defulat constrcutor.
 *
 * @param <T>
 */
public class InstanceCollector<T> {

    private final Logger logger = Logger.getLogger(getClass());
    private final Set<T> _instances;

    public InstanceCollector(Class<T> interfaceClass, String basePackage) {

        Collection<Class<? extends T>> implementations = ClassInspectionUtil.findClassesImplementing(interfaceClass, basePackage);

        ImmutableSet.Builder<T> instances = ImmutableSet.builder();
        for (Class<? extends T> impl : filter(implementations, not(isAnonymous()))) {
            instances.addAll(getInstances(impl));
        }

        _instances = instances.build();
    }

    public Collection<T> instances() {
        return _instances;
    }

    private Collection<T> getInstances(Class<? extends T> clazz) {
        if (clazz.isEnum()) {
            T[] enumConstants = clazz.getEnumConstants();
            return Arrays.asList(enumConstants);
        }
        try {
            T instance = clazz.newInstance();
            return Arrays.asList(instance);
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error("Can not create instance of '" + clazz + "'!", e);
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Predicate<Class<? extends T>> isAnonymous() {
        return (Predicate<Class<? extends T>>) (Predicate<?>) IsAnonymous.INSTANCE;
    }

    private enum IsAnonymous implements Predicate<Class<?>> {
        INSTANCE;

        @Override
        public boolean apply(Class<?> input) {
            return input.isAnonymousClass();
        }
    }
}
