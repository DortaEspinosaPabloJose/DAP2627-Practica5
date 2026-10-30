import media.factory.MediaFactory;
import media.player.MediaPlayer;
import media.searcher.MediaSearcher;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {

    private MediaFactory activeFactory;
    private MediaSearcher activeSearcher;
    private MediaPlayer activePlayer;

    private JComboBox<String> comboFormat;
    private JComboBox<String> comboSource;
    private DefaultListModel<String> listModel;

    public Main() {
        setTitle("Media Player");
        setSize(1000, 562);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior (Controles de fábrica y búsqueda)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboFormat = new JComboBox<>(new String[]{"Vídeo", "Audio"});
        comboSource = new JComboBox<>(new String[]{"Local", "YouTube"});

        JTextField searchField = new JTextField(30);
        JButton searchBtn = new JButton("Buscar");

        topPanel.add(new JLabel("Formato:"));
        topPanel.add(comboFormat);
        topPanel.add(new JLabel("Fuente:"));
        topPanel.add(comboSource);
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // Panel izquierdo (Resultados de búsqueda)
        listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(resultList);
        listScroll.setPreferredSize(new Dimension(250, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Resultados"));

        add(listScroll, BorderLayout.WEST);

        // Panel Central (Área de reproducción)
        JPanel playerPanel = new JPanel();
        playerPanel.setBackground(Color.BLACK);
        playerPanel.setLayout(new GridBagLayout());
        JLabel statusLabel = new JLabel("Seleccione un archivo para reproducir", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        playerPanel.add(statusLabel);

        add(playerPanel, BorderLayout.CENTER);

        // Panel inferior (Controles de reproducción)
        JPanel bottomPanel = new JPanel();
        JButton playBtn = new JButton("Reproducir");
        bottomPanel.add(playBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        /*
         * LOGICA DEL CLIENTE:
         */
        searchBtn.addActionListener(e -> {
            String format = (String) comboFormat.getSelectedItem();
            String source = (String) comboSource.getSelectedItem();

            // TODO:
            // if (format.equals("Vídeo")) { activeFactory = new VideoFactory(source); }
            // else { activeFactory = new AudioFactory(source); }

            // activeSearcher = activeFactory.createSearcher();
            // List<String> results = activeSearcher.search(searchField.getText());
            // actualizar listModel con results...
        });

        playBtn.addActionListener(e -> {
            String selected = resultList.getSelectedValue();
            if (selected != null && activeFactory != null) {
                // TODO:
                // File mediaFile = activeSearcher.select(selected);
                // activePlayer = activeFactory.createPlayer();
                // activePlayer.play(mediaFile);
                statusLabel.setText("Reproduciendo: " + selected);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
    }
}