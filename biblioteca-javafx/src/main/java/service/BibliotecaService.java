package service;

import model.Autor;
import model.Libro;
import persistence.JsonStore;

import java.util.List;

public class BibliotecaService {

    private final JsonStore store = new JsonStore();
    private final List<Autor> autores;
    private final List<Libro> libros;

    public BibliotecaService() {
        autores = store.loadAutores();
        libros = store.loadLibros();
    }

    // ================= AUTORES =================
    public List<Autor> getAutores() {
        return autores;
    }

    public void addAutor(Autor a) {
        int nextId = autores.stream().mapToInt(Autor::getId).max().orElse(0) + 1;
        a.setId(nextId);
        autores.add(a);
        store.saveAutores(autores);
    }

    public void updateAutor(Autor a) {
        int index = autores.indexOf(a);
        if (index != -1) {
            autores.set(index, a);
            store.saveAutores(autores);
        }
    }

    public void deleteAutor(Autor a) {
        autores.remove(a);
        store.saveAutores(autores);
    }

    public String getNombreAutor(int autorId) {
        return autores.stream()
                .filter(a -> a.getId() == autorId)
                .map(Autor::getNombre)
                .findFirst()
                .orElse("Desconocido");
    }

    // ================= LIBROS =================
    public List<Libro> getLibros() {
        return libros;
    }

    public void addLibro(Libro l) {
        int nextId = libros.stream().mapToInt(Libro::getId).max().orElse(0) + 1;
        l.setId(nextId);
        libros.add(l);
        store.saveLibros(libros);
    }

    public void updateLibro(Libro l) {
        int index = libros.indexOf(l);
        if (index != -1) {
            libros.set(index, l);
            store.saveLibros(libros);
        }
    }

    public void deleteLibro(Libro l) {
        libros.remove(l);
        store.saveLibros(libros);
    }

}
