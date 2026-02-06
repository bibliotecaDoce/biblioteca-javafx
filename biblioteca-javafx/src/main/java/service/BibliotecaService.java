package service;

import model.Autor;
import persistence.JsonStore;

import java.util.ArrayList;
import java.util.List;

public class BibliotecaService {

    private final JsonStore store;
    private List<Autor> autores;

    public BibliotecaService() {
        store = new JsonStore();
        autores = store.loadAutores();

        if (autores == null) {
            autores = new ArrayList<>();
        }
    }

    // ================= AUTORES =================

    public List<Autor> getAutores() {
        return new ArrayList<>(autores);
    }

    public void addAutor(Autor autor) {
        // Generar ID automático
        int nextId = autores.stream()
                .mapToInt(Autor::getId)
                .max()
                .orElse(0) + 1;

        autor.setId(nextId);
        autores.add(autor);
        store.saveAutores(autores);
    }

    /**
     * Guarda los cambios de un autor ya modificado en memoria.
     * Se usa tras editar (setNombre, setNacionalidad, etc.)
     */
    public void updateAutor() {
        store.saveAutores(autores);
    }

    public void deleteAutor(Autor autor) {
        autores.removeIf(a -> a.getId() == autor.getId());
        store.saveAutores(autores);
    }
}


