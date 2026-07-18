package com.example.doglistener.service;

import com.example.doglistener.audio.AudioChunk;
import com.example.doglistener.audio.MicrophoneCapture;
import com.example.doglistener.ml.AudioConverter;
import com.example.doglistener.ml.FeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DetectionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DetectionService.class);

    private final MicrophoneCapture microphone;
    private final FeatureExtractor extractor;

    public DetectionService(
            MicrophoneCapture microphone,
            FeatureExtractor extractor) {

        this.microphone = microphone;
        this.extractor = extractor;

    }

    public void start() throws Exception {

        microphone.start();

        while (true) {

            AudioChunk chunk = microphone.readChunk();

            float[] samples =
                    AudioConverter.pcm16ToFloat(
                            chunk.getPcm());

            float[] features =
                    extractor.extract(samples);

            LOGGER.info(
                    "Received {} samples",
                    features.length);

        }

    }

}
