package media.searcher;

import java.io.File;
import java.util.List;

public interface MediaSearcher {
    List<String> search(String query); // Devuelve una lista de títulos encontrados
    File select(String name);          // Devuelve el fichero específico seleccionado
}