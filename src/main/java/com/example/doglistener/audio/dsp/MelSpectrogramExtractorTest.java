package com.example.doglistener.audio.dsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MelSpectrogramExtractorTest {

    @Test
    void producesCorrectShape() {

        MelSpectrogramExtractor extractor =
                new MelSpectrogramExtractor();

        float[] samples =
                new float[DspConstants.PATCH_SAMPLES];

        float[][] mel =
                extractor.extract(samples);

        assertEquals(
                DspConstants.FRAMES,
                mel.length);

        assertEquals(
                DspConstants.MEL_BINS,
                mel[0].length);

    }

}
