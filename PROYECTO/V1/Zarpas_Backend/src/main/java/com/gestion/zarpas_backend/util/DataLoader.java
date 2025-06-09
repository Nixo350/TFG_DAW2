package com.gestion.zarpas_backend.util;

import com.gestion.zarpas_backend.modelo.ERol;
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
        if (rolRepository.findByNombre(ERol.USER.name()).isEmpty()) {
            Rol userRole = new Rol();
            userRole.setNombre(ERol.USER.name());
            rolRepository.save(userRole);
            System.out.println("Rol 'USER' creado.");
        }

        if (rolRepository.findByNombre(ERol.ADMIN.name()).isEmpty()) {
            Rol adminRole = new Rol();
            adminRole.setNombre(ERol.ADMIN.name());
            rolRepository.save(adminRole);
            System.out.println("Rol 'ADMIN' creado.");
        }

    }
}