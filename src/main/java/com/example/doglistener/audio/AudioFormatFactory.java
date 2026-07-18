package com.example.doglistener.audio;

import com.example.doglistener.config.AudioProperties;

import javax.sound.sampled.AudioFormat;

public final class AudioFormatFactory {

    private AudioFormatFactory() {
    }

    public static AudioFormat create(AudioProperties config) {

        return new AudioFormat(
                config.getSampleRate(),
                config.getSampleSize(),
                config.getChannels(),
                config.isSigned(),
                config.isBigEndian());

    }

}
