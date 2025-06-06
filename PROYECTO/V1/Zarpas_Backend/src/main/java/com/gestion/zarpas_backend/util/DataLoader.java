// src/main/java/com/gestion/zarpas_backend/util/DataLoader.java
package com.gestion.zarpas_backend.util;

import com.gestion.zarpas_backend.modelo.ERol; // Asegúrate de que esta enumeración existe
import com.gestion.zarpas_backend.modelo.Rol;
import com.gestion.zarpas_backend.repositorio.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica si el rol 'USER' existe, si no, lo crea
        if (rolRepository.findByNombre(ERol.USER.name()).isEmpty()) {
            Rol userRole = new Rol();
            userRole.setNombre(ERol.USER.name()); // Asume que ERol.USER.name() devuelve "USER"
            rolRepository.save(userRole);
            System.out.println("Rol 'USER' creado.");
        }

        // Verifica si el rol 'ADMIN' existe, si no, lo crea
        if (rolRepository.findByNombre(ERol.ADMIN.name()).isEmpty()) {
            Rol adminRole = new Rol();
            adminRole.setNombre(ERol.ADMIN.name()); // Asume que ERol.ADMIN.name() devuelve "ADMIN"
            rolRepository.save(adminRole);
            System.out.println("Rol 'ADMIN' creado.");
        }

        // Puedes añadir más roles si es necesario
    }
}