package com.example.doglistener.sound;

import com.example.doglistener.config.ResponseProperties;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

@Component
public class JavaSoundPlayer implements ResponseSoundPlayer {

    private static final Logger log =
            LoggerFactory.getLogger(JavaSoundPlayer.class);

    private final ResponseProperties properties;

    private Clip activeClip;

    public JavaSoundPlayer(ResponseProperties properties) {
        this.properties = properties;
    }

   @Override
	public synchronized void play(String soundFile) {

    stopActiveClip();

        stopActiveClip();

        //String soundFile = properties.getSoundFile();

        try {
            InputStream resourceStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(soundFile);

            if (resourceStream == null) {
                log.error(
                        "Response sound file was not found: {}",
                        soundFile
                );
                return;
            }

            try (
                    BufferedInputStream bufferedInputStream =
                            new BufferedInputStream(resourceStream);

                    AudioInputStream audioInputStream =
                            AudioSystem.getAudioInputStream(
                                    bufferedInputStream
                            )
            ) {
                Clip clip = AudioSystem.getClip();

                clip.open(audioInputStream);

                applyVolume(
                        clip,
                        properties.getVolume()
                );

                clip.addLineListener(event -> {
                    switch (event.getType().toString()) {
                        case "STOP", "CLOSE" -> closeClip(clip);
                    }
                });

                activeClip = clip;

                clip.setFramePosition(0);
                clip.start();

                log.info(
                        "Playing response sound: {}",
                        soundFile
                );
            }

        } catch (Exception exception) {
            log.error(
                    "Failed to play response sound: {}",
                    soundFile,
                    exception
            );
        }
    }

    private void applyVolume(Clip clip, float volume) {

        float normalizedVolume =
                Math.max(0.0f, Math.min(1.0f, volume));

        if (!clip.isControlSupported(
                FloatControl.Type.MASTER_GAIN)) {

            log.debug(
                    "Audio output does not support volume control."
            );

            return;
        }

        FloatControl gainControl =
                (FloatControl) clip.getControl(
                        FloatControl.Type.MASTER_GAIN
                );

        if (normalizedVolume == 0.0f) {
            gainControl.setValue(
                    gainControl.getMinimum()
            );
            return;
        }

        float decibels =
                (float) (
                        20.0
                        * Math.log10(normalizedVolume)
                );

        decibels = Math.max(
                gainControl.getMinimum(),
                Math.min(
                        gainControl.getMaximum(),
                        decibels
                )
        );

        gainControl.setValue(decibels);
    }

    private synchronized void stopActiveClip() {

        if (activeClip == null) {
            return;
        }

        try {
            if (activeClip.isRunning()) {
                activeClip.stop();
            }

            if (activeClip.isOpen()) {
                activeClip.close();
            }
        } finally {
            activeClip = null;
        }
    }

    private synchronized void closeClip(Clip clip) {

        if (clip.isOpen()) {
            clip.close();
        }

        if (activeClip == clip) {
            activeClip = null;
        }
    }

    @PreDestroy
    public void shutdown() {
        stopActiveClip();
    }
}
