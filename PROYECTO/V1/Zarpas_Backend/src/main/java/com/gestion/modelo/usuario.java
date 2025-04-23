package com.gestion.modelo;

import jakarta.persistence.*;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name= "usuario")
@Builder
public class usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id_usuario;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "contrseña")
    private String contrseña;
    @Column(name = "tipo_autenticacion")
    private Enum tipo_autenticacion;
    @Column(name = "fecha_registro")
    private Timestamp fecha_registro;
    @Column(name = "ultimo_login")
    private Timestamp ultimo_login;


    public usuario() {

    }
}
