package com.zestic.common.inspection.collect;

import com.google.common.collect.Maps;
import com.zestic.common.inspection.InspectionHelper;
import com.zestic.common.utils.ClassInspectionUtil;
import javassist.bytecode.ClassFile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

final class InspectionHelperImpl implements InspectionHelper {
    private final Map<String, ClassInfo> _classFiles = Maps.newHashMap();

    private final ClassLoader _classloader;

    private final Map<String, Entry> _entries = Maps.newLinkedHashMap();

    InspectionHelperImpl(ClassLoader classloader, Map<String, Entry> entries) {
        _classloader = classloader;
        _entries.putAll(entries);
    }

    @Override
    public Class<?> loadClass(ClassFile type) throws ClassNotFoundException {
        return _classloader.loadClass(type.getName());
    }

    @Override
    public ClassInfo getClassInfo(String name) throws IOException, ClassNotFoundException {
        ClassInfo classFile = _classFiles.get(name);
        if (classFile == null) {
            if (name.startsWith("java") || name.startsWith("com.sun")) {
                // java-* packages, they might be in 'rt.jar' and could not be
                // read (?). But there shouldn't be so many so loading them into
                // the JVM won't take too much memory...
                classFile = new NativeClassInfo(_classloader.loadClass(name));
            } else {
                Entry entry = _entries.get(name);
                if (entry == null) {
                    int lastDot = name.lastIndexOf(".");
                    if (lastDot >= 0) {
                        // The class isn't in the inspected package tree. Load
                        // it but don't inspect it!
                        String packageName = name.substring(0, lastDot);

                        _entries.putAll(new ClassFilesCollector(_classloader, packageName).getEntries());
                        entry = _entries.get(name);
                    }
                    if (entry == null) {
                        throw new ClassNotFoundException(name);
                    }
                }
                classFile = new ClassFileClassInfo(toClassFile(entry.getContent()));
            }
            _classFiles.put(name, classFile);
        }
        return classFile;
    }

    @Override
    public ClassInfo toClassInfo(ClassFile type) {
        return new ClassFileClassInfo(type);
    }

    static ClassFile toClassFile(byte[] classContent) throws IOException {
        DataInputStream dstream = new DataInputStream(new ByteArrayInputStream(classContent));
        ClassFile type = new ClassFile(dstream);
        return type;
    }

    private static class NativeClassInfo implements ClassInfo {
        private final Class<?> _clazz;

        NativeClassInfo(Class<?> clazz) {
            this._clazz = clazz;
        }

        @Override
        public String getName() {
            return _clazz.getName();
        }

        @Override
        public String getSuperclass() {
            return _clazz.getSuperclass().getName();
        }

        @Override
        public String[] getInterfaces() {
            return transform(newArrayList(_clazz.getInterfaces()), ClassInspectionUtil.classToName()).toArray(new String[]{});
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + _clazz.getName() + ")";
        }
    }

    private static class ClassFileClassInfo implements ClassInfo {
        private final ClassFile _classFile;

        ClassFileClassInfo(ClassFile classFile) {
            this._classFile = classFile;
        }

        @Override
        public String getName() {
            return _classFile.getName();
        }

        @Override
        public String getSuperclass() {
            return _classFile.getSuperclass();
        }

        @Override
        public String[] getInterfaces() {
            return _classFile.getInterfaces();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + _classFile.getName() + ")";
        }
    }
}