
package com.zestic.system.driver.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.*;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.mac.CoreGraphics;
import com.zestic.system.software.os.OSDesktopWindow;
import com.zestic.system.util.FormatUtil;
import com.zestic.system.util.platform.mac.CFUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.zestic.system.jna.platform.mac.CoreGraphics.*;

/*
 * Utility to query desktop windows
 */
@ThreadSafe public final class WindowInfo {

    private WindowInfo() {
    }

    /*
     * Gets windows on the operating system's GUI desktop.
     *
     * @param visibleOnly Whether to restrict the list to only windows visible to the user.
     * @return A list of {@link OSDesktopWindow} objects
     * representing the desktop windows.
     */
    public static List<OSDesktopWindow> queryDesktopWindows(boolean visibleOnly) {
        CFArrayRef windowInfo = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(visibleOnly ?
            kCGWindowListOptionOnScreenOnly | kCGWindowListExcludeDesktopElements :
            kCGWindowListOptionAll, kCGNullWindowID);
        int numWindows = windowInfo.getCount();
        // Prepare a list to return
        List<OSDesktopWindow> windowList = new ArrayList<>();
        // Set up keys for dictionary lookup
        CFStringRef kCGWindowIsOnscreen = CFStringRef.createCFString("kCGWindowIsOnscreen");
        CFStringRef kCGWindowNumber = CFStringRef.createCFString("kCGWindowNumber");
        CFStringRef kCGWindowOwnerPID = CFStringRef.createCFString("kCGWindowOwnerPID");
        CFStringRef kCGWindowLayer = CFStringRef.createCFString("kCGWindowLayer");
        CFStringRef kCGWindowBounds = CFStringRef.createCFString("kCGWindowBounds");
        CFStringRef kCGWindowName = CFStringRef.createCFString("kCGWindowName");
        CFStringRef kCGWindowOwnerName = CFStringRef.createCFString("kCGWindowOwnerName");
        try {
            // Populate the list
            for (int i = 0; i < numWindows; i++) {
                // For each array element, get the dictionary
                Pointer result = windowInfo.getValueAtIndex(i);
                CFDictionaryRef windowRef = new CFDictionaryRef(result);
                // Now get information from the dictionary.
                result = windowRef.getValue(kCGWindowIsOnscreen); // Optional key, check for null
                boolean visible = result == null || new CFBooleanRef(result).booleanValue();
                if (!visibleOnly || visible) {
                    result = windowRef.getValue(kCGWindowNumber); // kCFNumberSInt64Type
                    long windowNumber = new CFNumberRef(result).longValue();

                    result = windowRef.getValue(kCGWindowOwnerPID); // kCFNumberSInt64Type
                    long windowOwnerPID = new CFNumberRef(result).longValue();

                    result = windowRef.getValue(kCGWindowLayer); // kCFNumberIntType
                    int windowLayer = new CFNumberRef(result).intValue();

                    result = windowRef.getValue(kCGWindowBounds);
                    CGRect rect = new CGRect();
                    CoreGraphics.INSTANCE.CGRectMakeWithDictionaryRepresentation(
                        new CFDictionaryRef(result), rect);
                    Rectangle windowBounds = new Rectangle(FormatUtil.roundToInt(rect.origin.x),
                        FormatUtil.roundToInt(rect.origin.y),
                        FormatUtil.roundToInt(rect.size.width),
                        FormatUtil.roundToInt(rect.size.height));

                    // Note: the Quartz name returned by this field is rarely used
                    result = windowRef.getValue(kCGWindowName); // Optional key, check for null
                    String windowName = CFUtil.cfPointerToString(result, false);
                    // This is the program running the window, use as name if name blank or add in
                    // parenthesis
                    result = windowRef.getValue(kCGWindowOwnerName); // Optional key, check for null
                    String windowOwnerName = CFUtil.cfPointerToString(result, false);
                    if (windowName.isEmpty()) {
                        windowName = windowOwnerName;
                    } else {
                        windowName = windowName + "(" + windowOwnerName + ")";
                    }

                    windowList.add(
                        new OSDesktopWindow(windowNumber, windowName, windowOwnerName, windowBounds,
                            windowOwnerPID, windowLayer, visible));
                }
            }
        } finally {
            // CF references from "Copy" or "Create" must be released
            kCGWindowIsOnscreen.release();
            kCGWindowNumber.release();
            kCGWindowOwnerPID.release();
            kCGWindowLayer.release();
            kCGWindowBounds.release();
            kCGWindowName.release();
            kCGWindowOwnerName.release();
            windowInfo.release();
        }

        return windowList;
    }
}
