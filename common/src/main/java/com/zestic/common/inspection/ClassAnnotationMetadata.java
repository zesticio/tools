package com.zestic.common.inspection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Metadata container of a class who is itself annotated or any of its members.
 */
public interface ClassAnnotationMetadata {

	/**
	 * @return the related class object
	 */
	Class<?> getRelatedClass();

	/**
	 * @return is the related class object annotated itself?
	 */
	boolean isRelatedClassAnnotated();

	/**
	 * @return all annotated fields
	 */
	Collection<Field> getAnnotatedFields();

	/**
	 * @return all annotated methods
	 */
	Collection<Method> getAnnotatedMethods();
}
