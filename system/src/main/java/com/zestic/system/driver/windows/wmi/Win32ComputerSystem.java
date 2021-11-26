/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_ComputerSystem}
 */
@ThreadSafe public final class Win32ComputerSystem {

    private static final String WIN32_COMPUTER_SYSTEM = "Win32_ComputerSystem";


    private Win32ComputerSystem() {
    }

    /*
     * Queries the Computer System.
     *
     * @return Computer System Manufacturer and Model
     */
    public static WmiResult<ComputerSystemProperty> queryComputerSystem() {
        WmiQuery<ComputerSystemProperty> computerSystemQuery =
            new WmiQuery<>(WIN32_COMPUTER_SYSTEM, ComputerSystemProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance())
            .queryWMI(computerSystemQuery);
    }

    /*
     * Computer System properties
     */
    public enum ComputerSystemProperty {
        MANUFACTURER, MODEL;
    }
}
