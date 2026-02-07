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

    private int nextAutorId = 1;
    private int nextLibroId = 1;

    public BibliotecaService() {
        this.store = new JsonStore();

        // Carga desde JSON
        this.autores = new ArrayList<>(store.loadAutores());
        this.libros = new ArrayList<>(store.loadLibros());

        recalcularSiguientesIds();
    }

    /* ===================== AUTORES ===================== */

    public List<Autor> getAutores() {
        return new ArrayList<>(autores);
    }

    public void addAutor(Autor a) {
        a.setId(nextAutorId++);
        autores.add(a);
        autores.sort(Comparator.comparingInt(Autor::getId));
        store.saveAutores(autores);
    }

    public void updateAutor(Autor actualizado) {
        for (int i = 0; i < autores.size(); i++) {
            if (autores.get(i).getId() == actualizado.getId()) {
                autores.set(i, actualizado);
                store.saveAutores(autores);
                return;
            }
        }
        // si no existe, no hacemos nada
    }

    public void deleteAutor(Autor a) {
        autores.removeIf(x -> x.getId() == a.getId());
        store.saveAutores(autores);
    }

    // Métodos extra para LibrosController
    public Autor getAutorById(int id) {
        for (Autor a : autores) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    public String getAutorNombreById(int id) {
        Autor a = getAutorById(id);
        return (a != null) ? a.getNombre() : "(sin autor)";
    }

    /* ===================== LIBROS ===================== */

    public List<Libro> getLibros() {
        return new ArrayList<>(libros);
    }

    public void addLibro(Libro l) {
        l.setId(nextLibroId++);
        libros.add(l);
        libros.sort(Comparator.comparingInt(Libro::getId));
        store.saveLibros(libros);
    }

    public void updateLibro(Libro actualizado) {
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getId() == actualizado.getId()) {
                libros.set(i, actualizado);
                store.saveLibros(libros);
                return;
            }
        }
    }

    public void deleteLibro(Libro l) {
        libros.removeIf(x -> x.getId() == l.getId());
        store.saveLibros(libros);
    }

    /* ===================== UTIL ===================== */

    private void recalcularSiguientesIds() {
        if (!autores.isEmpty()) {
            int max = autores.stream().map(Autor::getId).max(Integer::compareTo).orElse(0);
            nextAutorId = max + 1;
        }
        if (!libros.isEmpty()) {
            int max = libros.stream().map(Libro::getId).max(Integer::compareTo).orElse(0);
            nextLibroId = max + 1;
        }
    }
}

