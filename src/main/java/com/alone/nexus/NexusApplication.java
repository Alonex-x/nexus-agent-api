package com.alone.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la API Nexus.
 * Gestiona el registro, las misiones y los eventos de los agentes de software
 * que componen el ecosistema de automatizacion del Centro de Mando.
 */
@SpringBootApplication
@EnableScheduling
public class NexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
    }
}
