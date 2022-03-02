package com.zestic.common.inspection.collect;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

abstract class Entry {
    private final String _className;

    Entry(String className) {
        _className = className;
    }

    String getClassName() {
        return _className;
    }

    abstract byte[] getContent() throws IOException;

    static Entry newEntry(String className, File file) {
        return new FileContent(className, file);
    }

    static Entry newEntry(String className, JarFile jar, JarEntry entry) {
        return new JarEntryContent(className, jar, entry);
    }

    private static class FileContent extends Entry {
        private final File _file;

        FileContent(String className, File file) {
            super(className);
            _file = file;
        }

        @Override
        public byte[] getContent() throws IOException {
            return Files.toByteArray(_file);
        }
    }

    private static class JarEntryContent extends Entry {
        private final JarEntry _entry;

        private final JarFile _jar;

        JarEntryContent(String className, JarFile jar, JarEntry entry) {
            super(className);
            _jar = jar;
            _entry = entry;
        }

        @Override
        public byte[] getContent() throws IOException {
            return ByteStreams.toByteArray(_jar.getInputStream(_entry));
        }
    }
}