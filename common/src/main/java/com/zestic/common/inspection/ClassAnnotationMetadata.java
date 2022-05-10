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
