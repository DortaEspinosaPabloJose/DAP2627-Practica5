package media.player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import media.fetcher.MediaFetchException;

/**
 * Reproductor de audio (WAV) basado en javax.sound.sampled.
 */
public class AudioPlayer implements MediaPlayer {

    private Clip clip;
    private final Logger logger = Logger.getLogger("AudioPlayerLogger");
    private Consumer<Double> progressListener;
    private Timer progressTimer;

    @Override
    public void setProgressListener(Consumer<Double> listener) {
        this.progressListener = listener;
    }

    @Override
    public synchronized void play(File file) {
        this.stop();
        Objects.requireNonNull(file, "file no puede ser null");
        if (!file.isFile() || !file.canRead()) {
            throw new MediaFetchException("No se puede leer el fichero de audio: " + file.getAbsolutePath());
        }

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
            Clip newClip = AudioSystem.getClip();
            logger.log(Level.INFO, "Playing on: " + file.getName());

            newClip.open(stream);
            clip = newClip;
            newClip.start();

            // Lanzamos un Timer de Swing para actualizar la barra de la GUI cada 100ms
            if (progressTimer != null) progressTimer.stop();
            progressTimer = new Timer(100, e -> {
                if (clip != null && clip.isRunning() && progressListener != null) {
                    long total = clip.getMicrosecondLength();
                    long current = clip.getMicrosecondPosition();
                    if (total > 0) {
                        progressListener.accept((current * 100.0) / total);
                    }
                }
            });
            progressTimer.start();

        } catch (UnsupportedAudioFileException e) {
            throw new MediaFetchException("Formato de audio no soportado: " + file.getName(), e);
        } catch (IOException | LineUnavailableException e) {
            throw new MediaFetchException("Error al reproducir el audio: " + file.getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void stop() {
        if (progressTimer != null) {
            progressTimer.stop();
            progressTimer = null;
        }
        if (clip != null) {
            Clip current = clip;
            clip = null;
            current.stop();
            current.close();
        }
    }

    @Override
    public synchronized void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    @Override
    public synchronized void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    @Override
    public synchronized void seek(double percentage) {
        if (clip != null) {
            long total = clip.getMicrosecondLength();
            long targetPos = (long) (total * (percentage / 100.0));
            clip.setMicrosecondPosition(targetPos);
        }
    }
}