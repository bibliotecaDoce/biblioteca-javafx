package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Autor;
import model.Libro;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonStore {

    // Guardamos datos fuera de src/, en carpeta "data" al nivel del pom.xml
    private static final Path DATA_DIR = Path.of("data");
    private static final Path AUTORES_PATH = DATA_DIR.resolve("autores.json");
    private static final Path LIBROS_PATH  = DATA_DIR.resolve("libros.json");

    private final Gson gson;

    public JsonStore() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    // ================= AUTORES =================

    public List<Autor> loadAutores() {
        try {
            if (!Files.exists(AUTORES_PATH)) return new ArrayList<>();

            String content = Files.readString(AUTORES_PATH).trim();
            if (content.isEmpty()) return new ArrayList<>();

            Type type = new TypeToken<List<Autor>>() {}.getType();
            List<Autor> autores = gson.fromJson(content, type);
            return autores != null ? autores : new ArrayList<>();

        } catch (Exception e) {
            // Si el JSON está vacío/corrupto, no rompemos la app
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveAutores(List<Autor> autores) {
        try {
            Files.createDirectories(DATA_DIR);
            try (Writer writer = Files.newBufferedWriter(AUTORES_PATH)) {
                gson.toJson(autores, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= LIBROS =================

    public List<Libro> loadLibros() {
        try {
            if (!Files.exists(LIBROS_PATH)) return new ArrayList<>();

            String content = Files.readString(LIBROS_PATH).trim();
            if (content.isEmpty()) return new ArrayList<>();

            Type type = new TypeToken<List<Libro>>() {}.getType();
            List<Libro> libros = gson.fromJson(content, type);
            return libros != null ? libros : new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveLibros(List<Libro> libros) {
        try {
            Files.createDirectories(DATA_DIR);
            try (Writer writer = Files.newBufferedWriter(LIBROS_PATH)) {
                gson.toJson(libros, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

