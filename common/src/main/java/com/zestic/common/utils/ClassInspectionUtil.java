/*
 * Version:  1.0.0
 *
 * Authors:  Kumar <Deebendu Kumar>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zestic.common.utils;

import com.google.common.base.Function;
import com.zestic.common.inspection.ClassAnnotationMetadata;
import com.zestic.common.inspection.ClassInspector;
import com.zestic.common.inspection.collect.ClassCollector;
import com.zestic.common.inspection.inspect.AnnotatedElementsAnnotatedWith;
import com.zestic.common.inspection.inspect.ClassesAnnotatedWith;
import com.zestic.common.inspection.inspect.ClassesAssignableFrom;
import com.zestic.common.inspection.inspect.ClassesImplementing;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Utilities to inspect and discover classes (elements) that match a specific
 * criteria.
 */
public class ClassInspectionUtil {

    public static <T, CI extends ClassInspector<T>> Collection<T> findElements(CI inspector, String packageName) {
        return new ClassCollector<T, CI>(inspector, packageName).findAndLetInspect().getElements();
    }

    public static <T> Collection<Class<? extends T>> findClassesAssignableFrom(Class<T> clazz, String packageName) {
        return findElements(new ClassesAssignableFrom<>(clazz), packageName);
    }

    public static <T> Collection<Class<? extends T>> findClassesImplementing(Class<T> iface, String packageName) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException("'" + iface + "' is no Interface!");
        }
        return findElements(new ClassesImplementing<>(iface), packageName);
    }

    public static Collection<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation, String packageName) {
        return findElements(new ClassesAnnotatedWith(annotation), packageName);
    }

    public static Collection<ClassAnnotationMetadata> findAnnotatedElements(Class<? extends Annotation> annotation, String packageName) {
        return findElements(new AnnotatedElementsAnnotatedWith(annotation), packageName);
    }

    public static Function<Class<?>, String> classToName() {
        return ToClassName.INSTANCE;
    }

    private enum ToClassName implements Function<Class<?>, String> {
        INSTANCE;

        @Override
        public String apply(Class<?> input) {
            return input.getName();
        }
    }
}
