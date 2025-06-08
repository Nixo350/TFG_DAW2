// src/main/java/com/gestion/zarpas_backend/modelo/Categoria.java
package com.gestion.zarpas_backend.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre; // Ej: "Perros", "Gatos", "Adopcion"

    @Column(length = 100)
    private String descripcion; // Ej: "Publicaciones sobre perros"

    // Relación Many-to-Many con Publicacion
    // @JsonIgnore para evitar bucles infinitos al serializar Categoria
    // Si necesitas ver las publicaciones de una categoría, tendrías que hacer un endpoint específico.
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita bucles infinitos al serializar Categoria si se cargan Publicaciones
    private Set<Publicacion> publicaciones = new HashSet<>();

    @Override
    public int hashCode() {
        return nombre != null ? nombre.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Categoria categoria = (Categoria) o;

        return nombre != null ? nombre.equals(categoria.nombre) : categoria.nombre == null;
    }
}