package com.example.doglistener.service;

import com.example.doglistener.audio.AudioChunk;
import com.example.doglistener.audio.MicrophoneCapture;
import com.example.doglistener.ml.AudioConverter;
import com.example.doglistener.ml.InferenceEngine;
import com.example.doglistener.ml.Prediction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DetectionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DetectionService.class);

    private final MicrophoneCapture microphone;
	private final InferenceEngine inferenceEngine;

   public DetectionService(
        MicrophoneCapture microphone,
        InferenceEngine inferenceEngine) {

    this.microphone = microphone;
    this.inferenceEngine = inferenceEngine;

}

    public void start() throws Exception {

        microphone.start();

        while (true) {

            AudioChunk chunk = microphone.readChunk();

            float[] samples =
                    AudioConverter.pcm16ToFloat(
                            chunk.getPcm());

            Prediction prediction =
        inferenceEngine.predict(samples);

LOGGER.info(
        "{} ({:.2f})",
        prediction.label(),
        prediction.score());

        }

    }

}
