package model;

import java.util.Objects;

public class Libro {
    private int id;
    private String titulo;
    private String isbn;
    private int anio;
    private int autorId;
    private boolean activo;

    public Libro() {}

    public Libro(int id, String titulo, String isbn, int anio, int autorId, boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.anio = anio;
        this.autorId = autorId;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro libro)) return false;
        return id == libro.id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}