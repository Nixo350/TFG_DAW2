package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Chat;
import com.gestion.zarpas_backend.modelo.Mensaje;
import com.gestion.zarpas_backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByChat(Chat chat);
    List<Mensaje> findByEmisor(Usuario emisor);
}
