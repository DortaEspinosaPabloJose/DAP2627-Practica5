package media;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import media.factory.AudioFactory;
import media.fetcher.MediaFetchException;
import media.searcher.MediaSearcher;

// main que solo prueba la parte de audio
public class Main {
    // Literals on purpose: AudioSearcher compares with ==, so a String read
    // from the keyboard would NOT match (see improvement #1).
    private static final String LOCAL = "Local";
    private static final String YOUTUBE = "Youtube";

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
            String source = in.nextLine().trim().equals("2") ? YOUTUBE : LOCAL;

            MediaSearcher searcher = new AudioFactory(source.equals(LOCAL), dir).createSearcher();

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
                } catch (MediaFetchException e) {
                    System.err.println("Error: " + e.getMessage());
                }
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