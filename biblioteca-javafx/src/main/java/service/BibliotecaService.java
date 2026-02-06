package service;

import model.Autor;
import model.Libro;
import persistence.JsonStore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BibliotecaService {

    private final JsonStore store;
    private final List<Autor> autores;
    private final List<Libro> libros;

    public BibliotecaService() {
        this.store = new JsonStore();
        this.autores = new ArrayList<>(store.loadAutores());
        this.libros  = new ArrayList<>(store.loadLibros());
    }

    // -------- AUTORES --------
    public List<Autor> getAutores() {
        return autores;
    }

    public Autor addAutor(Autor autor) {
        autor.setId(nextAutorId());
        autores.add(autor);
        store.saveAutores(autores);
        return autor;
    }

    public void updateAutor() {
        store.saveAutores(autores);
    }

    public void deleteAutor(Autor autor) {
        autores.remove(autor);
        store.saveAutores(autores);
    }

    private int nextAutorId() {
        return autores.stream().map(Autor::getId).max(Comparator.naturalOrder()).orElse(0) + 1;
    }

    // -------- LIBROS --------
    public List<Libro> getLibros() {
        return libros;
    }

    public Libro addLibro(Libro libro) {
        libro.setId(nextLibroId());
        libros.add(libro);
        store.saveLibros(libros);
        return libro;
    }

    public void updateLibro() {
        store.saveLibros(libros);
    }

    public void deleteLibro(Libro libro) {
        libros.remove(libro);
        store.saveLibros(libros);
    }

    private int nextLibroId() {
        return libros.stream().map(Libro::getId).max(Comparator.naturalOrder()).orElse(0) + 1;
    }
}

