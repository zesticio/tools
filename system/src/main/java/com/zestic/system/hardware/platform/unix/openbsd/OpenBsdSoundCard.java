
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.SoundCard;
import com.zestic.system.hardware.common.AbstractSoundCard;
import com.zestic.system.util.ExecutingCommand;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * OpenBSD soundcard.
 */
@Immutable final class OpenBsdSoundCard extends AbstractSoundCard {

    private static final Pattern AUDIO_AT = Pattern.compile("audio\\d+ at (.+)");
    private static final Pattern PCI_AT =
        Pattern.compile("(.+) at pci\\d+ dev \\d+ function \\d+ \"(.*)\" (rev .+):.*");

    /*
     * Constructor for OpenBsdSoundCard.
     *
     * @param kernelVersion The version
     * @param name          The name
     * @param codec         The codec
     */
    OpenBsdSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    /*
     * <p>
     * getSoundCards.
     * </p>
     *
     * @return a {@link List} object.
     */
    public static List<SoundCard> getSoundCards() {
        List<String> dmesg = ExecutingCommand.runNative("dmesg");
        // Iterate dmesg once to collect location of audioN
        Set<String> names = new HashSet<>();
        for (String line : dmesg) {
            Matcher m = AUDIO_AT.matcher(line);
            if (m.matches()) {
                names.add(m.group(1));
            }
        }
        // Iterate again and add cards when they match the name
        Map<String, String> nameMap = new HashMap<>();
        Map<String, String> codecMap = new HashMap<>();
        Map<String, String> versionMap = new HashMap<>();
        String key = "";
        for (String line : dmesg) {
            Matcher m = PCI_AT.matcher(line);
            if (m.matches() && names.contains(m.group(1))) {
                key = m.group(1);
                nameMap.put(key, m.group(2));
                versionMap.put(key, m.group(3));
            } else if (!key.isEmpty()) {
                // Codec is on the next line
                int idx = line.indexOf("codec");
                if (idx >= 0) {
                    idx = line.indexOf(':');
                    codecMap.put(key, line.substring(idx + 1).trim());
                }
                // clear key so we don't keep looking
                key = "";
            }
        }
        List<SoundCard> soundCards = new ArrayList<>();
        for (Entry<String, String> entry : nameMap.entrySet()) {
            soundCards.add(new OpenBsdSoundCard(versionMap.get(entry.getKey()), entry.getValue(),
                codecMap.get(entry.getKey())));
        }
        return soundCards;
    }
}
