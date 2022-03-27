
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.SoundCard;

/*
 * An abstract Sound Card
 */
@Immutable public abstract class AbstractSoundCard implements SoundCard {

    private String kernelVersion;
    private String name;
    private String codec;

    /*
     * Abstract Sound Card Constructor
     *
     * @param kernelVersion The version
     * @param name          The name
     * @param codec         The codec
     */
    protected AbstractSoundCard(String kernelVersion, String name, String codec) {
        this.kernelVersion = kernelVersion;
        this.name = name;
        this.codec = codec;
    }

    @Override public String getDriverVersion() {
        return this.kernelVersion;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public String getCodec() {
        return this.codec;
    }

    @Override public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SoundCard@");
        builder.append(Integer.toHexString(hashCode()));
        builder.append(" [name=");
        builder.append(this.name);
        builder.append(", kernelVersion=");
        builder.append(this.kernelVersion);
        builder.append(", codec=");
        builder.append(this.codec);
        builder.append(']');
        return builder.toString();
    }

}
