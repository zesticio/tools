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

package com.zestic.common.inspection.inspect;

import com.zestic.common.inspection.InspectionHelper;

public class ClassesImplementing<T> extends ClassesMatching<T> {

    private final Class<T> iface;

    public ClassesImplementing(Class<T> iface) {
        this.iface = iface;
    }

    @Override
    protected boolean isMatch(InspectionHelper.ClassInfo potentialMatch) {
        for (String implementedInterface : potentialMatch.getInterfaces()) {
            if (implementedInterface.equals(iface.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "classes implementing " + iface.getSimpleName();
    }
}
