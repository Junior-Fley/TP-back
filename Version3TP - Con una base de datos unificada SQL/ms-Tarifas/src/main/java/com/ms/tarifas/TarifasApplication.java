package com.ms.tarifas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Tarifas
 * Gestiona los costos de traslado, combustible y estadía en depósitos
 */
@SpringBootApplication
public class TarifasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TarifasApplication.class, args);
    }
}

