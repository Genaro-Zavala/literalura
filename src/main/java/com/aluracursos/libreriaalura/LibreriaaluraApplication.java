package com.aluracursos.libreriaalura;

import com.aluracursos.libreriaalura.principal.Principal;
import com.aluracursos.libreriaalura.service.ConsumoAPI;
import com.aluracursos.libreriaalura.service.ConvierteDatos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
public class LibreriaaluraApplication implements CommandLineRunner {

	private final Principal principal;

    public LibreriaaluraApplication(@Lazy Principal principal) {
        this.principal = principal;
    }

    public static void main(String[] args) {
		SpringApplication.run(LibreriaaluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		principal.muestraElMenu();
	}


//	public ConsumoAPI consumoAPI(){
//		return new ConsumoAPI();
//	}
//
//
//	public ConvierteDatos conversor(){
//		return new ConvierteDatos();
//	}
//
//	public Principal getPrincipal(){
//		return principal;
//	}




}
