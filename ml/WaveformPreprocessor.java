package com.example.doglistener.ml;

import com.example.doglistener.audio.dsp.DspConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WaveformPreprocessor {

    public float[] prepare(float[] audio) {
        if (audio == null) {
            throw new IllegalArgumentException("Audio must not be null.");
        }

        if (audio.length == 0) {
            throw new IllegalArgumentException("Audio must not be empty.");
        }

        int requiredSamples = DspConstants.PATCH_SAMPLES;

        if (audio.length == requiredSamples) {
            return audio;
        }

        /*
         * Arrays.copyOf:
         * - trims audio when it is longer than required;
         * - zero-pads audio when it is shorter.
         */
        return Arrays.copyOf(audio, requiredSamples);
    }
}
