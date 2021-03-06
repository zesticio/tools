/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zestic.dispatch.jmx;

import javax.management.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * MBean that looks for method/parameter descriptions in the Info annotation.
 */
public class AnnotatedMBean extends StandardMBean {

    private static final Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();

    static {
        Class<?>[] p = { byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class, };
        for (Class<?> c : p) {
            primitives.put(c.getName(), c);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static AnnotatedMBean create(Object object) throws Exception {
        String mbeanName = object.getClass().getName() + "MBean";
        for (Class c : object.getClass().getInterfaces()) {
            if (mbeanName.equals(c.getName())) {
                return new AnnotatedMBean(object, c);
            }
        }
        throw new IllegalArgumentException(object.getClass().getName()+" does not implement a "+object.getClass().getName()+"MBean interface");
    }

    /* Instance where the MBean interface is implemented by another object. */
    public <T> AnnotatedMBean(T impl, Class<T> mbeanInterface) throws NotCompliantMBeanException {
        super(impl, mbeanInterface);
    }

    /* Instance where the MBean interface is implemented by this object. */
    protected AnnotatedMBean(Class<?> mbeanInterface) throws NotCompliantMBeanException {
        super(mbeanInterface);
    }

    /* {@inheritDoc} */
    @Override
    protected String getDescription(MBeanAttributeInfo info) {

        String descr = info.getDescription();
        Method m = getMethod(getMBeanInterface(), "get" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1));
        if (m == null)
            m = getMethod(getMBeanInterface(), "is" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1));
        if (m == null)
            m = getMethod(getMBeanInterface(), "does" + info.getName().substring(0, 1).toUpperCase() + info.getName().substring(1));

        if (m != null) {
            com.zestic.dispatch.jmx.MBeanInfo d = m.getAnnotation(com.zestic.dispatch.jmx.MBeanInfo.class);
            if (d != null)
                descr = d.value();
        }
        return descr;
    }

    /* {@inheritDoc} */
    @Override
    protected String getDescription(MBeanOperationInfo op) {

        String descr = op.getDescription();
        Method m = getMethod(op);
        if (m != null) {
            com.zestic.dispatch.jmx.MBeanInfo d = m.getAnnotation(com.zestic.dispatch.jmx.MBeanInfo.class);
            if (d != null)
                descr = d.value();
        }
        return descr;
    }

    /* {@inheritDoc} */
    @Override
    protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int paramNo) {
        String name = param.getName();
        Method m = getMethod(op);
        if (m != null) {
            for (Annotation a : m.getParameterAnnotations()[paramNo]) {
                if (com.zestic.dispatch.jmx.MBeanInfo.class.isInstance(a))
                    name = MBeanInfo.class.cast(a).value();
            }
        }
        return name;
    }

    /*
     * Extracts the Method from the MBeanOperationInfo
     *
     * @param op
     * @return
     */
    private Method getMethod(MBeanOperationInfo op) {
        final MBeanParameterInfo[] params = op.getSignature();
        final String[] paramTypes = new String[params.length];
        for (int i = 0; i < params.length; i++)
            paramTypes[i] = params[i].getType();

        return getMethod(getMBeanInterface(), op.getName(), paramTypes);
    }

    /*
     * Returns the Method with the specified name and parameter types for the
     * given class, null if it doesn't exist.
     *
     * @param mbean
     * @param method
     * @param params
     * @return
     */
    private static Method getMethod(Class<?> mbean, String method, String... params) {
        try {
            final ClassLoader loader = mbean.getClassLoader();
            final Class<?>[] paramClasses = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = primitives.get(params[i]);
                if (paramClasses[i] == null)
                    paramClasses[i] = Class.forName(params[i], false, loader);
            }
            return mbean.getMethod(method, paramClasses);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }
}
