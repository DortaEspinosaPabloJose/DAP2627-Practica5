import media.factory.AudioFactory;
import media.factory.MediaFactory;
import media.factory.VideoFactory;
import media.player.MediaPlayer;
import media.searcher.MediaSearcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class Main extends JFrame {

    private MediaFactory activeFactory;
    private MediaSearcher activeSearcher;
    private MediaPlayer activePlayer;

    private DefaultListModel<String> listModel;
    private JList<String> resultList;
    private JPanel centerPanel;
    private JLabel statusLabel;

    // Controles de tiempo
    private JSlider timeSlider;
    private JButton btnPause;
    private boolean isPaused = false;

    // Estado general
    private boolean isVideoMode = true;
    private boolean isLocalMode = false;

    private final String videoDir = "media_files/video";
    private final String audioDir = "media_files/audio";

    // Paleta de colores del Mockup
    private final Color bgDark = Color.decode("#1E212B");
    private final Color bgDarker = Color.decode("#15181F");
    private final Color accentBlue = Color.decode("#2B65D9");
    private final Color textLight = Color.decode("#E0E0E0");
    private final Color textMuted = Color.decode("#8A8F99");
    private final Color borderDark = Color.decode("#2C313C");

    public Main() {
        setTitle("Media Server");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgDarker);

        new File(videoDir).mkdirs();
        new File(audioDir).mkdirs();

        // --- PANEL SUPERIOR ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgDark);
        topContainer.setBorder(new MatteBorder(0, 0, 1, 0, borderDark));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        modePanel.setOpaque(false);
        JButton btnModeVideo = createStyledButton("Modo Vídeo", true);
        JButton btnModeAudio = createStyledButton("Modo Audio", false);
        modePanel.add(btnModeVideo);
        modePanel.add(btnModeAudio);

        JLabel titleLabel = new JLabel("MEDIA SERVER", SwingConstants.CENTER);
        titleLabel.setForeground(textMuted);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel sourcePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sourcePanel.setOpaque(false);
        JButton btnSrcLocal = createStyledButton("Local", false);
        JButton btnSrcYt = createStyledButton("YouTube", true);
        sourcePanel.add(btnSrcLocal);
        sourcePanel.add(btnSrcYt);

        headerPanel.add(modePanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(sourcePanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(5, 20, 15, 20));

        JTextField searchField = new JTextField();
        searchField.setBackground(bgDarker);
        searchField.setForeground(textLight);
        searchField.setCaretColor(textLight);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderDark, 1, true),
                new EmptyBorder(8, 15, 8, 15)
        ));

        JButton btnSearch = createStyledButton("Buscar", true);
        btnSearch.setPreferredSize(new Dimension(100, 35));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // --- PANEL IZQUIERDO (Resultados y estado) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(bgDark);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(new MatteBorder(0, 0, 0, 1, borderDark));

        JLabel resultTitle = new JLabel("RESULTADOS");
        resultTitle.setForeground(textMuted);
        resultTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        resultTitle.setBorder(new EmptyBorder(15, 20, 10, 20));
        leftPanel.add(resultTitle, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setBackground(bgDark);
        resultList.setForeground(textLight);
        resultList.setSelectionBackground(accentBlue);
        resultList.setSelectionForeground(Color.WHITE);
        resultList.setBorder(new EmptyBorder(0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(bgDark);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Etiqueta de estado en la parte inferior de los resultados
        statusLabel = new JLabel("Listo", SwingConstants.CENTER);
        statusLabel.setForeground(textMuted);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // --- PANEL CENTRAL (Reproductor) ---
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.BLACK);

        // Mensaje inicial de bienvenida
        JLabel welcomeLabel = new JLabel("Usa el buscador para encontrar archivos...", SwingConstants.CENTER);
        welcomeLabel.setForeground(textMuted);
        welcomeLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        centerPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Controles) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 0));
        bottomPanel.setBackground(bgDark);
        bottomPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel btnControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnControlsPanel.setOpaque(false);

        JButton btnPlay = createControlButton("▶ Descargar/Play");
        btnPause = createControlButton("⏸ Pausa");
        JButton btnStop = createControlButton("⏹ Stop");

        btnControlsPanel.add(btnPlay);
        btnControlsPanel.add(btnPause);
        btnControlsPanel.add(btnStop);

        timeSlider = new JSlider(0, 100, 0);
        timeSlider.setBackground(bgDark);
        timeSlider.setOpaque(true);

        bottomPanel.add(btnControlsPanel, BorderLayout.WEST);
        bottomPanel.add(timeSlider, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // LOGICA DE EVENTOS
        // ==========================================

        btnModeVideo.addActionListener(e -> { isVideoMode = true; updateButtonStyles(btnModeVideo, btnModeAudio); });
        btnModeAudio.addActionListener(e -> { isVideoMode = false; updateButtonStyles(btnModeAudio, btnModeVideo); });
        btnSrcLocal.addActionListener(e -> { isLocalMode = true; updateButtonStyles(btnSrcLocal, btnSrcYt); });
        btnSrcYt.addActionListener(e -> { isLocalMode = false; updateButtonStyles(btnSrcYt, btnSrcLocal); });

        btnSearch.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) return;

            statusLabel.setText("Buscando...");
            listModel.clear();

            try {
                if (isVideoMode) {
                    activeFactory = new VideoFactory(isLocalMode, new File(videoDir), centerPanel);
                } else {
                    activeFactory = new AudioFactory(isLocalMode, new File(audioDir));
                }
                activeSearcher = activeFactory.createSearcher();

                new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() {
                        return activeSearcher.search(query);
                    }
                    @Override
                    protected void done() {
                        try {
                            List<String> results = get();
                            for (String res : results) listModel.addElement(res);
                            statusLabel.setText(results.isEmpty() ? "Sin resultados" : "Seleccione y pulse Play");
                        } catch (Exception ex) {
                            statusLabel.setText("Error en la búsqueda");
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnPlay.addActionListener(e -> {
            String selected = resultList.getSelectedValue();
            if (selected == null || activeSearcher == null) return;

            if (activePlayer != null) activePlayer.stop();

            isPaused = false;
            btnPause.setText("⏸ Pausa");
            timeSlider.setValue(0);
            statusLabel.setText(isLocalMode ? "Cargando..." : "Procesando con yt-dlp...");

            new SwingWorker<File, Void>() {
                @Override
                protected File doInBackground() {
                    return activeSearcher.select(selected);
                }
                @Override
                protected void done() {
                    try {
                        File mediaFile = get();
                        activePlayer = activeFactory.createPlayer();

                        activePlayer.setProgressListener(percent -> {
                            if (!timeSlider.getValueIsAdjusting()) {
                                timeSlider.setValue(percent.intValue());
                            }
                        });

                        if (!isVideoMode) {
                            centerPanel.removeAll();
                            JLabel audioIcon = new JLabel("🎵", SwingConstants.CENTER);
                            audioIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
                            audioIcon.setForeground(textMuted);

                            JLabel audioTitle = new JLabel(mediaFile.getName(), SwingConstants.CENTER);
                            audioTitle.setForeground(textLight);
                            audioTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
                            audioTitle.setBorder(new EmptyBorder(20, 0, 0, 0));

                            JPanel audioPanel = new JPanel(new BorderLayout());
                            audioPanel.setBackground(Color.BLACK);
                            audioPanel.add(audioIcon, BorderLayout.CENTER);
                            audioPanel.add(audioTitle, BorderLayout.SOUTH);

                            centerPanel.add(audioPanel, BorderLayout.CENTER);
                            centerPanel.revalidate();
                            centerPanel.repaint();
                        }

                        statusLabel.setText("Reproduciendo");
                        activePlayer.play(mediaFile);
                    } catch (Exception ex) {
                        statusLabel.setText("Error al reproducir");
                        ex.printStackTrace();
                    }
                }
            }.execute();
        });

        btnPause.addActionListener(e -> {
            if (activePlayer != null) {
                if (!isPaused) {
                    activePlayer.pause();
                    btnPause.setText("▶ Reanudar");
                } else {
                    activePlayer.resume();
                    btnPause.setText("⏸ Pausa");
                }
                isPaused = !isPaused;
            }
        });

        btnStop.addActionListener(e -> {
            if (activePlayer != null) {
                activePlayer.stop();
                btnPause.setText("⏸ Pausa");
                isPaused = false;
                timeSlider.setValue(0);

                statusLabel.setText("Reproducción detenida");

                // Limpiamos la pantalla dejándola en negro si el usuario da a Stop manualmente
                centerPanel.removeAll();
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        timeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (activePlayer != null) {
                    activePlayer.seek(timeSlider.getValue());
                }
            }
        });
    }

    private JButton createStyledButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderDark),
                new EmptyBorder(8, 15, 8, 15)
        ));
        if (isActive) {
            btn.setBackground(accentBlue);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(bgDarker);
            btn.setForeground(textMuted);
        }
        return btn;
    }

    private void updateButtonStyles(JButton activeBtn, JButton inactiveBtn) {
        activeBtn.setBackground(accentBlue); activeBtn.setForeground(Color.WHITE);
        inactiveBtn.setBackground(bgDarker); inactiveBtn.setForeground(textMuted);
    }

    private JButton createControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#333842"));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        return btn;
    }

    public static void main(String[] args) {
        try { new javafx.embed.swing.JFXPanel(); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}