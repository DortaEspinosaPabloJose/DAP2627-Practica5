package media.player;

import java.io.File;
import java.util.Objects;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;

import media.fetcher.MediaFetchException;

/**
 * Reproductor de vídeo (MP4) basado en JavaFX embebido en Swing.
 */
public class VideoPlayer implements MediaPlayer {

    private final JPanel targetPanel;
    private JFXPanel jfxPanel;
    private javafx.scene.media.MediaPlayer fxPlayer;

    public VideoPlayer(JPanel targetPanel) {
        this.targetPanel = Objects.requireNonNull(targetPanel, "targetPanel no puede ser null");
        this.jfxPanel = new JFXPanel();
        this.targetPanel.setLayout(new BorderLayout());
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
            if (fxPlayer != null) {
                fxPlayer.stop();
                fxPlayer.dispose();
            }

            try {
                Media media = new Media(file.toURI().toString());
                fxPlayer = new javafx.scene.media.MediaPlayer(media);
                MediaView mediaView = new MediaView(fxPlayer);
                mediaView.setPreserveRatio(true);

                // Creamos contenedor y escena
                StackPane root = new StackPane(mediaView);
                Scene scene = new Scene(root, javafx.scene.paint.Color.BLACK);

                // Vinculamos el tamaño del vídeo al tamaño de la escena de JavaFX
                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());

                // Asignamos la escena al panel
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
        });
    }
}