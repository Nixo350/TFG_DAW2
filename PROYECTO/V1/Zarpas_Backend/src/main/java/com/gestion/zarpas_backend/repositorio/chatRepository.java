package com.gestion.zarpas_backend.repositorio;

import com.gestion.zarpas_backend.modelo.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface chatRepository extends JpaRepository<Chat, Long> {

}
