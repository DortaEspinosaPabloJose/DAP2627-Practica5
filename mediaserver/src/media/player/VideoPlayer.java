package media.player;

import java.io.File;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import media.fetcher.MediaFetchException;

/**
 * Reproductor de vídeo (MP4) basado en JavaFX embebido en Swing.
 */
public class VideoPlayer implements MediaPlayer {

    private final JPanel targetPanel;
    private static JFXPanel jfxPanel;
    private javafx.scene.media.MediaPlayer fxPlayer;
    private Consumer<Double> progressListener;

    public VideoPlayer(JPanel targetPanel) {
        this.targetPanel = Objects.requireNonNull(targetPanel, "targetPanel no puede ser null");
        this.targetPanel.setLayout(new BorderLayout());

        if (jfxPanel == null) {
            jfxPanel = new JFXPanel();
            Platform.setImplicitExit(false);
        }
    }

    @Override
    public void setProgressListener(Consumer<Double> listener) {
        this.progressListener = listener;
    }

    @Override
    public void play(File file) {
        this.stop();
        Objects.requireNonNull(file, "file no puede ser null");
        if (!file.isFile() || !file.canRead()) {
            throw new MediaFetchException("No se puede leer el fichero de vídeo: " + file.getAbsolutePath());
        }

        // Preparar la UI en el hilo de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            targetPanel.removeAll();
            targetPanel.add(jfxPanel, BorderLayout.CENTER);
            targetPanel.revalidate();
            targetPanel.repaint();
        });

        // Operaciones de JavaFX en su propio hilo
        Platform.runLater(() -> {
            try {
                Media media = new Media(file.toURI().toString());
                fxPlayer = new javafx.scene.media.MediaPlayer(media);
                MediaView mediaView = new MediaView(fxPlayer);
                mediaView.setPreserveRatio(true);

                StackPane root = new StackPane(mediaView);
                root.setStyle("-fx-background-color: black;");

                Scene scene = new Scene(root, javafx.scene.paint.Color.BLACK);

                root.widthProperty().addListener((obs, oldVal, newVal) -> mediaView.setFitWidth(newVal.doubleValue()));
                root.heightProperty().addListener((obs, oldVal, newVal) -> mediaView.setFitHeight(newVal.doubleValue()));

                // Enviar el progreso a la interfaz
                fxPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (progressListener != null && media.getDuration() != null) {
                        double percent = (newTime.toMillis() / media.getDuration().toMillis()) * 100.0;
                        javax.swing.SwingUtilities.invokeLater(() -> progressListener.accept(percent));
                    }
                });

                jfxPanel.setScene(scene);
                fxPlayer.play();
            } catch (Exception e) {
                throw new MediaFetchException("Error al reproducir el vídeo: " + file.getName(), e);
            }
        });
    }

    @Override
    public void stop() {
        Platform.runLater(() -> {
            if (fxPlayer != null) {
                fxPlayer.stop();
                fxPlayer.dispose();
                fxPlayer = null;
            }
            if (jfxPanel != null) {
                jfxPanel.setScene(null);
            }
        });
    }

    @Override
    public void pause() {
        Platform.runLater(() -> {
            if (fxPlayer != null) fxPlayer.pause();
        });
    }

    @Override
    public void resume() {
        Platform.runLater(() -> {
            if (fxPlayer != null) fxPlayer.play();
        });
    }

    @Override
    public void seek(double percentage) {
        Platform.runLater(() -> {
            if (fxPlayer != null && fxPlayer.getMedia().getDuration() != null) {
                Duration duration = fxPlayer.getMedia().getDuration();
                fxPlayer.seek(duration.multiply(percentage / 100.0));
            }
        });
    }
}