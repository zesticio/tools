
package com.zestic.system.hardware.platform.mac;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.SoundCard;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.hardware.common.AbstractSoundCard;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Sound card data obtained via AppleHDA kext
 */
@Immutable final class MacSoundCard extends AbstractSoundCard {

    private static final String APPLE = "Apple Inc.";

    /*
     * Constructor for MacSoundCard.
     *
     * @param kernelVersion The version
     * @param name          The name
     * @param codec         The codec
     */
    MacSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    /*
     * public method used by
     * {@link AbstractHardwareAbstractionLayer} to access the
     * sound cards.
     *
     * @return List of {@link MacSoundCard} objects.
     */
    public static List<SoundCard> getSoundCards() {
        List<SoundCard> soundCards = new ArrayList<>();

        // /System/Library/Extensions/AppleHDA.kext/Contents/Info.plist

        // ..... snip ....
        // <dict>
        // <key>com.apple.driver.AppleHDAController</key>
        // <string>1.7.2a1</string>

        String manufacturer = APPLE;
        String kernelVersion = "AppleHDAController";
        String codec = "AppleHDACodec";

        boolean version = false;
        String versionMarker = "<key>com.apple.driver.AppleHDAController</key>";

        for (final String checkLine : FileUtil.readFile(
            "/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
            if (checkLine.contains(versionMarker)) {
                version = true;
                continue;
            }
            if (version) {
                kernelVersion =
                    "AppleHDAController " + ParseUtil.getTextBetweenStrings(checkLine, "<string>",
                        "</string>");
                version = false;
            }
        }
        soundCards.add(new MacSoundCard(kernelVersion, manufacturer, codec));

        return soundCards;
    }
}
