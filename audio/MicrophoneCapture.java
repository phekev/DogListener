package com.example.doglistener.audio;

import com.example.doglistener.config.AudioProperties;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sound.sampled.*;

@Component
public class MicrophoneCapture {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MicrophoneCapture.class);

    private final AudioProperties config;

    private TargetDataLine microphone;

    private int bytesPerChunk;

    public MicrophoneCapture(AudioProperties config) {

        this.config = config;

    }

    public void start() throws Exception {

        AudioFormat format =
                AudioFormatFactory.create(config);

        DataLine.Info info =
                new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {

            throw new IllegalStateException(
                    "Microphone format not supported.");

        }

        microphone =
                (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(format, config.getBufferSize());

        microphone.start();

        bytesPerChunk =
                (int)((config.getSampleRate()
                        * config.getChunkMillis()
                        / 1000)
                        * config.getChannels()
                        * (config.getSampleSize() / 8));

        LOGGER.info("Microphone started.");

        LOGGER.info("Chunk size = {} bytes", bytesPerChunk);

    }

    public AudioChunk readChunk() {

        byte[] buffer = new byte[bytesPerChunk];

        int offset = 0;

        while (offset < buffer.length) {

            offset += microphone.read(
                    buffer,
                    offset,
                    buffer.length - offset);

        }
	long timestamp = System.nanoTime();

	return new AudioChunk(buffer, timestamp);
       
    }

    @PreDestroy
    public void stop() {

        if (microphone != null) {

            microphone.stop();

            microphone.close();

            LOGGER.info("Microphone stopped.");

        }

    }

}
