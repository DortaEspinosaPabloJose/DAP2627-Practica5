package media.searcher;

import java.io.File;
import java.util.List;

public interface MediaSearcher {
    public List<String> search(String search); // devuelve una lista de titulos
    public File select(String name); // devuelve el fichero especifico
}
