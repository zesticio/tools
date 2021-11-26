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
