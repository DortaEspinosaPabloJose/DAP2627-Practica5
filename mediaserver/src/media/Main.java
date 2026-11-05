package media;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import media.factory.AudioFactory;
import media.factory.MediaFactory;
import media.fetcher.MediaFetchException;
import media.player.MediaPlayer;
import media.searcher.MediaSearcher;

// main que solo prueba la parte de audio
public class Main {

    public static void main(String[] args) {
        File dir = new File(args.length > 0
                ? args[0]
                : System.getProperty("user.home") + "/Audios");
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("No se pudo crear: " + dir);
            return;
        }

        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Fuente (1 = Local, 2 = YouTube): ");
            boolean source = !in.nextLine().trim().equals("2");

            // Una sola fábrica crea la familia completa: buscador + reproductor
            MediaFactory factory = new AudioFactory(source, dir);
            MediaSearcher searcher = factory.createSearcher();
            MediaPlayer player = factory.createPlayer();

            try {
                while (true) {
                    System.out.print("\nBúsqueda (vacío para salir): ");
                    String query = in.nextLine().trim();
                    if (query.isEmpty()) break;

                    try {
                        List<String> results = searcher.search(query);
                        if (results.isEmpty()) {
                            System.out.println("Sin resultados.");
                            continue;
                        }

                        for (int i = 0; i < results.size(); i++) {
                            System.out.printf("%2d) %s%n", i + 1, results.get(i));
                        }

                        System.out.print("Selecciona número (0 = cancelar): ");
                        int n = parseInt(in.nextLine());
                        if (n < 1 || n > results.size()) continue;

                        File file = searcher.select(results.get(n - 1));
                        System.out.println("Fichero: " + file.getAbsolutePath()
                                + " (" + file.length() / 1024 + " KB)");

                        player.play(file);
                        System.out.print("Reproduciendo... pulsa Enter para detener.");
                        in.nextLine();
                        player.stop();

                    } catch (MediaFetchException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            } finally {
                player.stop(); // libera el Clip pase lo que pase
            }
        }
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}