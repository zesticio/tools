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

package com.zestic.common.inspection.collect;

import com.zestic.common.inspection.ClassInspector;
import com.zestic.common.inspection.InspectionHelper;
import javassist.bytecode.ClassFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

public class ClassCollector<T, CI extends ClassInspector<T>> {
    static final Logger logger = org.slf4j.LoggerFactory.getLogger(ClassCollector.class);

    private ClassLoader _classloader = Thread.currentThread().getContextClassLoader();

    private final CI _inspector;

    private final String _packageName;

    public ClassCollector(CI inspector, String packageName) {
        _inspector = inspector;
        _packageName = packageName;

    }

    public CI findAndLetInspect() {

        Map<String, Entry> entries = new ClassFilesCollector(_classloader, _packageName).getEntries();

        // Inspect collected classes
        InspectionHelper helper = new InspectionHelperImpl(_classloader, entries);
        for (Entry entry : entries.values()) {
            try {
                letInspect(helper, entry.getClassName(), entry.getContent());
            } catch (IOException e) {
                logger.error("Could not read class '" + entry.getClassName() + "'!", e);
            }
        }

        return _inspector;
    }

    private void letInspect(InspectionHelper helper, String className, byte[] classContent) {
        try {

            ClassFile type = InspectionHelperImpl.toClassFile(classContent);

            logger.trace("Checking to see if class " + className + " matches criteria [" + _inspector + "]");

            _inspector.inspect(type, helper);
        } catch (Throwable t) {
            logger.warn("Could not examine class '" + className + "'" + " due to a " + t.getClass().getName() + " with message: " + t.getMessage());
        }
    }

    public void setClassLoader(ClassLoader classloader) {
        _classloader = classloader;
    }
}
