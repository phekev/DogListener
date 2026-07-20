package com.example.doglistener.service;

import com.example.doglistener.audio.AudioChunk;
import com.example.doglistener.audio.MicrophoneCapture;
import com.example.doglistener.config.DetectorProperties;
import com.example.doglistener.ml.AudioConverter;
import com.example.doglistener.ml.InferenceEngine;
import com.example.doglistener.ml.OnnxInferenceEngine;
import com.example.doglistener.ml.Prediction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.doglistener.sound.ResponseSoundPlayer;
import com.example.doglistener.config.ResponseProperties;

@Service
public class DetectionService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DetectionService.class);

    private final MicrophoneCapture microphone;
    private final InferenceEngine inferenceEngine;
    private final DetectorProperties detectorProperties;
    private final ResponseSoundPlayer responseSoundPlayer;
    private final ResponseProperties responseProperties;

private long barkingSessionStartMillis;
private long lastBarkTimeMillis;

private boolean firstResponsePlayed;
private boolean secondResponsePlayed;
private boolean prolongedResponsePlayed;

    private volatile boolean running;
    private long lastDetectionTimeMillis;

 public DetectionService(
        MicrophoneCapture microphone,
        InferenceEngine inferenceEngine,
        DetectorProperties detectorProperties,
        ResponseProperties responseProperties,
        ResponseSoundPlayer responseSoundPlayer
) {
    this.microphone = microphone;
    this.inferenceEngine = inferenceEngine;
    this.detectorProperties = detectorProperties;
    this.responseProperties = responseProperties;
    this.responseSoundPlayer = responseSoundPlayer;
}

    public void start() throws Exception {
        if (running) {
            LOGGER.warn("Detection service is already running.");
            return;
        }

        running = true;
        microphone.start();

        LOGGER.info(
                "Detection started. Confidence threshold={}, cooldown={} ms",
                detectorProperties.getConfidenceThreshold(),
                detectorProperties.getCooldownMillis());

        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                processNextChunk();
            }
        } finally {
            running = false;
            microphone.stop();
            LOGGER.info("Detection service stopped.");
        }
    }

    public void stop() {
        running = false;
    }
    
    private void processNextChunk() {
    try {
        LOGGER.info("Waiting for audio chunk...");

        AudioChunk chunk = microphone.readChunk();

        LOGGER.info(
                "Audio chunk received: {} bytes",
                chunk.getPcm().length
        );

        float[] samples = AudioConverter.pcm16ToFloat(chunk.getPcm());

        LOGGER.info(
                "Converted audio: {} samples",
                samples.length
        );

        Prediction prediction = inferenceEngine.predict(samples);

        LOGGER.info(
                "Prediction: classId={}, label='{}', confidence={}",
                prediction.classId(),
                prediction.label(),
                prediction.confidence()
        );

        if (!prediction.isDogBark()) {
            return;
        }

        if (prediction.confidence()
                < detectorProperties.getConfidenceThreshold()) {
            return;
        }

        // Existing cooldown and response logic...

    
    
    

   

            long now = System.currentTimeMillis();

           boolean barkDetected =
        prediction.isDogBark()
        && prediction.confidence()
        >= detectorProperties.getConfidenceThreshold();

if (!barkDetected) {
    checkForQuietReset(now);
    return;
}

handleBarkDetected(prediction, now);

     } catch (Exception ex) {
        LOGGER.error("Failed to process audio chunk.", ex);
    }
}

    private boolean isInCooldown(long now) {
        return lastDetectionTimeMillis > 0
                && now - lastDetectionTimeMillis
                < detectorProperties.getCooldownMillis();
    }

   private void handleBarkDetected(
        Prediction prediction,
        long now
) {
    startSessionIfNecessary(now);

    lastBarkTimeMillis = now;

    long sessionDuration =
            now - barkingSessionStartMillis;

    LOGGER.info(
            "Dog bark detected: confidence={}, sessionDuration={} ms",
            prediction.confidence(),
            sessionDuration
    );

    if (!firstResponsePlayed) {
    playFirstResponse();
    return;
}

if (!prolongedResponsePlayed
        && sessionDuration
        >= responseProperties
                .getProlongedResponseDelayMillis()) {

    playProlongedResponse();
    return;
}

if (!secondResponsePlayed
        && sessionDuration
        >= responseProperties
                .getSecondResponseDelayMillis()) {

    playSecondResponse();
}
}
private void playFirstResponse() {

    firstResponsePlayed = true;

    LOGGER.info(
            "Playing first bark response: {}",
            responseProperties.getFirstSoundFile()
    );

    responseSoundPlayer.play(
            responseProperties.getFirstSoundFile()
    );
}

private void playSecondResponse() {

    secondResponsePlayed = true;

    LOGGER.info(
            "Playing second bark response: {}",
            responseProperties.getSecondSoundFile()
    );

    responseSoundPlayer.play(
            responseProperties.getSecondSoundFile()
    );
}


private void playProlongedResponse() {

    prolongedResponsePlayed = true;

    LOGGER.info(
            "Playing prolonged-barking response: {}",
            responseProperties.getProlongedSoundFile()
    );

    responseSoundPlayer.play(
            responseProperties.getProlongedSoundFile()
    );
}

private void checkForQuietReset(long now) {

    if (barkingSessionStartMillis == 0) {
        return;
    }

    long quietDuration =
            now - lastBarkTimeMillis;

    if (quietDuration
            < responseProperties.getQuietResetMillis()) {
        return;
    }

    LOGGER.info(
            "Barking session ended after {} ms of quiet.",
            quietDuration
    );

    resetBarkingSession();
}

private void resetBarkingSession() {

    barkingSessionStartMillis = 0;
    lastBarkTimeMillis = 0;

    firstResponsePlayed = false;
    secondResponsePlayed = false;
    prolongedResponsePlayed = false;
}


private void startSessionIfNecessary(long now) {

    if (barkingSessionStartMillis > 0) {
        return;
    }

    barkingSessionStartMillis = now;
    lastBarkTimeMillis = now;

    firstResponsePlayed = false;
    secondResponsePlayed = false;
    prolongedResponsePlayed = false;

    LOGGER.info("New barking session started.");
}
}
