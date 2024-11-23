package com.aluracursos.libreriaalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;
    private String idioma;
    @Column(name = "numero_descargas")
    private Double numeroDescargas;

    public Libro(){}

    public Libro(DatosLibros datosLibros,Autor autor){
        this.titulo = datosLibros.titulo();
        this.autor = autor;
        this.idioma = datosLibros.idiomas().get(0);
        this.numeroDescargas = datosLibros.numeroDescargas();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }
    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDescargas() {
        return numeroDescargas;
    }
    public void setNumeroDescargas(Double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString() {
        return "*************** LIBRO ***************\n" +
                "Titulo: " + titulo + "\n" +
                "Autor: " + autor.getNombre() + "\n" +
                "idioma: " + idioma + "\n" +
                "Numero de Descargas: " + numeroDescargas + "\n" +
                "*************************************";
    }
}
