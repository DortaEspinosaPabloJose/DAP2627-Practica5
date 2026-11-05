package media.player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import media.fetcher.MediaFetchException;

/**
 * Reproductor de audio (WAV) basado en javax.sound.sampled.
 * No bloquea el hilo llamante: play() devuelve el control nada más empezar.
 */
public class AudioPlayer implements MediaPlayer {

    private Clip clip;
    private final Logger logger = Logger.getLogger("AudioPlayerLogger");

    @Override
    public synchronized void play(File file) {
        this.stop();
        Objects.requireNonNull(file, "file no puede ser null");
        if (!file.isFile() || !file.canRead()) {
            throw new MediaFetchException("No se puede leer el fichero de audio: " + file.getAbsolutePath());
        }

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
            Clip newClip = AudioSystem.getClip();
            logger.log(Level.INFO,newClip.toString() + " playing on: " + AudioSystem.getAudioInputStream(file));

            // Libera los recursos cuando termina (o se detiene) la reproducción
            newClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    release(newClip);
                }
            });

            newClip.open(stream); // el Clip carga los datos en memoria, el stream puede cerrarse después
            clip = newClip;
            newClip.start();

        } catch (UnsupportedAudioFileException e) {
            throw new MediaFetchException("Formato de audio no soportado: " + file.getName(), e);
        } catch (IOException | LineUnavailableException e) {
            throw new MediaFetchException("Error al reproducir el audio: " + file.getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void stop() {
        if (clip != null) {
            Clip current = clip;
            clip = null;
            current.stop();
            current.close();
        }
    }

    private synchronized void release(Clip finished) {
        if (clip == finished) {
            clip = null;
        }
        finished.close();
    }
}