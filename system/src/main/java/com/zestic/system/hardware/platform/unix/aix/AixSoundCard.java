
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.SoundCard;
import com.zestic.system.hardware.common.AbstractSoundCard;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * AIX Sound Card.
 */
@Immutable final class AixSoundCard extends AbstractSoundCard {

    /*
     * Constructor for AixSoundCard.
     *
     * @param kernelVersion The version
     * @param name          The name
     * @param codec         The codec
     */
    AixSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    /*
     * Gets sound cards
     *
     * @param lscfg a memoized lscfg object
     * @return sound cards
     */
    public static List<SoundCard> getSoundCards(Supplier<List<String>> lscfg) {
        List<SoundCard> soundCards = new ArrayList<>();
        for (String line : lscfg.get()) {
            String s = line.trim();
            if (s.startsWith("paud")) {
                String[] split = ParseUtil.whitespaces.split(s, 3);
                if (split.length == 3) {
                    soundCards.add(
                        new AixSoundCard(Constants.UNKNOWN, split[2], Constants.UNKNOWN));
                }
            }
        }
        return soundCards;
    }
}
