package com.aluracursos.libreriaalura.principal;

import com.aluracursos.libreriaalura.model.*;
import com.aluracursos.libreriaalura.repository.AutorRepository;
import com.aluracursos.libreriaalura.repository.LibroRepository;
import com.aluracursos.libreriaalura.service.ConsumoAPI;
import com.aluracursos.libreriaalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/?search=";

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos coversor;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LibroRepository libroRepository;

    private Scanner teclado = new Scanner(System.in);
    //private boolean exe;


    public Principal() {
        this.teclado = new Scanner(System.in);
       // this.exe =true;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    **************** Menu Principal ***************
                    
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    
                    0- Salir
                    
                    Elige una opcion:
                    
                    **********************************************
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {

                case 1:
                    buscarLibro();
                    break;

                case 2:
                    listarLibrosBuscados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorAnho();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;

                case 0:
                    System.out.println("Saliendo de la aplicacion...");
                    break;
                default:
                    System.out.println("Opcion no valida!!! Ingrese una opcion valida");
            }

        }

    }


    private void buscarLibro() {

        try {
            System.out.println("Ingrese el nombre del libro a buscar: ");
            var tituloLibro = teclado.nextLine();

            //Verifica sino se ingreso algun dato
            if (tituloLibro.isEmpty()){
                System.out.println("No ingreso ningun dato..");
                return;
            }


            //Busca si existe en la Base de datos
            Optional<Libro> libroBD = libroRepository.findByTituloContainingIgnoreCase(tituloLibro);
            if (libroBD.isPresent()){
            System.out.println("\nLibro existente en Base de Datos");
            System.out.println(libroBD.get());
            return;
            }


            var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "+"));
            if (json==null||json.isEmpty()){
                System.out.println("sin respuesta de API");
                return;
            }

            Datos datosBusqueda = coversor.obtenerDatos(json, Datos.class);
            System.out.println(datosBusqueda);

            if (datosBusqueda.resultados()==null|| datosBusqueda.resultados().isEmpty()){
            System.out.println("Libro no encontrado");
            return;
            }

            procesarResultados(datosBusqueda.resultados(),tituloLibro);

         } catch (Exception e) {
            System.out.println("Error en la busqueda... "+ e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarResultados(List<DatosLibros> resultados, String tituloLibro) {
        boolean encontrado = false;
        for (DatosLibros datosLibro : resultados){
            if (datosLibro.titulo().toUpperCase().contains(tituloLibro.toUpperCase())) {
                guardarDatos(datosLibro);
                encontrado = true;
                break;
            }
        }

        if (!encontrado){
            System.out.println("no encontrado exacto" + tituloLibro);
        }

    }

    private void guardarDatos(DatosLibros datosLibro) {

        try {

            if (datosLibro.autor()==null||datosLibro.autor().isEmpty()){
                System.out.println("Libro sin autor registrado");
                return;
            }

            //Guarda los datos del autor
            DatosAutor datosAutor = datosLibro.autor().get(0);
            Autor autor = autorRepository.findByNombre(datosAutor.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosAutor);
                        System.out.println("guardando nuevo: "+datosAutor.nombre());
                        return autorRepository.save(nuevoAutor);
                    });

            if (libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo()).isPresent()){
                System.out.println("Libro existente");
                return;
            }

            //Guardar los datos del libro
            Libro libro = new Libro(datosLibro,autor);
            Libro libroGuardado = libroRepository.save(libro);
            System.out.println("\n Libro guardado:");
            System.out.println(libroGuardado);
            //return;

        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Error"+ e.getMessage());
        }

    }


    private void listarLibrosBuscados() {
        System.out.println("\n --Libros registrados--");
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()){
            System.out.println("no hay libros registrados");
            return;
        }
        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        System.out.println("\nAutores registrados");
        List<Autor> autores = autorRepository.findAutoresConLibros();
        if (autores.isEmpty()){
            System.out.println("Sin autores registrados");
            return;
        }

        for (Autor autor: autores){
            System.out.println("------------------------------------------------------------");
            System.out.printf("Autor: %s - Nacimiento: %s - Fallecido: %s%n",
                    autor.getNombre(),autor.getFechaNacimiento(), autor.getFechaFallecido());

            System.out.println("Libros:");
            autor.getLibros().forEach(libro -> System.out.printf("- %s, Idioma: %s, Descargas: %s %n",
                    libro.getTitulo(),libro.getIdioma(),libro.getNumeroDescargas()));
            System.out.println("-------------------------------------------------------------");

        }

    }

    private void listarAutoresPorAnho() {

        System.out.println("\nIngrese el año a buscar: ");
        int year = Integer.parseInt(teclado.nextLine());
        System.out.println(year);

        System.out.println("Autores vivos en: "+year);
        List<Autor> autores = autorRepository.findAll();

        int j = autores.size();
        for (int i = 0; i < j; i++) {

            int dead = Integer.parseInt(autores.get(i).getFechaFallecido());
            int born = Integer.parseInt(autores.get(i).getFechaNacimiento());

            if (year<dead&year>=born){
                System.out.println("\n----------- Resultados ---------------");
                System.out.println("Autor: "+autores.get(i).getNombre());
                System.out.println("Nacio: "+autores.get(i).getFechaNacimiento());
                System.out.println("Fallecido: "+autores.get(i).getFechaFallecido());
            }

        }

    }

    private void listarLibrosPorIdioma() {

        System.out.println("Ingrese el idioma de los libros a listar: ");
        System.out.println("""
                       
                       Español = es
                       Ingles  = en
                       Frances = fr
                       Italiano= it
                       """);
        System.out.println("Escriba es , en, fr o it. ");

        var idiomaBuscado = teclado.nextLine();

        if (!idiomaBuscado.equals("es")&&!idiomaBuscado.equals("en")&&!idiomaBuscado.equals("fr")&&!idiomaBuscado.equals("it")){
            System.out.println("Idioma no valido...");
            listarLibrosPorIdioma();

        } else {

            List<Libro> libros = libroRepository.findAll();
            List<Libro> librosBuscados = libros.stream()
                    .filter(libro -> libro.getIdioma().equalsIgnoreCase(idiomaBuscado))
                    .toList();

            System.out.println("\n*********** Filtrado por idioma: " + "[" + idiomaBuscado + "]" + " **********");
            librosBuscados.forEach(libro ->
                    System.out.printf("* %s || Autor: %s%n",libro.getTitulo(), libro.getAutor().getNombre()));
        }
    }

}
