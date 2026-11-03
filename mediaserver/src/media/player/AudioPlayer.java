package media.player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import media.fetcher.MediaFetchException;

/**
 * Reproductor de audio (WAV) basado en javax.sound.sampled.
 * Bloquea el hilo llamante hasta que termina la reproducción.
 */
public class AudioPlayer implements MediaPlayer {

    @Override
    public void play(File file) {
        Objects.requireNonNull(file, "file no puede ser null");
        if (!file.isFile() || !file.canRead()) {
            throw new MediaFetchException("No se puede leer el fichero de audio: " + file.getAbsolutePath());
        }

        CountDownLatch finished = new CountDownLatch(1);

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file);
             Clip clip = AudioSystem.getClip()) {

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    finished.countDown();
                }
            });

            clip.open(stream);
            clip.start();
            finished.await();

        } catch (UnsupportedAudioFileException e) {
            throw new MediaFetchException("Formato de audio no soportado: " + file.getName(), e);
        } catch (IOException | LineUnavailableException e) {
            throw new MediaFetchException("Error al reproducir: " + file.getName(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}